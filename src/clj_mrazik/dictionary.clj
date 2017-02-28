(ns clj-mrazik.dictionary)

(require '[clj-mrazik.db-interface :as db-interface])

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

(defn yes-no
    [w key]
    (condp = (get w key)
          0 (red "no")
          1 (green "yes")
          2 (purple "with caution")))

(defn incorrect-forms
    [w]
    (str (black "class:")           (blue (:class w)) " "
         (black "use it:")          (yes-no w :use) " "
         (black "description:")     (blue (:description w)) " "
         (black "source:")          (blue (:source w)) " "
         (black "incorrect forms:") (blue (:incorrect_forms w)) " "
         (black "see also:")        (blue (:see_also w))
))

(defn correct-forms
    [w]
    (str (black "class:")           (blue (:class w)) " "
         (black "use it:")          (yes-no w :use) " "
         (black "description:")     (blue (:description w)) " "
         (black "source:")          (blue (:source w)) " "
         (black "correct forms:")   (blue (:correct_forms w)) " "
         (black "see also:")        (blue (:see_also w))
))

(defn preferred-forms
    [w]
    (str "class: " (:class w) "  "
         "use it: " (yes-no w :use) "  "
         "description: " (:description w) "  "
         "source: " (:source w) "  "
         "preferred forms: " (:correct_forms w) "  "
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

