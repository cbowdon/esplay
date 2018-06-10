(set-env! :dependencies '[[org.clojure/core.async "0.4.474"]
                          [cc.qbits/spandex "0.6.2"]])

(deftask run
  "Run the project."
  [a args ARG [str] "the arguments for the application."]
  (with-pass-thru fs
    (require '[ergast-redux.core :as app])
    (apply (resolve 'app/-main) args)))
