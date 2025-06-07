
import "./main.js";
import "./cljs-runtime/shadow.module.product.prepend.js";
SHADOW_ENV.setLoaded("shadow.module.product.prepend.js");
import "./cljs-runtime/gbo.product.js";
SHADOW_ENV.setLoaded("gbo.product.js");
import "./cljs-runtime/shadow.module.product.append.js";
SHADOW_ENV.setLoaded("shadow.module.product.append.js");


try { gbo.product.init(); } catch (e) { console.error("An error occurred when calling (gbo.product/init)"); console.error(e); }
shadow.cljs.devtools.client.env.module_loaded("product");