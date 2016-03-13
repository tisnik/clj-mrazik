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

(defproject clj-mrazik "0.1.0-SNAPSHOT"
    :description "FIXME: write description"
    :url "http://example.com/FIXME"
    :license {:name "Eclipse Public License"
              :url "http://www.eclipse.org/legal/epl-v10.html"}
    :dependencies [[org.clojure/clojure "1.7.0"]
                   [irclj "0.5.0-alpha4"]
                   [clojure-ini "0.0.1"]
                   [org.clojars.tisnik/clj-calendar "0.1.0-SNAPSHOT"]]
    :main ^:skip-aot clj-mrazik.core
    :target-path "target/%s"
    :plugins [[test2junit "1.1.0"]
              [codox "0.8.11"]
              [lein-cloverage "1.0.6"]]
    :profiles {:uberjar {:aot :all}})

