(ns notification.webpush
  (:import
   [java.security Security KeyPairGenerator]
   [nl.martijndwars.webpush
    Base64Encoder
    Utils
    Notification
    PushService
    Subscription
    Subscription$Keys]
   org.bouncycastle.jce.ECNamedCurveTable
   org.bouncycastle.jce.provider.BouncyCastleProvider)
  (:require [clojure.spec.alpha :as s]
            [environ.core :refer [env]]
            [cheshire.core :refer [generate-string]]
            [taoensso.timbre :as timbre]))

(def -subject
  "mailto:meguru.mokke@gmail.com")

;;; specs ;;;
(s/def ::vapid-public string?)
(s/def ::vapid-private string?)
(s/def ::vapid (s/keys :req [::vapid-public ::vapid-private]))

(s/def ::http-protocol #{"HTTP" "HTTPS"})
(s/def ::response-status #{201})

(s/def ::subscription string?)
(s/def ::endpoint string?)
(s/def ::p256dh string?)
(s/def ::auth string?)
(s/def ::subject (s/and string? #(clojure.string/starts-with? % "mailto:")))

;;; utils ;;;
(defn add-security-provider! []
  (Security/addProvider (BouncyCastleProvider.)))

(defonce init! (add-security-provider!))

(defn generate-key-pair []
  (-> Utils/ALGORITHM
      (KeyPairGenerator/getInstance BouncyCastleProvider/PROVIDER_NAME)
      ;; memo: "doto macro" does descructive change
      ;; https://clojuredocs.org/clojure.core/doto
      (doto
          (.initialize
           (ECNamedCurveTable/getParameterSpec Utils/CURVE)))
      .generateKeyPair))

(defn encode [key]
  (-> key
      Utils/encode
      Base64Encoder/encodeUrl))

(defn generate-keys
  "Generate public and private keys\n
  usage: (print (generate-keys))"
  []
  (let [key-pair (generate-key-pair)]
    {:public (-> key-pair .getPublic encode)
     :private (-> key-pair .getPrivate encode)}))

;;; notification ;;;
(defn subscription
  ([subscription-map]
   (let [{:keys [endpoint keys]} subscription-map
         {:keys [p256dh auth]} keys]
     (subscription endpoint p256dh auth)))
  ([endpoint p256dh auth]
   (if (and
        (s/valid? ::endpoint endpoint)
        (s/valid? ::p256dh p256dh)
        (s/valid? ::auth auth))
     (Subscription. endpoint (Subscription$Keys. p256dh auth)))
   ))

(defn service [vapid-keys subject]
  (let [public-key (::vapid-public vapid-keys)
        private-key (::vapid-private vapid-keys)]
    (if (s/valid? ::subject subject)
      (PushService. public-key private-key subject))))

(defn notification [subscription payload]
  (Notification. subscription payload))

(defn send! [push-service notification]
  (.send push-service notification))

;; validation ;;;
(defn check-response [response]
  (and (s/valid? ::http-protocol (-> response .getProtocolVersion .getProtocol))
       (s/valid? ::response-status (-> response .getStatusLine .getStatusCode))))


;; vapid-key ;;;
(defonce vapid
  (let [vapid {::vapid-public (env :vapid-pub)
               ::vapid-private (env :vapid-private)}]
    (if (s/valid? ::vapid vapid)
      vapid nil)))


;;; test for developer
(defonce subject
  (if (s/valid? ::subject -subject)
    -subject))

(def example-device
  {:endpoint "https://fcm.googleapis.com/fcm/send/f8ZrPSJxQVk:APA91bF2DMtsF8WyrpiE1b38Mq0gAU5b2GcdUOzO1eR8jyX3UbpfmYC-oFqZE0VBr658gJ0MvMBUKoBmp3h00VWO2a2E2dBH8aD3QcbYAgrEaJN914jPPwWCFKjzCGjMe8PNsTzqcdsU"
   :keys {:auth  "ch9nKvIqqsqjl529A_X-bw"
          :p256dh "BPT8zHapyg8fbcGJqzNlKXj0gTdSmKOcVn4V7rwaceFwf2h_ZXqiQtLHya7KeMPd3YXTrPoSt39AqtCTrzWX45o"}})

(def example-devices
  [example-device])

(defn test-push []
  (let [subs (map #(subscription %) example-devices)
        serv (service vapid subject)]
    (doall
     (map
      (fn [sub]
         (if (or (nil? sub) (nil? serv))
           (timbre/error {:invalid-device example-device})
           (let [response
                 (send! serv
                        (notification sub
                                      (generate-string
                                       {:title "test notification"
                                        :body "Hello World!"})))]
             (if (check-response response)
               (timbre/info response)
               (timbre/error response)))))
      subs))))

(defn -main []
  (println (generate-keys)))
