(ns clj-mrazik.core
  (:gen-class))

(require '[clojure.pprint :as pprint])

(require '[irclj.core :as irc])

(require '[clj-mrazik.config :as config])

(defn -main
    "Entry point to this bot."
    [& args]
    (pprint/pprint (config/load-configuration "config.ini")))

