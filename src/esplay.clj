(ns esplay
  (:require [qbits.spandex :as es]))

(def client (es/client {:hosts ["http://localhost:9200"]}))

;; Post some data to ES, which is a relatively well-behaved REST API.
;; In this case the index is arresteddevelopment and the type is character
(es/request client {:url "/arresteddevelopment/character/"
                    :method :post
                    :body {:surname "Bluth"
                           :forename "George"
                           :quote "No touching!"}})
;; a bunch more
(doseq [character [{:surname "Bluth"
                    :forename "George Michael"
                    :quote "I have Pop-Pop in the attic."}
                   {:surname "Bluth"
                    :forename "Michael"
                    :quote "I'm not sure what I expected."}
                   {:surname "Fünke"
                    :forename "Maebe"
                    :quote "Oh my god I've become my father."}
                   {:surname "Fünke"
                    :forename "Tobias"
                    :quote "I've been in and out of Michael like a cuckoo clock bird!"}
                   {:surname "Bluth"
                    :forename "GOB"
                    :quote "I've made a huge mistake."}
                   {:surname "Bluth"
                    :forename "Buster"
                    :quote "Hey, brother!"}
                   {:surname "Bluth"
                    :forename "Lucille"
                    :quote "I don't care for GOB."}
                   {:surname "Bluth"
                    :forename "Lindsay"
                    :quote "Oh my god I have the exact same blouse."}]]
  (es/request client {:url "/arresteddevelopment/character/"
                      :method :post
                      :body character}))

;; Searching requires you to know either the Lucene query syntax...
(es/request client {:url "/arresteddevelopment/_search/"
                    :method :get
                    :body {:query
                           {:query_string
                            {:query "surname:Bluth AND quote:\"Oh my god\""}}}})


;; ...or the ES Query DSL
(es/request client {:url "/arresteddevelopment/_search/"
                    :method :get
                    :body {:query
                           {:bool
                            {:must [{:match {:quote "Oh my god"}}]
                             :filter [{:term {:surname "Bluth"}}]}}}})
;; Presumably it's more powerful, but easier to screw up - the above doesn't
;; work because the surname field is analysed as a full text field, so can't
;; be the exact match "term" expects.

;; Resolve that issue with an updated mapping...
;; (guide: http://joelabrahamsson.com/elasticsearch-101/)
(es/request client {:url "/arresteddevelopment/character/_mapping"
                    :method :put
                    :body {:character
                           {:properties
                            {:surname {:type "text"}
                             :surname-exact {:type "keyword"}}}}})

;; ...and try again
(es/request client {:url "/arresteddevelopment/_search/"
                    :method :get
                    :body {:query
                           {:bool
                            {:must [{:match {:quote "Oh my god"}}]
                             :filter [{:term {:surname-exact "Bluth"}}]}}}})

;; re-index by re-putting the above (a post would re-create them...)
;; fuck it didn't work
