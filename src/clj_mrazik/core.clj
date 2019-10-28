;
;  (C) Copyright 2016, 2017  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns clj-mrazik.core
  (:gen-class))

(require '[clojure.pprint        :as pprint])
(require '[clj-calendar.calendar :as calendar])

(require '[clojure.tools.cli     :as cli])
(require '[clojure.tools.logging :as log])

(require '[clj-mrazik.config     :as config])
(require '[clj-mrazik.schedule   :as schedule])
(require '[clj-mrazik.irc-bot    :as irc-bot])
(require '[clj-mrazik.dyncfg     :as dyncfg])
(require '[clj-mrazik.importer   :as importer])

(def cli-options
    "Definitions of all command line options that are  currenty supported."
    ;; an option with a required argument
    [["-h"   "--help"                       "show help"                   :id :help]
     ["-i"   "--import datafile.csv"        "import data into dictionary" :id :import]])

(defn between?
    [schedule-item minutes]
    (and (>= minutes (-> schedule-item :minutes :from))
         (<= minutes (-> schedule-item :minutes :to))))

(defn in-schedule?
    [schedule minutes]
    (if (some #(between? % minutes) schedule)
        :should-be-open
        :should-be-closed))

; TODO refactor following four functions!

(defn open-window
    [config]
    (let [message    (-> config :bot    :message1)
          recipients (-> config :server :recipients)
          channel    (-> config :server :channel)]
          (println message)
          (irc-bot/send-message recipients channel message)))

(defn close-window
    [config]
    (let [message    (-> config :bot    :message2)
          recipients (-> config :server :recipients)
          channel    (-> config :server :channel)]
          (println message)
          (irc-bot/send-message recipients channel message)))

(defn sunrise
    [config]
    (let [message    "sunrise!"
          recipients (-> config :server :recipients)
          channel    (-> config :server :channel)]
          (irc-bot/send-message recipients channel message)))

(defn sunset
    [config]
    (let [message    "sunset"
          recipients (-> config :server :recipients)
          channel    (-> config :server :channel)]
          (irc-bot/send-message recipients channel message)))

(defn is-sunrise?
    [geolocation cal minutes]
    (let [sunrise     (schedule/get-sunrise geolocation cal)
          sunrise-min (config/parse-hh-mm-time-as-minutes sunrise)]
          (= sunrise-min minutes)))

(defn is-sunset?
    [geolocation cal minutes]
    (let [sunset     (schedule/get-sunset geolocation cal)
          sunset-min (config/parse-hh-mm-time-as-minutes sunset)]
          (= sunset-min minutes)))

(defn run-bot
    [config schedule]
    (loop [status :window-closed]
        (Thread/sleep dyncfg/sleep-amount)
        (let [cal            (calendar/get-calendar)
              minutes        (calendar/minute-of-day)
              actual         (in-schedule? schedule minutes)
              sunrise?       (is-sunrise? (:geolocation config) cal minutes)
              sunset?        (is-sunset?  (:geolocation config) cal minutes)]
            (println "minutes: " minutes status actual sunrise? sunset?)
            (if sunrise? (sunrise config))
            (if sunset?  (sunset config))
            (condp = [status actual]
                [:window-closed :should-be-closed] (recur :window-closed)
                [:window-closed :should-be-open]   (do (open-window config)  (recur :window-open))
                [:window-open   :should-be-closed] (do (close-window config) (recur :window-closed))
                [:window-open   :should-be-open]   (recur :window-open))
        )))

(defn start-bot
    []
    (let [config          (config/load-configuration "config.ini")
          actual-schedule (schedule/compute-schedule (:bot config))]
         (reset! dyncfg/schedule      actual-schedule)
         (reset! dyncfg/configuration config)
         (config/print-configuration config)
         (pprint/pprint @dyncfg/schedule)
         (irc-bot/start-irc-bot (:server config))
         (irc-bot/send-message (-> config :server :recipients) (-> config :server :channel) "Hi!")
         (if (or (-> config :module :sunrise-sunset)
                 (-> config :modules :scheduler))
             (run-bot config @dyncfg/schedule))))

; TODO
; schedule displaying
; user registration/unregistration

(defn show-help
    "Show help and all supported CLI flags."
    [summary]
    (println "Usage:")
    (println summary))

(defn run-app
    [summary show-help? import?]
    (cond show-help? (show-help summary)
          import?    (importer/import-data import?)
          :else      (start-bot)))

(defn -main
    "Entry point to this bot."
    [& args]
    (log/info "Starting the application")
    (let [all-options (cli/parse-opts args cli-options)
          options     (all-options :options)
          show-help?  (options :help)
          import?     (options :import)
          summary     (:summary all-options)]
          (run-app summary show-help? import?))
    (log/info "Exiting from the main function"))

