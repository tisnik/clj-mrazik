(ns clj-mrazik.config)

(require '[clojure-ini.core :as clojure-ini])

(defn parse-int
    [string]
    (java.lang.Integer/parseInt string))

(defn parse-hh-mm-time-as-minutes
    [string]
    (let [time (-> (java.text.SimpleDateFormat. "HH:mm")
               (.parse string))]
         (+ (.getMinutes time)
            (* 60 (.getHours time)))))

(defn load-configuration
    "Load configuration from the provided INI file."
    [ini-file-name]
    (-> (clojure-ini/read-ini ini-file-name :keywordize? true)
        (update-in [:server :port] parse-int)
        (update-in [:bot :start]     parse-hh-mm-time-as-minutes)
        (update-in [:bot :end]       parse-hh-mm-time-as-minutes)
        (update-in [:bot :frequency] parse-hh-mm-time-as-minutes)
        (update-in [:bot :duration]  parse-hh-mm-time-as-minutes)))

