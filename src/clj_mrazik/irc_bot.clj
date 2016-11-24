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

(defn print-prime-factors
    [input]
    (str input " = " (clojure.string/join " x " (primefactors (Integer/parseInt input)))))

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

(defn prepare-reply-text
    [incomming-message nick input-text]
    (let [in-channel? (message-to-channel? incomming-message)
          input       (if in-channel?
                          (subs input-text (+ 2 (count @dyncfg/bot-nick)))
                          input-text)
          prefix      (if in-channel? (str nick ": "))
          response    (condp = input
                          "help" "commands: schedule users time sunrise sunset"
                          "schedule" (format-schedule @dyncfg/schedule)
                          "users"    (-> @dyncfg/configuration :server :recipients)
                          "time"     (calendar/format-time (calendar/get-calendar))
                          "sunrise"  (schedule/get-sunrise (:geolocation @dyncfg/configuration))
                          "sunset"   (schedule/get-sunset  (:geolocation @dyncfg/configuration))
                          "die"      "thanks for your feedback, I appreciate it"
                          "Good bot" "I know"
                          "Good bot." "I know"
                          (condp
                              (is-number? input)      (print-prime-factors input)
                              (is-two-numbers? input) (print-gcd input)
                              (is-factorial? input)   (print-factorial input)
                                                      (random-message)))]
        (str prefix response)))

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
               (irc/reply connection (create-reply incoming-message)
                                     (prepare-reply-text incoming-message nick text)))))

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

