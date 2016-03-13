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

(ns clj-mrazik.schedule
  (:gen-class))

(require '[clojure.pprint :as pprint])

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

