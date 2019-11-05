(defproject notification "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.0"]
                 ;; for schedule
                 [jarohen/chime "0.2.2"]
                 [clj-time "0.15.2"]
                 ;; for webpush
                 [cheshire "5.9.0"]
                 [nl.martijndwars/web-push "5.0.2"]
                 [org.bouncycastle/bcprov-jdk15on "1.64"]
                 ;; for database
                 [seancorfield/next.jdbc "1.0.9"]
                 [org.postgresql/postgresql "42.2.5"]
                 ;; for environment settings
                 [environ "1.1.0"]
                 ;; for logging
                 [com.taoensso/timbre "4.10.0"]
                 [com.fzakaria/slf4j-timbre "0.3.14"]
                 ;; for integrant
                 [integrant "0.7.0"]
                 [integrant/repl "0.3.1"]
                 ;; for server
                 [ring "1.7.1"]
                 ]
  :aliases {"generate-key" ["exec" "-ep" "(use 'notification.webpush) (println generateKeyPair)"]}
  :main ^:skip-aot notification.core
  :target-path "target/%s"
  :plugins [[lein-environ "0.4.0"]]
  :profiles {:uberjar {:aot :all}
             :dev [:project/dev :profiles/dev]
             :test [:project/dev :profiles/test]
             :profiles/dev {}
             :profiles/test {}
             }
  )
