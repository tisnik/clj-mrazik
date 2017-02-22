(ns clj-mrazik.importer)

(require '[clojure.data.csv :as csv])
(require '[clojure.java.io  :as io])

(require '[clj-mrazik.db-interface :as db-interface])

(defn read-csv
    [file-name]
    (with-open [in-file (io/reader file-name)]
        (doall
            (csv/read-csv in-file))))

(defn parse-boolean
    [input]
    (if (or (= input "yes")
            (= input "Yes"))
        1 0))

(defn get-word-class
    [input]
    (if input
        (try
            (db-interface/select-word-class-id input)
            (catch Exception e
                (println (.getMessage e))))))

(defn get-source
    [input]
    (if input
        (try
            (db-interface/select-source-id input)
            (catch Exception e
                (println (.getMessage e))))))

(defn import-data
    [file-name]
    (let [parsed-csv (read-csv file-name)
          descriptions (first parsed-csv)
          entries      (rest  parsed-csv)]
         (doseq [entry entries]
             (let [word-class (get-word-class (nth entry 3))
                   source     (get-source     (nth entry 11))]
                 (println (nth entry 3) (nth entry 11) word-class source)
                 (db-interface/insert-word-into-dictionary
                     (nth entry 1) ;term
                     (nth entry 2) ;description
                     word-class      ; word class
                     (parse-boolean  (nth entry 4)) ; use it
                     (nth entry 5) ; incorrect forms
                     (nth entry 6) ; correct forms
                     (nth entry 7) ; see also
                     (parse-boolean  (nth entry 8))  ; internal
                     (parse-boolean  (nth entry 9))  ; verified
                     (parse-boolean  (nth entry 10)) ; copyrighted
                     source                            ; source
                 ))
             )
))

