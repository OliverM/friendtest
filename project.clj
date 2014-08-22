(defproject friendtest "0.1.0-SNAPSHOT"
  :description "Me, triying to decipher Friend."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.cemerick/friend "0.2.1"]
                 [ring/ring-core "1.3.0"]
                 [ring/ring-jetty-adapter "1.3.0"]
                 [compojure "1.1.8"]
                 [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.8.7"]]
  :main friendtest.core)
