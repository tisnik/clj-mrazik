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

(ns clj-mrazik.schedule)

(require '[clojure.pprint :as pprint])
(require '[clj-calendar.calendar :as calendar])
(require '[sunrise.core :as sunrise])

(defn format-schedule-time
    [time]
    (calendar/format-time
    (doto (calendar/get-calendar)
          (.set java.util.Calendar/HOUR_OF_DAY 0)
          (.set java.util.Calendar/SECOND 0)
          (.set java.util.Calendar/MINUTE time))))

(defn schedule-entry
    [time duration]
    (let [to (+ time duration)]
        {:minutes {:from time
                   :to   to}
         :formatted {:from (format-schedule-time time)
                     :to   (format-schedule-time to)}}
    ))

(defn compute-schedule
    [bot-configuration]
    (let [start     (:start bot-configuration)
          end       (:end   bot-configuration)
          frequency (:frequency bot-configuration)
          duration  (:duration  bot-configuration)]
          (loop [schedule [] time start]
              (if (>= time end)
                  schedule
                  (recur (conj schedule (schedule-entry time duration)) (+ time frequency))))))

(defn get-sunset
    ([geolocation]
     (get-sunset geolocation (calendar/get-calendar)))
    ([geolocation cal]
     (let [day   (calendar/get-day cal)
           month (calendar/get-month cal)
           year  (calendar/get-year cal)]
         (sunrise/setting-time {:day   day
                                :month month
                                :year  year
                                :latitude  (:latitude  geolocation)
                                :longitude (:longitude geolocation)
                                :local-offset 1}))))

(defn get-sunrise
    ([geolocation]
     (get-sunrise geolocation (calendar/get-calendar)))
    ([geolocation cal]
     (let [day   (calendar/get-day cal)
           month (calendar/get-month cal)
           year  (calendar/get-year cal)]
         (sunrise/rising-time {:day   day
                               :month month
                               :year  year
                               :latitude  (:latitude  geolocation)
                               :longitude (:longitude geolocation)
                               :local-offset 1}))))

