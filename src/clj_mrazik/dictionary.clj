(ns clj-mrazik.dictionary)

(require '[clj-mrazik.db-interface :as db-interface])

; best colors: 10 4 6
;
(defn highlight
    [string color-code]
    (str (char 3) color-code string (char 3) "99"))

(defn black
    [string]
    (highlight string "01"))

(defn red
    [string]
    (highlight string "04"))

(defn green
    [string]
    (highlight string "03"))

(defn blue
    [string]
    (highlight string "02"))

(defn purple
    [string]
    (highlight string "06"))

(defn yellow
    [string]
    (highlight string "08"))

(defn bold
    [string]
    (str (char 2) string (char 15)))

(defn reset
    []
    (str (char 15)))

(defn yes-no-with-caution
    [w key]
    (condp = (get w key)
          0 (red    "no")
          1 (green  "yes")
          2 (yellow "with caution")))

(defn incorrect-forms
    [w]
    (str (reset)
         (bold "class: ")           (:class w) " "
         (bold "use it: ")          (yes-no-with-caution w :use) " "
         (bold "description: ")     (:description w) " "
         (bold "source: ")          (:source w) " "
         (bold "incorrect forms: ") (:incorrect_forms w) " "
         (bold "see also: ")        (:see_also w)
))

(defn correct-forms
    [w]
    (str (reset)
         (bold "class: ")           (:class w) " "
         (bold "use it: ")          (yes-no-with-caution w :use) " "
         (bold "description: ")     (:description w) " "
         (bold "source: ")          (:source w) " "
         (bold "correct forms: ")   (:correct_forms w) " "
         (bold "see also: ")        (:see_also w)
))

(defn preferred-forms
    [w]
    (str (reset)
         (bold "class: ")           (:class w) " "
         (bold "use it: ")          (yes-no-with-caution w :use) " "
         (bold "description: ")     (:description w) " "
         (bold "source: ")          (:source w) " "
         (bold "preferred forms: ") (:correct_forms w) " "
         (bold "see also: ")        (:see_also w)
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

