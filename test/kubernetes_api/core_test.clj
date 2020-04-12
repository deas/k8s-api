(ns kubernetes-api.core-test
  (:require [clojure.test :refer :all]
            [kubernetes-api.core :as k8s-api]))

(def chuck "https://192.168.178.52:6443")
(def kubectl-proxy "http://localhost:8001")
(def basic-auth {:username "admin"
                 :password "94a0f6c76ddd9f623b35a7a06865107d"})
;; Authorization: Bearer <token>
(def token "eyJhbGciOiJSUzI1NiIsImtpZCI6InN3a2lxSFNQOTRaV0lUeXRQXy1zYzd4aVVCSGdHMzVmVzRrVk5pbFRKYXcifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tdDY0ZHIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjBkNTFiZjQ5LTc2ZWEtNDJjNS1hYWMxLTdhZjIzZGRhZmQ1ZiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.odDgNFAVXTkvnQifL9lOb9IvKEvcaJAJsO1M7h71CHET06MTYRsGIPm9extbDoYqkhdxiJ0gBKp9YT-yUjaeTI0lAXkKjs7ARIJUgai7vUEUGMMKwxu7bFx2Eqy1Rb_Cu1ruCVjTReIT2EE6DldxnJ41TTCSnNbkdH4CMP1iZdItZ9mYX9-z7TObwhCxCQdOS7O5BB9HbdZqbbXohLtcasameie_GnyIx__3Y-5tXATHkPp1J_lWhbzq4AR0W2njbic5uqUB8DNU3onb_c5G7xxMtB2Ftbws7O86NmQqS6RqrY25lkHZrA_rVBrxnTCUGPUVb1TmRmwyJxXxSaADhg")

;; (def token "ZXlKaGJHY2lPaUpTVXpJMU5pSXNJbXRwWkNJNkluTjNhMmx4U0ZOUU9UUmFWMGxVZVhSUVh5MXpZemQ0YVZWQ1NHZEhNelZtVnpSclZrNXBiRlJLWVhjaWZRLmV5SnBjM01pT2lKcmRXSmxjbTVsZEdWekwzTmxjblpwWTJWaFkyTnZkVzUwSWl3aWEzVmlaWEp1WlhSbGN5NXBieTl6WlhKMmFXTmxZV05qYjNWdWRDOXVZVzFsYzNCaFkyVWlPaUprWldaaGRXeDBJaXdpYTNWaVpYSnVaWFJsY3k1cGJ5OXpaWEoyYVdObFlXTmpiM1Z1ZEM5elpXTnlaWFF1Ym1GdFpTSTZJbVJsWm1GMWJIUXRkRzlyWlc0dGREWTBaSElpTENKcmRXSmxjbTVsZEdWekxtbHZMM05sY25acFkyVmhZMk52ZFc1MEwzTmxjblpwWTJVdFlXTmpiM1Z1ZEM1dVlXMWxJam9pWkdWbVlYVnNkQ0lzSW10MVltVnlibVYwWlhNdWFXOHZjMlZ5ZG1salpXRmpZMjkxYm5RdmMyVnlkbWxqWlMxaFkyTnZkVzUwTG5WcFpDSTZJakJrTlRGaVpqUTVMVGMyWldFdE5ESmpOUzFoWVdNeExUZGhaakl6WkdSaFptUTFaaUlzSW5OMVlpSTZJbk41YzNSbGJUcHpaWEoyYVdObFlXTmpiM1Z1ZERwa1pXWmhkV3gwT21SbFptRjFiSFFpZlEub2REZ05GQVZYVGt2blFpZkw5bE9iOUl2S0V2Y2FKQUpzTzFNN2g3MUNIRVQwNk1UWVJzR0lQbTlleHRiRG9ZcWtoZHhpSjBnQktwOVlULXlVamFlVEkwbEFYa0tqczdBUklKVWdhaTd2VUVVR01NS3d4dTdiRngyRXF5MVJiX0N1MXJ1Q1ZqVFJlSVQyRUU2RGxkeG5KNDFUVENTbk5ia2RINENNUDFpWmRJdFo5bVlYOS16N1RPYndoQ3hDUWRPUzdPNUJCOUhiZFpxYmJYb2hMdGNhc2FtZWllX0dueUl4X18zWS01dFhBVEhrUHAxSl9sV2hienE0QVIwVzJuamJpYzV1cVVCOEROVTNvbmJfYzVHN3h4TXRCMkZ0YndzN084Nk5tUXFTNlJxclkyNWxrSFpyQV9yVkJyeG5UQ1VHUFVWYjFUbVJtd3lKeFh4U2FBRGhn")
;; KUBERNETES_PORT=tcp://10.43.0.1:443
;; KUBERNETES_SERVICE_PORT=443
;; KUBERNETES_PORT_443_TCP_ADDR=10.43.0.1
;; KUBERNETES_PORT_443_TCP_PORT=443
;; KUBERNETES_PORT_443_TCP_PROTO=tcp
;; KUBERNETES_PORT_443_TCP=tcp://10.43.0.1:443
;; KUBERNETES_SERVICE_PORT_HTTPS=443
;; KUBERNETES_SERVICE_HOST=10.43.0.1

;; (System/getenv "HOME")
;; /var/run/secrets/kubernetes.io/serviceaccount # ls
;; ca.crt     namespace  token
;; /var/run/secrets/kubernetes.io/serviceaccount #

#_(deftest a-test
    (testing "FIXME, I fail."
      (is (= 0 1))))

(comment
  ;; curl -k -u admin:94a0f6c76ddd9f623b35a7a06865107d https://192.168.178.52:6443/api/v1/namespaces/default/pods
  ;; kubectl -n kube-system get secret default-token-w6vrc -o jsonpath=' {.data.token} '| base64 -d
  ;; {:token token}
  (let [client (k8s-api/client
                #_proxy
                chuck
                #_{:token token}
                {:insecure? true
                 :token token
                 ;;  :basic-auth basic-auth
                 })]
    ;; (k8s-api/explore client :Pod)
    #_(k8s-api/info client {:kind :Pod
                          :action :list})
    #_(k8s-api/invoke client {:kind    :ConfigMap
                            :action  :create
                            :request {:namespace "default"
                                      :body      {:apiVersion "v1"
                                                  :data       {"foo" "bar"}}}})
    
    (k8s-api/invoke client {:kind    :Pod
                            :action  :list
                            :request {:namespace "default"
                                      :watch true}})

    )
  token
  ;; chuck
  ;; kubectl-proxy
  )
;; TODO
