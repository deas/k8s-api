(ns kubernetes-api.internals.martian
  (:require [martian.core :as martian]))

(defn- promise? [v]
  (and
   (instance? clojure.lang.IDeref v)
   (instance? clojure.lang.IBlockingDeref v)
   (instance? clojure.lang.IPending v)
   (instance? clojure.lang.IFn v)))

(defn response-for
  "Workaround to throw exceptions in the client like connection timeout"
  [& args]
  (let [res (apply martian/response-for args)
        ;; A bit ugly, but httpkit returns a promise and inputstream bodies
        ;; may end up as lazy-seqs so we need to take care
        response (if (promise? res) @res res)]
    (if (instance? Throwable (:error response))
      (throw (:error response))
      response)))
