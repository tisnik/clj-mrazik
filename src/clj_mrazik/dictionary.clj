(ns clj-mrazik.dictionary)

(def terms [
["1", "abend", "For details, see Wiktionary", "verb", "", "", "", "", "no", "yes", "no", "The Wiktionary"]
["2", "abend", "For details, see Wiktionary", "noun", "", "", "", "", "no", "yes", "no", "The Wiktionary"]
["3", "abend", "For details, see Wiktionary", "adjective", "", "", "", "", "no", "yes", "no", "The Wiktionary"]
])

(defn find-word
    [terms word]
    (let [words (filter #(= (second %) word) terms)]
        (for [w words]
            (str "class: " (nth w 3) "   source: " (nth w 11) "    description: " (nth w 2)))))

