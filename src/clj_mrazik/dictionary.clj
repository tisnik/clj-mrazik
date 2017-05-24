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

(def reset
    (str (char 15)))

(defn yes-no-with-caution
    [w key]
    (condp = (get w key)
          0 (red    "no")
          1 (green  "yes")
          2 (yellow "with caution")
            (red    "error - word usage is not set!")))

(defn print-field
    [title word key]
    (str (bold (str title ": ")) (get word key) " "))

(defn use-it
    [w]
    (str (bold "use it: ") (yes-no-with-caution w :use) " "))

(defn incorrect-forms
    [w]
    (str reset
         (print-field "class"           w :class)
         (use-it                        w)
         (print-field "description"     w :description)
         (print-field "source"          w :source)
         (print-field "incorrect forms" w :incorrect_forms)
         (print-field "see also"        w :see_also)
         (print-field "product"         w :product)))

(defn correct-forms
    [w]
    (str reset
         (print-field "class"           w :class)
         (use-it                        w)
         (print-field "description"     w :description)
         (print-field "source"          w :source)
         (print-field "correct forms"   w :correct_forms)
         (print-field "see also"        w :see_also)
         (print-field "product"         w :product)))

(defn preferred-forms
    [w]
    (str reset
         (print-field "class"           w :class)
         (use-it                        w)
         (print-field "description"     w :description)
         (print-field "source"          w :source)
         (print-field "preferred forms" w :correct_forms)
         (print-field "see also"        w :see_also)
         (print-field "product"         w :product)))

(defn find-word
    [word]
    (let [words (db-interface/select-words word false)]
        (clojure.pprint/pprint words)
        (for [w words]
            (condp = (:use w)
                0 (correct-forms w)
                1 (incorrect-forms w)
                2 (preferred-forms w)))))

(defn find-word-like-this
    [word]
    (let [words (db-interface/select-words word true)]
        (clojure.pprint/pprint words)
        (for [w words]
            (condp = (:use w)
                0 (correct-forms w)
                1 (incorrect-forms w)
                2 (preferred-forms w)))))

(defn find-words-like-this
    [word]
    (let [words (db-interface/select-words word true)]
        (str "Found " (count words) " entries: "
             (clojure.string/join ", " (map #(:term %) words)))))

(defn term-count
    []
    (db-interface/all-words-count))

(defn word-exist?
    [word]
    (> (db-interface/select-word-count word false)
       0))

(defn words-like-this
    [word]
    (db-interface/select-word-count word true))

