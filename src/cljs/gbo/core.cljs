(ns gbo.core
  (:require ["jquery" :as $]
            ["/assets/theme/product.js" :as product]))

(defn test-jquery-from-cljs []
  (let [jquery-loaded? (boolean $)
        jquery-version (.. $ -fn -jquery)
        body-element-exists? (boolean (.length ($ "body")))
        all-tests-passed? (and jquery-loaded? jquery-version body-element-exists?)]
    (js/console.log "- All tests passed:" all-tests-passed?)))

;; Main initialization function
(defn init []
  (js/console.log "ClojureScript fn called on :init / pageload!"))


(defn run-all-print-tests []
  ;; calling project js fn from cljs
  (product/myJsFn)

  (test-jquery-from-cljs))

(defn exported_cljs_fn []
  (js/console.log "ClojureScript fn called from project js!")
  (run-all-print-tests))


