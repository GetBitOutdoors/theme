(ns gbo.core
  (:require
   ["jquery" :as $]
   ["product" :as product-page]
   ;; tried  "/assets/js/theme/product.js" but it gave me :
   ;; The required JS dependency "/assets/js/theme/product.js" is not available, it was required by "gbo/core.cljs".
   
   ;; Dependency Trace:
   ;; 	gbo/core.cljs
   
   ))

(defn test-jquery-from-cljs []
  (let [jquery-loaded? (boolean $)
        jquery-version (.. $ -fn -jquery)
        body-element-exists? (boolean (.length ($ "body")))]
    (js/console.log "- All jquery tests passed:" 
      (and jquery-loaded? 
           jquery-version 
           body-element-exists?))))

(defn my_cljs_fn []
  (js/console.log "clojurescript function my_cljs_fn was called!")
  
  (test-jquery-from-cljs)

  ;; test calling js fns
  (product-page/my_js_top_level_fn)

  ;; test calling a method Product class in theme/assets/js/theme/product.js
  (.my_js_product_method product-page/Product))
