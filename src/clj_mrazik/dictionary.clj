(ns clj-mrazik.dictionary)

(require '[clj-mrazik.db-interface :as db-interface])

(defn yes-no
    [w key]
    (condp = (get w key)
          0 "no"
          1 "yes"
          2 "with caution"))

(defn incorrect-forms
    [w]
    (str "class: " (:class w) "  "
         "use it: " (yes-no w :use) "  "
         "description: " (:description w) "  "
         "source: " (:source w) "  "
         "incorrect forms: " (:incorrect_forms w) "  "
         "see also: " (:see_also w)
))

(defn correct-forms
    [w]
    (str "class: " (:class w) "  "
         "use it: " (yes-no w :use) "  "
         "description: " (:description w) "  "
         "source: " (:source w) "  "
         "correct forms: " (:correct_forms w) "  "
         "see also: " (:see_also w)
))

(defn preferred-forms
    [w]
    (str "class: " (:class w) "  "
         "use it: " (yes-no w :use) "  "
         "description: " (:description w) "  "
         "source: " (:source w) "  "
         "preferred forms: " (:incorrect_forms w) "  "
         "see also: " (:see_also w)
))

(defn find-word
    [word]
    (let [words (db-interface/select-words word)]
        (clojure.pprint/pprint words)
        (for [w words]
            (condp = (:use w)
                0 (correct-forms w) 
                1 (incorrect-forms w)
                2 (preferred-forms w)))))

(defn word-exist?
    [word]
    (> (db-interface/select-word-count word)
       0))

