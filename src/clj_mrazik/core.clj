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

(require '[irclj.core :as irc])

(require '[clj-mrazik.config   :as config])
(require '[clj-mrazik.schedule :as schedule])
(require '[clj-mrazik.irc-bot  :as irc-bot])

(defn -main
    "Entry point to this bot."
    [& args]
    (let [config   (config/load-configuration "config.ini")
          schedule (schedule/compute-schedule (:bot config))]
         (config/print-configuration config)
         (pprint/pprint schedule)
         (irc-bot/start-irc-bot (:server config))
    ))

