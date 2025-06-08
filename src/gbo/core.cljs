(ns gbo.core)

;; main init fn
(defn my_cljs_fn [& args]
  (let [s 
        (cond 
          (empty? args) 
          "cljs fn ran without args (on module initialization)"
          (string? (first args))
          (first args)
          :else "bad argument given")]
    (js/console.log s)))


