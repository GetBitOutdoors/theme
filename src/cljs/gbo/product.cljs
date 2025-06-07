(ns gbo.product
  (:require [goog.dom :as gdom]
            [gbo.core :as core]))

(defn render-product-details
  [el product-data]
  (let [product-name (:name product-data)
        product-price (:price product-data)
        product-id (:id product-data)]
    (set! (.-innerHTML el)
          (str "<div class=\"cljs-product-widget\">"
               "<h3>Enhanced Product View</h3>"
               "<p>Product: " product-name "</p>"
               "<p>Price: " product-price "</p>"
               "<p>ID: " product-id "</p>"
               "</div>"))))

(defn ^:export init-product-widget
  [element-id data]
  (let [el (gdom/getElement element-id)]
    (if el
      (do
        (js/console.log (str "Initializing ClojureScript product widget on " element-id))
        (render-product-details el (js->clj data :keywordize-keys true)))
      (js/console.error (str "Element " element-id " not found for ClojureScript product widget.")))))

(defn ^:export init
  []
  (js/console.log "ClojureScript product.cljs initialized!")
  (let [product-elements (array-seq (js/document.querySelectorAll "[data-cljs-module='gbo.product']"))]
    (doseq [el product-elements]
      (let [element-id (.-id el)
            props (core/parse-props el)]
        (init-product-widget element-id props)))))

(js/console.log "gbo.product.cljs loaded")
