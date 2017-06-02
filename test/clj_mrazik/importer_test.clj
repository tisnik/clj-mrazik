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

(ns clj-mrazik.importer-test
  (:require [clojure.test            :refer :all]
            [clj-mrazik.importer     :refer :all]
            [clj-mrazik.db-interface :as db-interface]))

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

(deftest test-read-csv-existence
    "Check that the clj-mrazik.importer/read-csv definition exists."
    (testing "if the clj-mrazik.importer/read-csv definition exists."
        (is (callable? 'clj-mrazik.importer/read-csv))))


(deftest test-parse-boolean-existence
    "Check that the clj-mrazik.importer/parse-boolean definition exists."
    (testing "if the clj-mrazik.importer/parse-boolean definition exists."
        (is (callable? 'clj-mrazik.importer/parse-boolean))))


(deftest test-parse-use-it-existence
    "Check that the clj-mrazik.importer/parse-use-it definition exists."
    (testing "if the clj-mrazik.importer/parse-use-it definition exists."
        (is (callable? 'clj-mrazik.importer/parse-use-it))))


(deftest test-get-word-class-existence
    "Check that the clj-mrazik.importer/get-word-class definition exists."
    (testing "if the clj-mrazik.importer/get-word-class definition exists."
        (is (callable? 'clj-mrazik.importer/get-word-class))))


(deftest test-get-source-existence
    "Check that the clj-mrazik.importer/get-source definition exists."
    (testing "if the clj-mrazik.importer/get-source definition exists."
        (is (callable? 'clj-mrazik.importer/get-source))))


(deftest test-import-data-existence
    "Check that the clj-mrazik.importer/import-data definition exists."
    (testing "if the clj-mrazik.importer/import-data definition exists."
        (is (callable? 'clj-mrazik.importer/import-data))))


(deftest test-parse-boolean
    "Check the function parse-boolean."
    (testing "parse-boolean"
        (is (= 1 (parse-boolean "yes")))
        (is (= 1 (parse-boolean "Yes")))
        (is (= 0 (parse-boolean "no")))
        (is (= 0 (parse-boolean "No")))))

(deftest test-use-it
    "Check the function use-it"
    (testing "use-it"
        (is (= 1 (parse-use-it "yes")))
        (is (= 1 (parse-use-it "Yes")))
        (is (= 0 (parse-use-it "no")))
        (is (= 0 (parse-use-it "No")))
        (is (= 2 (parse-use-it "with caution")))))

(deftest test-get-word-class
    "Check the function get-word-class"
    (testing "Function get-word-class"
        (with-redefs [db-interface/select-word-class-id (fn [input] "verb")]
            (is (nil? (get-word-class nil)))
            (is (= "verb" (get-word-class 1))))))

(deftest test-get-word-class-exception-handling
    "Check the function get-word-class"
    (testing "Function get-word-class"
        (with-redefs [db-interface/select-word-class-id (fn [input] (throw (new Exception "exception")))]
            (is (nil? (get-word-class 42))))))

(deftest test-get-source
    "Check the function get-source"
    (testing "Function get-source"
        (with-redefs [db-interface/select-source-id (fn [input] "Glossary")]
            (is (nil? (get-source nil)))
            (is (= "Glossary" (get-source 1))))))

(deftest test-get-source-exception-handling
    "Check the function get-source"
    (testing "Function get-source"
        (with-redefs [db-interface/select-source-id (fn [input] (throw (new Exception "exception")))]
            (is (nil? (get-source 42))))))

(deftest test-read-csv
    "Check the function read-cvs"
    (testing "read-csv"
        (is (seq? (read-csv "test/test.csv")))
        (is (= 4 (count (read-csv "test/test.csv"))))))

(deftest test-import-data-return-value
    "Check the function import-data"
    (testing "import-data"
        (with-redefs [get-word-class (fn [input] "adverb")
                      get-source     (fn [input] "source")
                      db-interface/insert-word-into-dictionary (fn [term description word-class use-it incorrect-forms correct-forms see-also internal verified copyrighted source] term)]
            (is (nil? (import-data "test/test.csv"))))))
