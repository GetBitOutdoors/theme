# ClojureScript Integration with BigCommerce Stencil Themes

ClojureScript and BigCommerce Stencil themes can be successfully integrated through several sophisticated approaches, each offering distinct advantages for modern e-commerce development. **The key to successful integration lies in careful build system coordination, strategic bundle optimization, and robust runtime management** to prevent race conditions while maintaining performance under the 50KB bundle size constraint.

## Double bundle vs integrated webpack builds

The choice between double bundling and integrated webpack builds fundamentally impacts your development workflow and optimization capabilities. **The double bundle approach maintains strict separation between ClojureScript and JavaScript bundling**, requiring two distinct build processes but offering maximum control over each system.

Shadow-cljs supports webpack integration through the `:js-provider :external` configuration, where webpack handles all JavaScript dependencies while Shadow-cljs focuses exclusively on ClojureScript compilation:

```clojure
{:builds
  {:theme
    {:target :browser
     :output-dir "assets/js"
     :js-options {:js-provider :external
                  :external-index "target/index.js"}}}}
```

This approach enables full access to webpack's ecosystem including tree-shaking, code splitting, and CSS handling, but requires coordination between two separate build processes. **The integrated approach using `:js-provider :shadow` simplifies development by handling both ClojureScript and JavaScript bundling within Shadow-cljs**, providing superior hot-reload capabilities but limiting access to webpack-specific optimizations.

For existing Stencil projects, the `:npm-module` target enables incremental ClojureScript adoption by generating CommonJS-compatible files that webpack can consume directly. The newer `:esm` target provides better integration with modern JavaScript tooling and supports more effective tree-shaking through ES6 module exports.

## Hot-reload and nREPL configuration

Achieving seamless hot-reload functionality requires careful coordination between Shadow-cljs and stencil-cli development servers. **The recommended approach involves running Shadow-cljs on port 8080 and stencil-cli on port 3000, with proxy configuration directing JavaScript assets to the Shadow-cljs server**.

Configure stencil-cli to proxy JavaScript requests:

```json
{
  "port": 3000,
  "proxy": {
    "/assets/js/**": {
      "target": "http://localhost:8080",
      "changeOrigin": true,
      "pathRewrite": {
        "^/assets/js": "/js"
      }
    }
  }
}
```

nREPL integration enables REPL-driven development within the Stencil workflow by configuring Shadow-cljs with CIDER middleware:

```clojure
{:nrepl {:port 9000
         :init-ns theme.dev
         :middleware [cider.nrepl/cider-middleware
                     refactor-nrepl.middleware/wrap-refactor]}
 
 :builds
 {:theme
  {:devtools {:before-load theme.core/stop
              :after-load theme.core/start
              :preloads [devtools.preload theme.dev]}}}}
```

**The critical startup sequence involves starting Shadow-cljs server first, then the watch build, connecting the nREPL client, and finally launching stencil-cli**. This order prevents port conflicts and ensures proper asset serving coordination.

## Bidirectional JavaScript interop with stencil-utils

The applied-science/js-interop library provides elegant patterns for integrating with BigCommerce's stencil-utils library. **Key interop patterns focus on safe property access, function calls with proper `this` binding, and coordinated event handling**.

Basic interop patterns include compile-time safe property access and JavaScript object manipulation:

```clojure
(ns theme.stencil-integration
  (:require [applied-science.js-interop :as j]
            ["@bigcommerce/stencil-utils" :as stencil-utils]))

;; Cart operations with proper error handling
(defn add-to-cart [form-data callback]
  (j/call stencil-utils :api :cart :itemAdd 
          form-data
          (fn [err response]
            (if err
              (js/console.error "Cart error:" err)
              (callback response)))))

;; Product data fetching
(defn get-product-options [product-id callback]
  (let [options (j/obj :template "products/bulk-discount-rates")]
    (j/call stencil-utils :api :product :getById product-id options callback)))
```

**Exposing ClojureScript functions to JavaScript requires the `^:export` metadata and careful data transformation**:

```clojure
(defn ^:export update-product-display [product-data]
  (let [cljs-data (js->clj product-data :keywordize-keys true)]
    (update-ui-component cljs-data)))

;; Register functions globally for theme access
(defn register-global-functions []
  (j/assoc! js/window :clojureScriptAPI 
    (j/obj :updateProduct update-product-display
           :handleCheckout handle-checkout-update
           :processCart process-cart-data)))
```

## Replicant UI library integration

Replicant's data-driven rendering model aligns exceptionally well with e-commerce requirements, offering predictable performance characteristics through its pure functional approach. **The key to BigCommerce integration lies in coordinating Replicant's render cycle with BigCommerce's context data and API responses**.

Product page integration follows a clean separation between state management and view rendering:

```clojure
(ns bigcommerce.product
  (:require [replicant.dom :as rdom]))

(defn init-product-page [product-id]
  (let [app-state (atom {:product/id product-id
                        :product/data nil
                        :cart/items []
                        :ui/loading? true})]
    
    ;; Global event dispatcher
    (rdom/set-dispatch! 
      (fn handle-product-events [render-data [action & args]]
        (case action
          :product/add-to-cart (add-to-cart-action app-state args)
          :product/update-quantity (update-quantity app-state args)
          :ui/show-options (toggle-options app-state args))))
    
    ;; Reactive rendering
    (add-watch app-state :render
      (fn [_ _ _ new-state]
        (rdom/render (product-view new-state) 
                     (.getElementById js/document "product-app"))))))
```

**Replicant's event handling uses data-driven patterns that enhance testability and debugging**:

```clojure
(defn add-to-cart-section [state]
  [:div.cart-actions
   [:button.btn-primary
    {:on {:click [:product/add-to-cart 
                  {:product-id (:product/id state)
                   :quantity (:ui/quantity state 1)}]}
     :disabled (or (:ui/loading? state) 
                   (:product/out-of-stock? state))}
    "Add to Cart"]])
```

## Bundle size optimization strategies

Achieving sub-50KB gzipped bundles requires aggressive optimization techniques combined with careful dependency management. **The most effective approach combines advanced compilation settings with strategic dead code elimination and conditional compilation**.

Critical Shadow-cljs configuration for maximum optimization:

```clojure
{:release 
 {:compiler-options 
  {:optimizations :advanced
   :static-fns true
   :fn-invoke-direct true
   :infer-externs :auto
   :pseudo-names false
   :pretty-print false
   :closure-defines {goog.DEBUG false}
   :closure-extra-annotations #{"@define" "@enum" "@export" "@expose" 
                                "@nocollapse" "@override" "@suppress"}
   :language-in :ecmascript-2017
   :language-out :ecmascript5}}}
```

**Dead code elimination requires avoiding patterns that prevent Google Closure Compiler optimizations**. Replace dynamic dispatch maps with case-based dispatch, avoid top-level multimethods, and use closure defines for feature flags:

```clojure
;; Use closure defines for conditional compilation
(goog-define ^boolean DEBUG false)
(goog-define ^boolean ANALYTICS false)

(defn init-product-page []
  (when DEBUG (js/console.log "Debug mode enabled"))
  (when ANALYTICS (init-analytics))
  (rdom/render (product-view @app-state) root-element))
```

Bundle size monitoring should be automated through CI/CD integration:

```bash
# Generate bundle analysis
npx shadow-cljs run shadow.cljs.build-report product-page report.html

# Size budget enforcement
echo "const MAX_SIZE = 51200; // 50KB
const actualSize = require('./manifest.edn').modules.main['gzip-size'];
if (actualSize > MAX_SIZE) {
  console.error(\`Bundle size \${actualSize} exceeds \${MAX_SIZE} bytes\`);
  process.exit(1);
}" | node
```

## Load order and race condition prevention

**Preventing race conditions requires coordinated initialization sequences and dependency management**. The recommended approach uses polling strategies combined with event-driven coordination to ensure proper startup order.

Essential dependency coordination pattern:

```clojure
(defn ensure-dom-ready [callback]
  (if (= (j/get js/document :readyState) "loading")
    (j/call js/document :addEventListener "DOMContentLoaded" callback)
    (callback)))

(defn wait-for-stencil-utils [callback]
  (if (j/get js/window :stencilUtils)
    (callback)
    (js/setTimeout #(wait-for-stencil-utils callback) 50)))

(defn init-theme []
  (ensure-dom-ready
    (fn []
      (wait-for-stencil-utils
        (fn []
          (init-stencil-integration)
          (init-clojurescript-components))))))
```

**Event-driven coordination enables bidirectional communication between Stencil and ClojureScript systems**:

```clojure
(defn setup-event-coordination []
  ;; Listen to stencil-utils events
  (j/call stencil-utils :hooks :on "cart-item-add"
    (fn [event]
      (let [product-data (j/get event :product)]
        (update-cljs-cart-state product-data)
        (trigger-ui-update))))
  
  ;; Signal ClojureScript readiness
  (j/call js/window :dispatchEvent 
    (js/CustomEvent. "clojurescript-ready" (j/obj :detail {:ready true}))))
```

## Module loading and code splitting configuration

Shadow-cljs module configuration for non-SPA environments requires strategic splitting aligned with BigCommerce's page-based architecture. **The optimal approach creates shared modules for common functionality while maintaining page-specific entry points**.

Multi-page application configuration:

```clojure
{:builds
 {:theme
  {:target :browser
   :output-dir "assets/js"
   :modules
   {:shared {:entries []}
    :product-page {:entries [theme.product/init]
                   :depends-on #{:shared}}
    :cart {:entries [theme.cart/init]
           :depends-on #{:shared}}
    :checkout {:entries [theme.checkout/init]
               :depends-on #{:shared}}}}}}
```

**Dynamic module loading using shadow.lazy enables progressive feature enhancement**:

```clojure
(ns theme.core
  (:require [shadow.lazy :as lazy]))

(def product-modal-lazy 
  (lazy/loadable theme.modals.product/component))

(defn show-product-modal [product-id]
  (lazy/load product-modal-lazy
             (fn [modal-component]
               (modal-component {:product-id product-id}))))
```

Page-specific initialization aligns with Stencil's template system:

```clojure
(def page-components
  {:product (lazy/loadable theme.pages.product/init)
   :category (lazy/loadable theme.pages.category/init)
   :cart (lazy/loadable theme.pages.cart/init)})

(defn initialize-page [page-type]
  (when-let [component-loader (get page-components page-type)]
    (lazy/load component-loader)))
```

## UI element management until JavaScript loads

**Progressive enhancement patterns ensure graceful degradation when JavaScript fails to load or loads slowly**. The approach combines CSS-based loading states with JavaScript-driven activation once all dependencies are available.

HTML structure with disabled state by default:

```html
<div id="product-app" data-loading="true">
  <button class="add-to-cart" disabled>Add to Cart</button>
  <div class="product-options" style="opacity: 0.5; pointer-events: none;">
    <!-- Options here -->
  </div>
</div>

<script>
document.documentElement.classList.add('js-loading');

window.addEventListener('load', function() {
  if (window.bigcommerce && window.bigcommerce.product) {
    document.documentElement.classList.remove('js-loading');
    document.documentElement.classList.add('js-ready');
  }
});
</script>

<style>
.js-loading .add-to-cart { opacity: 0.6; pointer-events: none; }
.js-loading .product-options { opacity: 0.5; }
.js-ready .add-to-cart { opacity: 1; pointer-events: auto; }
</style>
```

**Declarative loading state management in ClojureScript provides clean separation of concerns**:

```clojure
(defn loading-wrapper [loading? content]
  [:div.relative
   (when loading?
     [:div.loading-overlay
      [:div.spinner "Loading..."]])
   [:div {:class (when loading? "loading")}
    content]])

(defn product-view [state]
  (loading-wrapper 
    (:ui/loading? state)
    [:div.product-content
     (product-details state)
     (product-actions state)]))
```

## Implementation pitfalls and considerations

Several critical pitfalls can derail ClojureScript-Stencil integration. **The most common issues involve build system conflicts, incorrect load order, and bundle size creep due to unnecessary dependencies**. 

Build system integration requires careful port management to prevent conflicts between development servers. Always verify that Shadow-cljs development server is accessible from Stencil's proxy configuration, and implement proper error handling for network failures during asset loading.

**Bundle size monitoring must be automated and enforced through CI/CD pipelines** to prevent gradual size increases. Implement size budgets with automated failing builds when limits are exceeded, and regularly audit dependencies for unnecessary inclusions.

Performance optimization requires ongoing attention to compilation settings and dead code elimination patterns. Avoid dynamic dispatch mechanisms that prevent Google Closure Compiler optimizations, and use strategic code splitting to minimize initial bundle size while maintaining functionality.

The integration of ClojureScript with BigCommerce Stencil themes offers significant advantages for modern e-commerce development when implemented with proper attention to build system coordination, performance optimization, and runtime management. Success requires disciplined adherence to bundle size constraints and careful orchestration of multiple development tools, but the resulting development experience provides the productivity benefits of functional programming within the BigCommerce ecosystem.