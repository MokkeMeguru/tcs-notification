(ns notification.schedule
  (:require
   [chime :as cc]
   [clj-time.core :as time]
   [clj-time.periodic :as periodic]
   [clojure.core.async :as a]
   [integrant.core :as ig]))

(defn adjust-minute-now []
  (let [now (time/now)]
    (time/plus
     (time/date-time
      (time/year now)
      (time/month now)
      (time/day now)
      (time/hour now)
      (time/minute now))
     (time/minutes 1))))

(defn schedule []
  (cc/chime-ch
   (periodic/periodic-seq (adjust-minute-now) (time/minutes 1))
   {:ch (a/chan (a/dropping-buffer 1))}))

(defn stop-schedule [ch]
  (a/close! ch))


;; integrant
(defmethod ig/init-key ::channel [_ _]
  (schedule))

(defmethod ig/halt-key! ::channel [_ ch]
  (a/close! ch))

(defmethod ig/init-key ::schedule-notification [_ {:keys [ch]}]
  (a/go-loop []
    (when-let [time (a/<! ch)]
      (prn "Hello world at " time)
      (notification.webpush/test-push)
      (recur))
   ))
