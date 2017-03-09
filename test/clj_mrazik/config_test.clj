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

(ns clj-mrazik.config-test
  (:require [clojure.test :refer :all]
            [clj-mrazik.config :refer :all]))

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

(deftest test-parse-int-existence
    "Check that the clj-mrazik.config/parse-int definition exists."
    (testing "if the clj-mrazik.config/parse-int definition exists."
        (is (callable? 'clj-mrazik.config/parse-int))))


(deftest test-parse-float-existence
    "Check that the clj-mrazik.config/parse-float definition exists."
    (testing "if the clj-mrazik.config/parse-float definition exists."
        (is (callable? 'clj-mrazik.config/parse-float))))


(deftest test-parse-boolean-existence
    "Check that the clj-mrazik.config/parse-boolean definition exists."
    (testing "if the clj-mrazik.config/parse-boolean definition exists."
        (is (callable? 'clj-mrazik.config/parse-boolean))))


(deftest test-parse-hh-mm-time-as-minutes-existence
    "Check that the clj-mrazik.config/parse-hh-mm-time-as-minutes definition exists."
    (testing "if the clj-mrazik.config/parse-hh-mm-time-as-minutes definition exists."
        (is (callable? 'clj-mrazik.config/parse-hh-mm-time-as-minutes))))


(deftest test-update-server-configuration-existence
    "Check that the clj-mrazik.config/update-server-configuration definition exists."
    (testing "if the clj-mrazik.config/update-server-configuration definition exists."
        (is (callable? 'clj-mrazik.config/update-server-configuration))))


(deftest test-update-bot-configuration-existence
    "Check that the clj-mrazik.config/update-bot-configuration definition exists."
    (testing "if the clj-mrazik.config/update-bot-configuration definition exists."
        (is (callable? 'clj-mrazik.config/update-bot-configuration))))


(deftest test-update-geolocation-configuration-existence
    "Check that the clj-mrazik.config/update-geolocation-configuration definition exists."
    (testing "if the clj-mrazik.config/update-geolocation-configuration definition exists."
        (is (callable? 'clj-mrazik.config/update-geolocation-configuration))))


(deftest test-update-modules-configuration-existence
    "Check that the clj-mrazik.config/update-modules-configuration definition exists."
    (testing "if the clj-mrazik.config/update-modules-configuration definition exists."
        (is (callable? 'clj-mrazik.config/update-modules-configuration))))


(deftest test-load-configuration-existence
    "Check that the clj-mrazik.config/load-configuration definition exists."
    (testing "if the clj-mrazik.config/load-configuration definition exists."
        (is (callable? 'clj-mrazik.config/load-configuration))))


(deftest test-print-configuration-existence
    "Check that the clj-mrazik.config/print-configuration definition exists."
    (testing "if the clj-mrazik.config/print-configuration definition exists."
        (is (callable? 'clj-mrazik.config/print-configuration))))

