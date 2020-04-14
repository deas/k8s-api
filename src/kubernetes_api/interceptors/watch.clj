(ns kubernetes-api.interceptors.watch
  (:import
    (java.io BufferedReader InputStreamReader))
  (:require                                                 ;;[less.awful.ssl :as ssl]
    [martian.encoding :as encoding]
    [martian.encoders :as encoders]
    ;; [tripod.log :as log]
    [cheshire.core :as json]))

(defn json-decode [key-fn body]
  (if (string? body)
            (json/decode body key-fn)
            (-> body (InputStreamReader. "UTF8") BufferedReader. (json/parsed-seq key-fn))))

(defn coerce-response [encoders]
  {:name ::coerce-response
   :enter (fn [{:keys [request handler] :as ctx}]
            (let [content-type (and (not (get-in request [:headers "Accept"]))
                                    (encoding/choose-content-type encoders (:produces handler)))
                  as (if
                      (get-in request [:query-params :watch]) :stream
                      (or (:as (encoding/find-encoder encoders content-type)) :text))]

              (cond-> (assoc-in ctx [:request :as] as)
                content-type (assoc-in [:request :headers "Accept"] content-type))))

   :leave (fn [{:keys [response] :as ctx}]
            (assoc ctx :response
                   (let [content-type (and (:body response)
                                           (not-empty (get-in response [:headers :content-type])))
                         {:keys [decode]} (encoding/find-encoder encoders content-type)]
                     (update response :body decode))))})

(def default-coerce-response (coerce-response
                              (assoc-in (encoders/default-encoders)
                                        ["application/json"
                                         :decode]
                                        (partial json-decode keyword))))
