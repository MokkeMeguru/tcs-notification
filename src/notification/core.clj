(ns notification.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [clojure.java.io :as io]
            [integrant.core :as ig]
            [integrant.repl :as igr]
            [notification.webpush :as nw]))


(def database-url
  (env :database-url))

(def config-file
  "config.edn")

(defn load-config [config]
  (-> config
      io/resource
      slurp
      ig/read-string
      (doto ig/load-namespaces)))

(defn start []
  (igr/set-prep! (constantly (load-config config-file)))
  (igr/prep)
  (igr/init))

(defn prepare []
  (timbre/info (format "Starting the app with %s environment" (name (env :clj-env))))
  (timbre/info "Hello, World!")
  (timbre/info "[process] read environment")
  (timbre/info "database-url:" database-url)
  (timbre/info "security provider initialization " nw/init!))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (prepare)
  (timbre/info "read-config")
  (ig/init (load-config config-file))
  (println "initialized!")
  )
