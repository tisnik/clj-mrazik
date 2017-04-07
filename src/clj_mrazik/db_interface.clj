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

(ns clj-mrazik.db-interface
    "Namespace that contains interface to the database.")

(require '[clojure.java.jdbc  :as jdbc])

(require '[clj-mrazik.db-spec :as db-spec])

(defn select-word-class-id
    [input]
    (-> (jdbc/query db-spec/mrazik-db
             ["select id from classes where class=?" input])
        first
        :id))

(defn select-source-id
    [input]
    (-> (jdbc/query db-spec/mrazik-db
             ["select id from sources where source=?" input])
        first
        :id))

(defn insert-word-into-dictionary
    [term description word-class use-it incorrect-forms correct-forms
     see-also internal verified copyrighted source]
    (jdbc/insert! db-spec/mrazik-db
        :dictionary {:term            term
                     :description     description
                     :class           word-class
                     :use             use-it
                     :incorrect_forms incorrect-forms
                     :correct_forms   correct-forms
                     :see_also        see-also
                     :internal        internal
                     :verified        verified
                     :copyrighted     copyrighted
                     :source          source}))

(defn select-words
    [word]
    (jdbc/query db-spec/mrazik-db
        ["select term, description,
                 (select class from classes where classes.id=dictionary.class) as class,
                 use, incorrect_forms, correct_forms, see_also, internal, verified, copyrighted,
                 (select source from sources where sources.id=dictionary.source) as source,
                 (select product from products where products.id=dictionary.product) as product
                 from dictionary where term=?" word]))

(defn select-word-count
    [word]
    (->
        (jdbc/query db-spec/mrazik-db
            ["select count(*) as cnt from dictionary where term=?" word])
        first
        :cnt))

(defn all-words-count
    []
    (->
        (jdbc/query db-spec/mrazik-db
            ["select count(*) as cnt from dictionary"])
        first
        :cnt))

