(ns gbo.mycljs
  (:require
    ;[gql-format.core :as gf]
    [lambdaisland.fetch :as fetch]))
    ;["bigcommerce/stencil-utils" :as sten]))
    ;[re-graph.core :as re-graph]))

(defn log [v]
  (js/console.log v))

;------------------------------
;javascript:void function(){fetch(window.location.pathname+"%3Fdebug=context").then(function(n){n.json().then(function(n){console.log(n)})})}();
;(log
;  (.then
;    (fetch/get )
;    prn))

(def json-context-url
  (str (.. js/window -location -pathname)  "?debug=context"))

;(log json-context-url)

(def json-test-url
  "https://jsonplaceholder.typicode.com/posts/1")

;(let
;  [p (fetch/get json-test-url)]
;  (-> (js/Promise.resolve p)
;    ;(.then #(prn (get (js->clj (:body %)) "title")))
;    (.then #(log (.-title (:body %))))
;    ;(.then #(log  (js/JSON %)))
;    (.catch #(log %))
;    (.finally #(log "cleanup"))))


(defn stringify [o]
  (.stringify js/JSON o))

;(sten/utils.api.product.getById "1234" "{}" #(log "callback"))

(def graphql-endpoint
  "https://getbitoutdoors.com/graphql")
(def query-string
  "query CategoryTree3LevelsDeep {\n  site {\n    categoryTree {\n      ...CategoryFields\n      children {\n        ...CategoryFields\n        children {\n          ...CategoryFields\n        }\n      }\n    }\n  }\n}\n\nfragment CategoryFields on CategoryTreeItem {\n  name\n  path\n  entityId\n}\n")

;(let
;  [p (fetch/post json-test-url)]
;  (-> (js/Promise.resolve p)
;    ;(.then #(prn (get (js->clj (:body %)) "title")))
;    (.then #(log (.-title (:body %))))
;    ;(.then #(log  (js/JSON %)))
;    (.catch #(log %))
;    (.finally #(log "cleanup"))))


(def origin-url
  "https://developer.bigcommerce.com")


(def token
  "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJlYXQiOjE2MDgzODMwODAsInN1Yl90eXBlIjowLCJ0b2tlbl90eXBlIjoxLCJjb3JzIjpbImh0dHBzOi8vZGV2ZWxvcGVyLmJpZ2NvbW1lcmNlLmNvbSJdLCJjaWQiOjEsImlhdCI6MTYwODIxMDI4MCwic3ViIjoiYmNhcHAubGlua2VyZCIsInNpZCI6MTAwMDcyODU0NSwiaXNzIjoiQkMifQ.NAPnS06aVWErh-EM5kMCFMT541_AxV9n-XghgIfga7D3Alc9ccfF2Lv0VtKg6TL5t7vxggSED3q3Nkq944BLvQ")
(def bearer
  "Bearer " token)


;-H 'Accept-Encoding: gzip,
;deflate,
;br' -H 'Content-Type: ' -H 'Accept: application/json' -H 'Connection: keep-alive' -H 'DNT: 1' -H '' --data-binary '{"query":}' --compressed

(fetch/post graphql-endpoint
  {
   :query-params {:Origin origin-url}
   :body (stringify (clj->js {:query query-string}))})

;(log
;  (clj->js {:query query-string}))
;
;(log
;  (stringify (clj->js {:query query-string})))

;(defn on-thing [{:keys [data errors] :as payload}]
;  (js/console.log "response recieved" data errors))

;(log "this")
;(this-as my-this (log my-this))
;
;(log "global")
;(log (js->clj js/window))

;(log "----")

;(log js/jsContext)
(prn
  (keys
   ;(:myProductName
     (js->clj
       (.parse js/JSON
         js/jsContext)
       :keywordize-keys true)))

(log "hello")

;
;(re-graph/query
;  query-string  ;; your graphql query
;  {}  ;; arguments map
;  on-thing)          ;; callback event when response is recieved
;


;(js/console.log
;  ;(.. js/document -location -href)
;  (.. js/window -location -pathname))

;(let [div (.createElement js/document "DIV")]
;    (.appendChild div (create-button)))



