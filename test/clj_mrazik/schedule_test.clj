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

(ns clj-mrazik.schedule-test
  (:require [clojure.test :refer :all]
            [clj-mrazik.schedule :refer :all]))

;
; Common functions used by tests.
;

(defn callable?
    "Test if given function-name is bound to the real function."
    [function-name]
    (clojure.test/function? function-name))

;
; Tests for various functions
;

(deftest test-format-schedule-time-existence
    "Check that the clj-mrazik.schedule/format-schedule-time definition exists."
    (testing "if the clj-mrazik.schedule/format-schedule-time definition exists."
        (is (callable? 'clj-mrazik.schedule/format-schedule-time))))


(deftest test-schedule-entry-existence
    "Check that the clj-mrazik.schedule/schedule-entry definition exists."
    (testing "if the clj-mrazik.schedule/schedule-entry definition exists."
        (is (callable? 'clj-mrazik.schedule/schedule-entry))))


(deftest test-compute-schedule-existence
    "Check that the clj-mrazik.schedule/compute-schedule definition exists."
    (testing "if the clj-mrazik.schedule/compute-schedule definition exists."
        (is (callable? 'clj-mrazik.schedule/compute-schedule))))


(deftest test-get-sunset-existence
    "Check that the clj-mrazik.schedule/get-sunset definition exists."
    (testing "if the clj-mrazik.schedule/get-sunset definition exists."
        (is (callable? 'clj-mrazik.schedule/get-sunset))))


(deftest test-get-sunrise-existence
    "Check that the clj-mrazik.schedule/get-sunrise definition exists."
    (testing "if the clj-mrazik.schedule/get-sunrise definition exists."
        (is (callable? 'clj-mrazik.schedule/get-sunrise))))

