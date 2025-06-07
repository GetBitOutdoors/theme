(ns gbo.core
  (:require [goog.dom :as gdom]))

;; Main initialization function
(defn ^:export init []
  (js/console.log "ClojureScript core.cljs initialized!"))

;; Generic component mounting function
(defn ^:export mount-component
  [element-id data]
  (let [el (gdom/getElement element-id)]
    (if el
      (do
        (js/console.log (str "Mounting ClojureScript component on " element-id))
        (set! (.-innerHTML el) 
              (str "Hello from ClojureScript component on " element-id 
                   " with data: " (js/JSON.stringify (clj->js data)))))
      (js/console.error (str "Element " element-id " not found for ClojureScript component.")))))

;; Helper function to parse JSON data from DOM elements
(defn parse-props [^js/HTMLElement el]
  (try
    (when-let [props-str (.-dataset.cljsProps el)]
      (js->clj (js/JSON.parse props-str) :keywordize-keys true))
    (catch :default e
      (js/console.error "Failed to parse CLJS props:" e)
      nil)))

;; Log that the module has loaded
(js/console.log "gbo.core.cljs loaded")
