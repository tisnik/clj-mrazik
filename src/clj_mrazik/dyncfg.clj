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

(ns clj-mrazik.dyncfg)

(def schedule
    (atom nil))

(def configuration
    (atom nil))

(def sleep-amount
    10000)

(def connection
    (atom nil))

(def bot-nick
    (atom nil))

