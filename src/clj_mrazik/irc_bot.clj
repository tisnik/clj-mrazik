;
;  (C) Copyright 2017  Pavel Tisnovsky
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

(require '[clojure.tools.logging :as log])

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
    (let [output (into [] (load-data-file filename))]
        (println "Read" (count output) "lines from" filename)
        output))

(def verbs                   (atom nil))
(def adverbs                 (atom nil))
(def adjectives              (atom nil))
(def nouns                   (atom nil))
(def congrat-messages        (atom nil))
(def happy-birthday-messages (atom nil))
(def quotes                  (atom nil))
(def papalas                 (atom nil))
(def soviet1                 (atom nil))
(def soviet2                 (atom nil))
(def soviet3                 (atom nil))
(def soviet4                 (atom nil))
(def czech1                  (atom nil))
(def czech2                  (atom nil))
(def czech3                  (atom nil))
(def czech4                  (atom nil))


(defn load-all-data-files
    []
    (reset! verbs                   (load-vocabulary "data/verbs.txt"))
    (reset! adverbs                 (load-vocabulary "data/adverbs.txt"))
    (reset! adjectives              (load-vocabulary "data/adjectives.txt"))
    (reset! nouns                   (load-vocabulary "data/nouns.txt"))
    (reset! congrat-messages        (load-vocabulary "data/congratulations.txt"))
    (reset! happy-birthday-messages (load-vocabulary "data/happy-birthday.txt"))
    (reset! quotes                  (load-vocabulary "data/quotes.txt"))
    (reset! papalas                 (load-vocabulary "data/papalas.txt"))
    (reset! czech1                  (load-vocabulary "data/czech1.txt"))
    (reset! czech2                  (load-vocabulary "data/czech2.txt"))
    (reset! czech3                  (load-vocabulary "data/czech3.txt"))
    (reset! czech4                  (load-vocabulary "data/czech4.txt"))
    (reset! soviet1                 (load-vocabulary "data/soviet1.txt"))
    (reset! soviet2                 (load-vocabulary "data/soviet2.txt"))
    (reset! soviet3                 (load-vocabulary "data/soviet3.txt"))
    (reset! soviet4                 (load-vocabulary "data/soviet4.txt")))

(load-all-data-files)

(defn message-to-channel?
    [message]
    (.startsWith (:target message) "#"))

(defn message-for-me?
    [my-name special-prefix message]
    (or (.startsWith (:target message) my-name)        ; private message
        (.startsWith (:text message) (str my-name ":")); direct message
        (.startsWith (:text message) (str my-name ",")); direct message
        (and (.startsWith (:text message) special-prefix)   ; special prefix
             (not (= (.trim (:text message)) special-prefix)))
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
    (str (rand-nth @adverbs)
     " " (rand-nth @verbs)
     " " (rand-nth @adjectives)
     " " (rand-nth @nouns)))

 (defn say-quote
    [& quotes]
    (clojure.string/join " "
                         (map (fn [quote] (rand-nth (deref quote))) quotes)))

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

(defn find-word-in-known-responses
    [input]
    (let [responses (-> @dyncfg/configuration :responses)]
        (responses (.toLowerCase input))))

(defn is-word-from-known-responses?
    [input]
    (find-word-in-known-responses input))

(defn return-known-response
    [input]
    (find-word-in-known-responses input))

(defn is-word-from-dictionary?
    [input]
    (try
        (dictionary/word-exist? input)
        (catch java.sql.SQLException e
            (log/error e "is-word-from-dictionary?")
            false)))

(defn return-words-from-dictionary
    [input]
    (dictionary/find-word input))

(defn use-wildchars?
    [input]
    (or (.startsWith input "*")
        (.endsWith input "*")))

(defn one-word-like-this?
    [input]
    (if (use-wildchars? input)
        (= (dictionary/words-like-this input) 1)))

(defn more-words-like-this?
    [input]
    (if (use-wildchars? input)
        (> (dictionary/words-like-this input) 1)))

(defn return-word-like-this
    [input]
    (dictionary/find-word-like-this input))

(defn return-more-words-like-this
    [input]
    (dictionary/find-words-like-this input))

(defn dictionary-status
    []
    (try
        (str "Number of terms in dictionary: " (dictionary/term-count))
        (catch java.sql.SQLException e
            (log/error e "dictionary-status")
            "Can not access database")))

(defn get-nick
    [prefix input]
    (-> input (subs (count prefix)) clojure.string/trim))

(defn congrats?
    [input]
    (re-matches #"congrats [a-zA-Z0-9]+" input))

(defn congratulate-to
    [nick]
    (let [message (rand-nth @congrat-messages)]
       (format message nick)))

(defn congratulate
    [input]
    (let [nick (get-nick "congrats" input)]
        (congratulate-to nick)))

(defn happy-birthday-to
    [nick]
    (let [message (rand-nth @happy-birthday-messages)]
       (format message nick)))

(defn happy-birthday
    [input]
    (let [nick (get-nick "happy birthday" input)]
        (happy-birthday-to nick)))

(defn happy-birthday?
    [input]
    (re-matches #"happy birthday [a-zA-Z0-9]+" input))

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
                          (if (.startsWith input-text (-> @dyncfg/configuration :bot :prefix))
                              (subs input-text 1)
                              (subs input-text (+ 2 (count @dyncfg/bot-nick))))
                          input-text)
          prefix      (if in-channel? (str nick ": "))
          response    (condp = input
                          "help"     (-> @dyncfg/configuration :bot :help)
                          "status"   (dictionary-status)
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
                          "ms"       "The current Microsoft is *NOTHING* like the old Microsoft. They have excellent people who really get Open Source. There's no reason to stop using github except outdated and outmoded feelings from yesteryear. The Microsoft of 2018 are our friends. They're not the Borg. They're not out to Embrace, Extend, Extinguish all we hold dear."
                          "quote"    (say-quote quotes)
                          "hlaska"   (say-quote quotes)
                          "papalas"  (say-quote papalas)
                          "papaláš"  (say-quote papalas)
                          "совет"    (say-quote soviet1 soviet2 soviet3 soviet4)
                          "sg"       (say-quote czech1 czech2 czech3 czech4)
                          "fix the Universe" "linux++ trello-- Mojo-- Blue.Jeans-- BlueJeans-- asciidoc-- docbook++"
                          "rainbow"   (apply str (for [color (range 16)]
                                                     (str (char 3) (format "%02d" color) (format "test%02d " color) (char 3) "99")))
                          (cond
                              (and number-cruncher? (is-number? input))               (print-prime-factors input)
                              (and number-cruncher? (is-two-numbers? input))          (print-gcd input)
                              (and number-cruncher? (is-factorial? input))            (print-factorial input)
                              (and s-expressions?   (is-s-expression? input))         (s-expression input)
                              (congrats? input)                                       (congratulate input)
                              (happy-birthday? input)                                 (happy-birthday input)
                              (is-word-from-known-responses? input) (return-known-response input)
                              (is-word-from-dictionary? input) (return-words-from-dictionary input)
                              (one-word-like-this? input)      (return-word-like-this input)
                              (more-words-like-this? input)    (return-more-words-like-this input)
                              :else (if random-messages? (random-message)
                                                         "Command not understood or term not found")))]
        {:prefix prefix
         :response response})
        (catch Exception e
            (log/error e "prepare-reply-text")
            {:prefix ""
             :response ""})))

(defn on-incoming-message
    [connection incoming-message]
    (let [{text    :text
           target  :target
           nick    :nick
           host    :host
           command :command} incoming-message]
           (log/info (str "Received message from" nick "to" target ":" text "(" host command ")"))
           (log/info incoming-message)
           (if (message-for-me? @dyncfg/bot-nick (-> @dyncfg/configuration :bot :prefix) incoming-message)
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
    (let [server   (:name configuration)
          port     (:port configuration)
          channels (:channels configuration)
          chanlist (clojure.string/split channels #" ")
          nick     (:nick configuration)]
        (log/info "Connecting to" server "on port" port)
        (let [conn (irc/connect server port nick
                                :callbacks {:privmsg on-incoming-message})]
            (log/info "Connected, joining to channels" channels)
            (reset! dyncfg/connection conn)
            (reset! dyncfg/bot-nick nick)
            (doseq [channel chanlist]
                (log/info channel)
                (irc/join @dyncfg/connection (clojure.string/trim channel)))
            (log/info "Connected..."))))

