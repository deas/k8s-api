(ns kubernetes-api.core-test
  (:import
    (java.io BufferedReader InputStreamReader))
  (:require [clojure.test :refer :all]
            [kubernetes-api.interceptors.watch :as w]
            ;; [clj-http.client :as http]
            [martian.encoders :as encoder]
            [clj-http.lite.client :as http]
            [cheshire.core :as c]
            [kubernetes-api.core :as k8s-api]
            [kubernetes-api.misc :as misc]))

(deftest pluggable-client
  (testing "Namespace is httpkit"
    (is (= "martian.httpkit"
           (-> (misc/http-default-interceptors) meta :ns .toString)))))

(def token "eyJhbGciOiJSUzI1NiIsImtpZCI6InN3a2lxSFNQOTRaV0lUeXRQXy1zYzd4aVVCSGdHMzVmVzRrVk5pbFRKYXcifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tdDY0ZHIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjBkNTFiZjQ5LTc2ZWEtNDJjNS1hYWMxLTdhZjIzZGRhZmQ1ZiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.odDgNFAVXTkvnQifL9lOb9IvKEvcaJAJsO1M7h71CHET06MTYRsGIPm9extbDoYqkhdxiJ0gBKp9YT-yUjaeTI0lAXkKjs7ARIJUgai7vUEUGMMKwxu7bFx2Eqy1Rb_Cu1ruCVjTReIT2EE6DldxnJ41TTCSnNbkdH4CMP1iZdItZ9mYX9-z7TObwhCxCQdOS7O5BB9HbdZqbbXohLtcasameie_GnyIx__3Y-5tXATHkPp1J_lWhbzq4AR0W2njbic5uqUB8DNU3onb_c5G7xxMtB2Ftbws7O86NmQqS6RqrY25lkHZrA_rVBrxnTCUGPUVb1TmRmwyJxXxSaADhg")


(misc/http-default-interceptors)
;; (c/parse-string "{\"a\":1, \"b\":{\"c\":3}}" keyword)

(comment
  (-> (http/request {:insecure? false,
                     :oauth-token
                     "eyJhbGciOiJSUzI1NiIsImtpZCI6InN3a2lxSFNQOTRaV0lUeXRQXy1zYzd4aVVCSGdHMzVmVzRrVk5pbFRKYXcifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tdDY0ZHIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjBkNTFiZjQ5LTc2ZWEtNDJjNS1hYWMxLTdhZjIzZGRhZmQ1ZiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.odDgNFAVXTkvnQifL9lOb9IvKEvcaJAJsO1M7h71CHET06MTYRsGIPm9extbDoYqkhdxiJ0gBKp9YT-yUjaeTI0lAXkKjs7ARIJUgai7vUEUGMMKwxu7bFx2Eqy1Rb_Cu1ruCVjTReIT2EE6DldxnJ41TTCSnNbkdH4CMP1iZdItZ9mYX9-z7TObwhCxCQdOS7O5BB9HbdZqbbXohLtcasameie_GnyIx__3Y-5tXATHkPp1J_lWhbzq4AR0W2njbic5uqUB8DNU3onb_c5G7xxMtB2Ftbws7O86NmQqS6RqrY25lkHZrA_rVBrxnTCUGPUVb1TmRmwyJxXxSaADhg",
                     :method    :get,
                     :url       "http://localhost:8001/apis/",
                     :as        :text,
                     :headers   {"Accept" "application/json"}})
      :body
      type
      )
  
  (assoc-in (encoder/default-encoders) ["application/json" :decode] w/json-decode)
  
  ;; (get-in (encoder/default-encoders) ["application/json" :decode])

  (let [res (http-light/get "http://127.0.0.1:8001/api/v1/namespaces/default/pods?watch=true"
                            {:as :stream})
        rdr (-> (:body res) (InputStreamReader. "UTF8") BufferedReader.)
        items (parsed-seq rdr)]
    (doseq [item items] (println item)))

  (let [client (k8s-api/client
                "http://localhost:8001"
                #_{:token token}
                {;; :insecure? true
                 :token token
                  ;;  :basic-auth basic-auth
                 })]
    (doseq [item (k8s-api/invoke client {:kind    :Pod
                                         :action  :list
                                         :request {:namespace "default"
                                                   :watch     true}})]
      (println item))
    
    #_(k8s-api/invoke client {:kind    :Pod
                            :action  :list
                            :request {:namespace "default"
                                      ;; :watch     true
                                      }})
    

    #_(k8s-api/request client {:kind    :Pod
                               :action  :list
                               :request {                      ;; :as :stream
                                         :namespace "default"
                                         :watch     true
                                         }})
    #_(k8s-api/explore client :Pod)
    #_(k8s-api/info client {:kind   :Pod
                            :action :list})
    )
  ;; (pst)
  )
;; (misc/http-request)
