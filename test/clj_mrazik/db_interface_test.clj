;
;  (C) Copyright 2017  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns clj-mrazik.db-interface-test
  (:require [clojure.test :refer :all]
            [clj-mrazik.db-interface :refer :all]))

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

(deftest test-select-word-class-id-existence
    "Check that the clj-mrazik.db-interface/select-word-class-id definition exists."
    (testing "if the clj-mrazik.db-interface/select-word-class-id definition exists."
        (is (callable? 'clj-mrazik.db-interface/select-word-class-id))))


(deftest test-select-source-id-existence
    "Check that the clj-mrazik.db-interface/select-source-id definition exists."
    (testing "if the clj-mrazik.db-interface/select-source-id definition exists."
        (is (callable? 'clj-mrazik.db-interface/select-source-id))))


(deftest test-insert-word-into-dictionary-existence
    "Check that the clj-mrazik.db-interface/insert-word-into-dictionary definition exists."
    (testing "if the clj-mrazik.db-interface/insert-word-into-dictionary definition exists."
        (is (callable? 'clj-mrazik.db-interface/insert-word-into-dictionary))))


(deftest test-select-words-existence
    "Check that the clj-mrazik.db-interface/select-words definition exists."
    (testing "if the clj-mrazik.db-interface/select-words definition exists."
        (is (callable? 'clj-mrazik.db-interface/select-words))))


(deftest test-select-word-count-existence
    "Check that the clj-mrazik.db-interface/select-word-count definition exists."
    (testing "if the clj-mrazik.db-interface/select-word-count definition exists."
        (is (callable? 'clj-mrazik.db-interface/select-word-count))))


(deftest test-all-words-count-existence
    "Check that the clj-mrazik.db-interface/all-words-count definition exists."
    (testing "if the clj-mrazik.db-interface/all-words-count definition exists."
        (is (callable? 'clj-mrazik.db-interface/all-words-count))))

