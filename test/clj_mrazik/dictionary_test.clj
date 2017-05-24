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

(ns clj-mrazik.dictionary-test
  (:require [clojure.test :refer :all]
            [clj-mrazik.dictionary :refer :all]))

;
; Common functions used by tests.
;

(defn callable?
    "Test if given function-name is bound to the real function."
    [function-name]
    (clojure.test/function? function-name))


(deftest test-highlight-existence
    "Check that the clj-mrazik.dictionary/highlight definition exists."
    (testing "if the clj-mrazik.dictionary/highlight definition exists."
        (is (callable? 'clj-mrazik.dictionary/highlight))))


(deftest test-black-existence
    "Check that the clj-mrazik.dictionary/black definition exists."
    (testing "if the clj-mrazik.dictionary/black definition exists."
        (is (callable? 'clj-mrazik.dictionary/black))))


(deftest test-red-existence
    "Check that the clj-mrazik.dictionary/red definition exists."
    (testing "if the clj-mrazik.dictionary/red definition exists."
        (is (callable? 'clj-mrazik.dictionary/red))))


(deftest test-green-existence
    "Check that the clj-mrazik.dictionary/green definition exists."
    (testing "if the clj-mrazik.dictionary/green definition exists."
        (is (callable? 'clj-mrazik.dictionary/green))))


(deftest test-blue-existence
    "Check that the clj-mrazik.dictionary/blue definition exists."
    (testing "if the clj-mrazik.dictionary/blue definition exists."
        (is (callable? 'clj-mrazik.dictionary/blue))))


(deftest test-purple-existence
    "Check that the clj-mrazik.dictionary/purple definition exists."
    (testing "if the clj-mrazik.dictionary/purple definition exists."
        (is (callable? 'clj-mrazik.dictionary/purple))))


(deftest test-yellow-existence
    "Check that the clj-mrazik.dictionary/yellow definition exists."
    (testing "if the clj-mrazik.dictionary/yellow definition exists."
        (is (callable? 'clj-mrazik.dictionary/yellow))))


(deftest test-bold-existence
    "Check that the clj-mrazik.dictionary/bold definition exists."
    (testing "if the clj-mrazik.dictionary/bold definition exists."
        (is (callable? 'clj-mrazik.dictionary/bold))))


(deftest test-yes-no-with-caution-existence
    "Check that the clj-mrazik.dictionary/yes-no-with-caution definition exists."
    (testing "if the clj-mrazik.dictionary/yes-no-with-caution definition exists."
        (is (callable? 'clj-mrazik.dictionary/yes-no-with-caution))))


(deftest test-print-field-existence
    "Check that the clj-mrazik.dictionary/print-field definition exists."
    (testing "if the clj-mrazik.dictionary/print-field definition exists."
        (is (callable? 'clj-mrazik.dictionary/print-field))))


(deftest test-use-it-existence
    "Check that the clj-mrazik.dictionary/use-it definition exists."
    (testing "if the clj-mrazik.dictionary/use-it definition exists."
        (is (callable? 'clj-mrazik.dictionary/use-it))))


(deftest test-incorrect-forms-existence
    "Check that the clj-mrazik.dictionary/incorrect-forms definition exists."
    (testing "if the clj-mrazik.dictionary/incorrect-forms definition exists."
        (is (callable? 'clj-mrazik.dictionary/incorrect-forms))))


(deftest test-correct-forms-existence
    "Check that the clj-mrazik.dictionary/correct-forms definition exists."
    (testing "if the clj-mrazik.dictionary/correct-forms definition exists."
        (is (callable? 'clj-mrazik.dictionary/correct-forms))))


(deftest test-preferred-forms-existence
    "Check that the clj-mrazik.dictionary/preferred-forms definition exists."
    (testing "if the clj-mrazik.dictionary/preferred-forms definition exists."
        (is (callable? 'clj-mrazik.dictionary/preferred-forms))))


(deftest test-find-word-existence
    "Check that the clj-mrazik.dictionary/find-word definition exists."
    (testing "if the clj-mrazik.dictionary/find-word definition exists."
        (is (callable? 'clj-mrazik.dictionary/find-word))))


(deftest test-word-exist?-existence
    "Check that the clj-mrazik.dictionary/word-exist? definition exists."
    (testing "if the clj-mrazik.dictionary/word-exist? definition exists."
        (is (callable? 'clj-mrazik.dictionary/word-exist?))))

(deftest test-highlight
    "Checks the function highlight."
    (testing "the function highlight."
        (is (= java.lang.String (type (highlight "string" "code"))))
        (is (= (char 3) (.charAt (highlight "string" "code") 0)))))

