# ClojureScript Integration for BigCommerce Stencil Theme

This document describes the ClojureScript integration with the BigCommerce Stencil theme using shadow-cljs and webpack.

## Overview

The integration uses shadow-cljs with ESM target and webpack to compile and bundle ClojureScript code into the theme. The approach follows the pattern described in [this article](https://deh.li/2022/11/04/shadow-cljs-webpack.html).

## Directory Structure

- `src/cljs/gbo/` - ClojureScript source files
  - `core.cljs` - Core functionality and utilities
  - `product.cljs` - Product page specific code
  - `cart.cljs` - Cart page specific code
  - `category.cljs` - Category page specific code
- `assets/js/cljs-output/` - Compiled ClojureScript output
- `assets/js/cljs-loader.js` - JavaScript loader for ClojureScript modules
- `assets/scss/components/cljs/` - CSS styles for ClojureScript components

## Configuration Files

- `shadow-cljs.edn` - Shadow-cljs configuration
- `webpack.shadow.js` - Webpack configuration for ClojureScript integration
- `stencil.conf.cjs` - Stencil configuration with proxy for shadow-cljs dev server

## Development Workflow

1. Start the development server using the provided script:

```bash
./start-dev.sh
```

This script starts both the shadow-cljs watch process and the Stencil development server.

2. Alternatively, you can run the processes separately:

```bash
# In one terminal
npm run cljs:watch

# In another terminal
stencil start -o
```

## Building for Production

To build for production, run:

```bash
npm run build:all
```

This will compile ClojureScript with advanced optimizations and build the Webpack production bundle.

## Linting

To lint your ClojureScript code:

```bash
npm run cljs:lint
```

A pre-commit hook is also set up to automatically lint ClojureScript files before committing.

## Adding New ClojureScript Components

1. Create a new ClojureScript namespace in `src/cljs/gbo/`
2. Add the module to the `shadow-cljs.edn` configuration
3. Add the module mapping in `assets/js/cljs-loader.js`
4. Add the component container to the appropriate template with data attributes:
   - `data-cljs-module`: The ClojureScript namespace
   - `data-cljs-init-fn`: The initialization function to call
   - `data-cljs-props`: JSON props to pass to the component

## Troubleshooting

- If components don't load, check the browser console for errors
- Verify that the shadow-cljs server is running on port 9630
- Check that the proxy configuration in `stencil.conf.cjs` is correct
- Run `clj-kondo` to check for ClojureScript errors
