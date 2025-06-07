
import "./main.js";
import "./cljs-runtime/shadow.module.category.prepend.js";
SHADOW_ENV.setLoaded("shadow.module.category.prepend.js");
import "./cljs-runtime/gbo.category.js";
SHADOW_ENV.setLoaded("gbo.category.js");
import "./cljs-runtime/shadow.module.category.append.js";
SHADOW_ENV.setLoaded("shadow.module.category.append.js");


try { gbo.category.init(); } catch (e) { console.error("An error occurred when calling (gbo.category/init)"); console.error(e); }
shadow.cljs.devtools.client.env.module_loaded("category");