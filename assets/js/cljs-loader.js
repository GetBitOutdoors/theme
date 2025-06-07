// ClojureScript module loader for BigCommerce Stencil theme using ESM imports
// This file is responsible for dynamically importing and initializing ClojureScript modules

// Import the main ClojureScript module entry point for its side-effects.
// This should load gbo.core.js and make gbo.core.init available globally via goog.exportSymbol.
import './cljs-output/main.js';

// Module cache to avoid re-importing the same module
const moduleCache = {
  // 'gbo.core': coreModule // coreModule is no longer an object representing the whole module
};

/**
 * Dynamically import a ClojureScript module
 * @param {string} moduleName - The name of the module (e.g., 'gbo.product')
 * @returns {Promise<Object>} - Promise resolving to the imported module
 */
async function importCljsModule(moduleName) {
  // If module is already in cache, return it
  if (moduleCache[moduleName]) {
    return moduleCache[moduleName];
  }
  
  // Map module name to file path
  const moduleMapping = {
    'gbo.product': '../js/cljs-output/product.js',
    'gbo.cart': '../js/cljs-output/cart.js',
    'gbo.category': '../js/cljs-output/category.js'
  };
  
  const modulePath = moduleMapping[moduleName];
  if (!modulePath) {
    console.error(`No module path mapping found for ${moduleName}`);
    return null;
  }
  
  try {
    // Dynamically import the module
    const module = await import(/* webpackIgnore: true */ modulePath);
    moduleCache[moduleName] = module;
    return module;
  } catch (error) {
    console.error(`Failed to import CLJS module ${moduleName} from ${modulePath}:`, error);
    return null;
  }
}

/**
 * Initialize a ClojureScript module with the given parameters
 * @param {string} moduleName - The name of the module (e.g., 'gbo.product')
 * @param {string} initFnName - The name of the initialization function to call
 * @param {string} targetElementId - The ID of the DOM element to mount the component to
 * @param {Object} props - Properties to pass to the initialization function
 */
async function initializeCljsModule(moduleName, initFnName, targetElementId, props) {
  console.log(`Attempting to load CLJS module: ${moduleName}, init function: ${initFnName}`);
  
  // Import the module
  const module = await importCljsModule(moduleName);
  if (!module) return;
  
  // Check if the initialization function exists in the module
  if (typeof module[initFnName] === 'function') {
    console.log(`Calling ${moduleName}.${initFnName} for element #${targetElementId}`);
    module[initFnName](targetElementId, props);
  } else {
    console.error(`CLJS function ${initFnName} not found in module ${moduleName}`);
  }
}

/**
 * Find all ClojureScript mount points in the DOM and initialize them
 */
async function initializeAllCljsComponents() {
  const cljsMountPoints = document.querySelectorAll('[data-cljs-module]');
  console.log(`Found ${cljsMountPoints.length} CLJS mount points`);
  
  // Initialize core module first by calling the globally available function
  if (window.gbo && window.gbo.core && typeof window.gbo.core.init === 'function') {
    console.log("Attempting to call window.gbo.core.init()");
    window.gbo.core.init();
  } else {
    console.error("window.gbo.core.init not found. Ensure cljs-output/main.js has been loaded and correctly defines gbo.core.init.");
    console.log("Debug info - window.gbo:", window.gbo);
    if (window.gbo) {
      console.log("Debug info - window.gbo.core:", window.gbo.core);
    }
  }
  
  // Process each mount point
  for (const el of cljsMountPoints) {
    const moduleName = el.dataset.cljsModule;
    const initFnName = el.dataset.cljsInitFn || 'mount_component'; // Default init function
    const targetElementId = el.id;
    
    if (!targetElementId) {
      console.warn('Element with data-cljs-module needs an id for mounting:', el);
      continue;
    }
    
    let props = {};
    if (el.dataset.cljsProps) {
      try {
        props = JSON.parse(el.dataset.cljsProps);
      } catch (e) {
        console.error('Failed to parse cljs-props on element:', el, e);
      }
    }
    
    await initializeCljsModule(moduleName, initFnName, targetElementId, props);
  }
}

// Initialize components when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  console.log('ClojureScript loader initializing components');
  initializeAllCljsComponents().catch(error => {
    console.error('Error initializing ClojureScript components:', error);
  });
});

// Export the initialization functions for use in other scripts
window.initializeCljsModule = initializeCljsModule;
window.initializeAllCljsComponents = initializeAllCljsComponents;

console.log('ClojureScript loader initialized');
