// webpack.shadow.js - Configuration for ClojureScript integration with ESM modules
const path = require('path');

module.exports = {
  // Add ClojureScript entry points
  entry: {
    // Main entry point that will load our ClojureScript modules
    cljs: './assets/js/cljs-loader.js'
  },
  // Enable ESM module support
  experiments: {
    outputModule: true
  },
  module: {
    rules: [
      // Handle source maps from shadow-cljs
      {
        test: /\.js$/,
        enforce: 'pre',
        use: ['source-map-loader'],
        exclude: [
          /node_modules/
        ]
      }
    ]
  },
  resolve: {
    // Make sure Webpack can find our ClojureScript output
    modules: [
      // path.resolve(__dirname, 'assets/js/cljs-output'),
      'node_modules'
    ],
    // Add .js extension for ESM imports
    extensions: ['.js']
  }
};
