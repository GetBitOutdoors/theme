(ns gbo.category
  (:require [goog.dom :as gdom]
            [gbo.core :as core]))

(defn render-category-view
  [el category-data]
  (let [category-name (:name category-data)
        product-count (:productCount category-data)
        category-id (:id category-data)]
    (set! (.-innerHTML el)
          (str "<div class=\"cljs-category-view\">"
               "<h3>Enhanced Category View</h3>"
               "<p>Category: " category-name "</p>"
               "<p>Products: " product-count "</p>"
               "<p>ID: " category-id "</p>"
               "</div>"))))

(defn ^:export init-category-widget
  [element-id data]
  (let [el (gdom/getElement element-id)]
    (if el
      (do
        (js/console.log (str "Initializing ClojureScript category widget on " element-id))
        (render-category-view el (js->clj data :keywordize-keys true)))
      (js/console.error (str "Element " element-id " not found for ClojureScript category widget.")))))

(defn ^:export init
  []
  (js/console.log "ClojureScript category.cljs initialized!")
  (let [category-elements (array-seq (js/document.querySelectorAll "[data-cljs-module='gbo.category']"))]
    (doseq [el category-elements]
      (let [element-id (.-id el)
            props (core/parse-props el)]
        (init-category-widget element-id props)))))

(js/console.log "gbo.category.cljs loaded")
