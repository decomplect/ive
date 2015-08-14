(defproject ive "0.1.0-SNAPSHOT"
  :description "Decomplect/ive Devcards"
  :url "https://github.com/decomplect/ive"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.107"]
                 [devcards "0.2.0-SNAPSHOT"]
                 [sablono "0.3.4"]
                 #_[org.omcljs/om "0.8.8"]
                 #_[reagent "0.5.0"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.7"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]
  
  :source-paths ["src"]

  :cljsbuild {:builds [{:id "devcards"
                        :source-paths ["src"]
                        :figwheel { :devcards true } ;; <- note this
                        :compiler { :main       "ive.index"
                                    :asset-path "js/compiled/devcards_out"
                                    :output-to  "resources/public/js/compiled/ive_devcards.js"
                                    :output-dir "resources/public/js/compiled/devcards_out"
                                    :source-map-timestamp true }}]}

  :figwheel { :css-dirs ["resources/public/css"] })
