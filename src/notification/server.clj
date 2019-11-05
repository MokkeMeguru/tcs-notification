(ns notification.server
  (:require
   [integrant.core :as ig]
   [ring.adapter.jetty :as jetty]))

;; for server
(defmethod ig/init-key :notification.server/jetty [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/halt-key! :notification.server/jetty [_ server]
  (.stop server))
