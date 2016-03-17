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

(ns clj-mrazik.core
  (:gen-class))

(require '[clojure.pprint :as pprint])
(require '[clj-calendar.calendar :as calendar])

(require '[irclj.core :as irc])

(require '[clj-mrazik.config   :as config])
(require '[clj-mrazik.schedule :as schedule])
(require '[clj-mrazik.irc-bot  :as irc-bot])

(def sleep-amount
    10000)

(defn between?
    [schedule-item minutes]
    (and (>= minutes (-> schedule-item :minutes :from))
         (<= minutes (-> schedule-item :minutes :to))))

(defn in-schedule?
    [schedule minutes]
    (if (some #(between? % minutes) schedule)
        :should-be-open
        :should-be-closed))

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

(defn run-bot
    [config schedule]
    (loop [status :window-closed]
        (Thread/sleep sleep-amount)
        (let [minutes (calendar/minute-of-day)
              actual  (in-schedule? schedule minutes)]
            (println "minutes: " minutes status actual)
            (condp = [status actual]
                [:window-closed :should-be-closed] (recur :window-closed)
                [:window-closed :should-be-open]   (do (open-window config)  (recur :window-open))
                [:window-open   :should-be-closed] (do (close-window config) (recur :window-closed))
                [:window-open   :should-be-open]   (recur :window-open))
        )))

; TODO
; schedule displaying
; user registration/unregistration

(defn -main
    "Entry point to this bot."
    [& args]
    (let [config   (config/load-configuration "config.ini")
          schedule (schedule/compute-schedule (:bot config))]
         (config/print-configuration config)
         (pprint/pprint schedule)
         (println (schedule/get-sunrise (:geolocation config)))
         (println (schedule/get-sunset (:geolocation config)))
         ;(irc-bot/start-irc-bot (:server config))
         ;(irc-bot/send-message (-> config :server :recipients) (-> config :server :channel) "Hi!")
         ;(run-bot config schedule)
    ))

