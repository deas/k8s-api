(ns kubernetes-api.misc
  (:require [kubernetes-api.interceptors.watch :as w]))

(defn find-first [pred coll]
  (first (filter pred coll)))

(defn indexes-of [pred coll]
  (keep-indexed #(when (pred %2) %1) coll))

(defn first-index-of [pred coll]
  (first (indexes-of pred coll)))

(defn map-vals [f coll]
  (into {} (map (fn [[k v]] [k (f v)]) coll)))

(defn map-keys [f coll]
  (into {} (map (fn [[k v]] [(f k) v]) coll)))

(defn assoc-some
  "Assoc[iate] if the value is not nil.
  Examples:
    (assoc-some {:a 1} :b false) => {:a 1 :b false}
    (assoc-some {:a 1} :b nil) => {:a 1}"
  ([m k v]
   (if (nil? v) m (assoc m k v)))
  ([m k v & kvs]
   (let [ret (assoc-some m k v)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw (IllegalArgumentException.
                 "assoc-some expects even number of arguments after map/vector, found odd number")))
       ret))))

(def http-impl (atom {}))

(defn http-request [] (:request-fn @http-impl))

(defn http-default-interceptors [] (:interceptors @http-impl))

(defn- swap-coerce-response
  [interceptors]
  (reduce #(conj %1
                 (if (= (:name %2) :martian.interceptors/coerce-response)
                   w/default-coerce-response
                   %2))
          []
          interceptors))

(defn ns-exists [ns]
  (try (require (symbol ns))
       ns
       (catch Exception _)))

(defn init! []
  (let [impl (or
              (ns-exists "martian.httpkit")
              (ns-exists "martian.clj-http-lite")
              (ns-exists "martian.clj-http"))
        request-fn (case impl
                     "martian.httpkit"
                     (fn [& args] @(apply @(resolve 'org.httpkit.client/request) args))
                     "martian.clj-http-lite"
                     (fn [& args] (apply @(resolve 'clj-http.lite.client/request) args))
                     "martian.clj-http"
                     (fn [& args] (apply @(resolve 'clj-http.client/request) args)))]
    (swap! http-impl assoc :interceptors 
           ;; @(resolve (symbol (str impl "/default-interceptors")))
           (swap-coerce-response @(resolve (symbol (str impl "/default-interceptors"))))
           )
    (swap! http-impl assoc :request-fn request-fn)))

(init!)
