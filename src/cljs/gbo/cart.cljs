(ns gbo.cart
  (:require [goog.dom :as gdom]
            [gbo.core :as core]))

(defn render-cart-summary
  [el cart-data]
  (let [total (:total cart-data)
        count (:count cart-data)]
    (set! (.-innerHTML el)
          (str "<div class=\"cljs-cart-summary\">"
               "<h3>Enhanced Cart Summary</h3>"
               "<p>Items: " count "</p>"
               "<p>Total: " total "</p>"
               "</div>"))))

(defn ^:export init-cart-widget
  [element-id data]
  (let [el (gdom/getElement element-id)]
    (if el
      (do
        (js/console.log (str "Initializing ClojureScript cart widget on " element-id))
        (render-cart-summary el (js->clj data :keywordize-keys true)))
      (js/console.error (str "Element " element-id " not found for ClojureScript cart widget.")))))

(defn ^:export init
  []
  (js/console.log "ClojureScript cart.cljs initialized!")
  (let [cart-elements (array-seq (js/document.querySelectorAll "[data-cljs-module='gbo.cart']"))]
    (doseq [el cart-elements]
      (let [element-id (.-id el)
            props (core/parse-props el)]
        (init-cart-widget element-id props)))))

(js/console.log "gbo.cart.cljs loaded")
