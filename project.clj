(defproject gbo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.879"]
                 [thheller/shadow-cljs "2.15.2"]
                 [gql-format "0.1.1"]
                 [lambdaisland/fetch "1.0.33"]
                 [re-graph "0.1.14" :exclusions [re-graph.hato]]
                 [hiccup "1.0.5"]]
  :repl-options {:init-ns gbo.core})
