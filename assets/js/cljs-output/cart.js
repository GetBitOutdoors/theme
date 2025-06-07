
import "./main.js";
import "./cljs-runtime/shadow.module.cart.prepend.js";
SHADOW_ENV.setLoaded("shadow.module.cart.prepend.js");
import "./cljs-runtime/gbo.cart.js";
SHADOW_ENV.setLoaded("gbo.cart.js");
import "./cljs-runtime/shadow.module.cart.append.js";
SHADOW_ENV.setLoaded("shadow.module.cart.append.js");


try { gbo.cart.init(); } catch (e) { console.error("An error occurred when calling (gbo.cart/init)"); console.error(e); }
shadow.cljs.devtools.client.env.module_loaded("cart");