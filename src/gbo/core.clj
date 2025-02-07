(ns gbo.core
  (:use [gbo.handlebars]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def example-post
  {:title "My first post"
   :author {:firstName "Charles"
            :lastName  "Jolley"}})

(deftemplate post-view
  [:div.entry
    [:h1 (% title)]
    (%with author
      [:h2 (%str "By " (% firstName) (% lastName))])])