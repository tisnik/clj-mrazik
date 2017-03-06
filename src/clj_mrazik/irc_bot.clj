;
;  (C) Copyright 2016  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns clj-mrazik.irc-bot)

(require '[irclj.core :as irc])
(require '[clj-calendar.calendar :as calendar])

(require '[clj-mrazik.core     :as core])
(require '[clj-mrazik.dyncfg   :as dyncfg])
(require '[clj-mrazik.schedule :as schedule])
(require '[clj-mrazik.dictionary :as dictionary])

(defn load-data-file
    [filename]
    (-> filename
        slurp
        clojure.string/split-lines))

(defn load-vocabulary
    [filename]
    (into [] (load-data-file filename)))

(def verbs      (load-vocabulary "data/verbs.txt"))
(def adverbs    (load-vocabulary "data/adverbs.txt"))
(def adjectives (load-vocabulary "data/adjectives.txt"))
(def nouns      (load-vocabulary "data/nouns.txt"))

(defn message-to-channel?
    [message]
    (.startsWith (:target message) "#"))

(defn message-for-me?
    [my-name message]
    (or (.startsWith (:target message) my-name)        ; private message
        (.startsWith (:text message) (str my-name ":")); direct message
    ))

(defn create-reply
    [incoming-message]
    (if (message-to-channel? incoming-message)
        incoming-message
        (assoc incoming-message :target (:nick incoming-message))))

(defn format-schedule
    [schedule]
    (apply str (for [s schedule]
       ;(str (subs (-> s :formatted :from) 0 5) "-" (subs (-> s :formatted :to) 0 5) ", "))))
       (str (subs (-> s :formatted :from) 0 5) "  "))))

(defn random-message
    []
    (str (rand-nth adverbs)
     " " (rand-nth verbs)
     " " (rand-nth adjectives)
     " " (rand-nth nouns)))

(defn primefactors 
    ([n] 
        (primefactors n 2 '()))
    ([n candidate accumulator]
        (cond (<= n 1) (reverse accumulator)
              (zero? (rem n candidate)) (recur (/ n candidate) candidate (cons candidate accumulator))
              :else (recur n (inc candidate) accumulator))))

(defn factorial
    [n]
    (reduce *' (range 1 (inc n))))

(defn gcd
    [x y]
    (if (zero? y)
        x
        (recur y (mod x y))))

(defn print-factorial
    [input]
    (factorial (Integer/parseInt (subs input 0 (dec (count input))))))

(defn s-expression
    [input]
    (try
        (eval (read-string input))
        (catch Exception e "sorry vole, error")))

(defn is-s-expression?
    [input]
    (and (.startsWith input "(") (.endsWith input ")")))

(defn print-prime-factors
    [input]
    (let [factors (primefactors (Integer/parseInt input))]
        (if (<= (count factors) 1)
            (str input " is a prime number")
            (str input " = " (clojure.string/join " x " factors)))))

(defn print-gcd
    [input]
    (let [splitted (re-find #"([0-9]+)\s+([0-9]+)" input)
          x (Integer/parseInt (nth splitted 1))
          y (Integer/parseInt (nth splitted 2))]
        (str "gcd(" x "," y ") = " (gcd x y))))

(defn is-number?
    [input]
    (re-matches #"[0-9]+" input))

(defn is-two-numbers?
    [input]
    (re-matches #"[0-9]+\s+[0-9]+" input))

(defn is-factorial?
    [input]
    (re-matches #"[0-9]+!" input))

(defn is-word-from-dictionary?
    [input]
    (dictionary/word-exist? input))

(defn return-words-from-dictionary
    [input]
    (dictionary/find-word input))

(defn prepare-reply-text
    [incomming-message nick input-text]
    (try
    (let [in-channel? (message-to-channel? incomming-message)
          modules     (-> @dyncfg/configuration :modules)
          number-cruncher? (:number-cruncher modules)
          s-expressions?   (:s-expressions modules)
          dictionary?      (:dictionary modules)
          random-messages? (:random-messages modules)
          input       (if in-channel?
                          (subs input-text (+ 2 (count @dyncfg/bot-nick)))
                          input-text)
          prefix      (if in-channel? (str nick ": "))
          response    (condp = input
                          "help" "commands: schedule users time sunrise sunset"
                          "schedule" (if (:scheduler modules)
                                         (format-schedule @dyncfg/schedule)
                                         "scheduler is disabled")
                          "users"    (-> @dyncfg/configuration :server :recipients)
                          "time"     (calendar/format-time (calendar/get-calendar))
                          "sunrise"  (schedule/get-sunrise (:geolocation @dyncfg/configuration))
                          "sunset"   (schedule/get-sunset  (:geolocation @dyncfg/configuration))
                          "die"      "thanks for your feedback, I appreciate it"
                          "Good bot" "I know"
                          "Good bot." "I know"
                          "rainbow"   (apply str (for [color (range 16)]
                                                     (str (char 3) (format "%02d" color) (format "test%02d " color) (char 3) "99")))
                          (cond
                              (and number-cruncher? (is-number? input))               (print-prime-factors input)
                              (and number-cruncher? (is-two-numbers? input))          (print-gcd input)
                              (and number-cruncher? (is-factorial? input))            (print-factorial input)
                              (and s-expressions?   (is-s-expression? input))         (s-expression input)
                              (and dictionary?      (is-word-from-dictionary? input)) (return-words-from-dictionary input)
                              :else (if random-messages? (random-message)
                                                         "Command not understood or term not found")))]
        {:prefix prefix
         :response response})
        (catch Exception e
            (println (.getMessage e))
            {:prefix ""
             :response ""})))

(defn on-incoming-message
    [connection incoming-message]
    (let [{text    :text
           target  :target
           nick    :nick
           host    :host
           command :command} incoming-message]
           (println "Received message from" nick "to" target ":" text "(" host command ")")
           (println incoming-message)
           (if (message-for-me? @dyncfg/bot-nick incoming-message)
               (let [reply  (create-reply incoming-message)
                     output (prepare-reply-text incoming-message nick text)]
                     (if (seq? (:response output))
                         (doseq [r (:response output)]
				 (irc/reply connection reply
					  (str (:prefix output) r)))
		         (irc/reply connection reply
				  (str (:prefix output) (:response output))
))))))

(defn send-message
    [recipients target message-text]
    (let [message {:target target :command "PRIVMSG"}]
        (irc/reply @dyncfg/connection message (str recipients " " message-text))))

(defn start-irc-bot
    [configuration]
    (let [server  (:name configuration)
          port    (:port configuration)
          channel (:channel configuration)
          nick    (:nick configuration)]
        (println "Connecting to" server "on port" port)
        (let [conn (irc/connect server port nick
                                :callbacks {:privmsg on-incoming-message})]
            (println "Connected, joining to channel" channel)
            (reset! dyncfg/connection conn)
            (reset! dyncfg/bot-nick nick)
            (irc/join @dyncfg/connection channel)
            (println "Connected..."))))

