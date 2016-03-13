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

(ns clj-mrazik.config)

(require '[clojure.pprint :as pprint])

(require '[clojure-ini.core :as clojure-ini])

(defn parse-int
    "Parse the given string as an integer number."
    [string]
    (java.lang.Integer/parseInt string))

(defn parse-hh-mm-time-as-minutes
    "Parse the given string that has to have format 'HH:mm' and returns
     the time in minutes."
    [string]
    (let [time (-> (java.text.SimpleDateFormat. "HH:mm")
               (.parse string))]
         (+ (.getMinutes time)
            (* 60 (.getHours time)))))

(defn update-server-configuration
    "Update server configuration from the loaded data."
    [configuration]
    (update-in configuration [:server :port] parse-int))

(defn update-bot-configuration
    "Update bot configuration from the loaded data."
    [configuration]
    (-> configuration
        (update-in [:bot :start]     parse-hh-mm-time-as-minutes)
        (update-in [:bot :end]       parse-hh-mm-time-as-minutes)
        (update-in [:bot :frequency] parse-hh-mm-time-as-minutes)
        (update-in [:bot :duration]  parse-hh-mm-time-as-minutes)))

(defn load-configuration
    "Load configuration from the provided INI file."
    [ini-file-name]
    (-> (clojure-ini/read-ini ini-file-name :keywordize? true)
        update-server-configuration
        update-bot-configuration))

(defn print-configuration
    "Print actual configuration to the output."
    [configuration]
    (pprint/pprint configuration))
