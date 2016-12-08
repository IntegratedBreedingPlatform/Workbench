'use strict';

var argv = require('yargs').argv;

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: './',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      // Polyfills.
      'node_modules/es6-shim/es6-shim.js',

      // System.js for module loading
      'node_modules/systemjs/dist/system.src.js',

      // Zone.js dependencies
      'node_modules/zone.js/dist/zone.js',
      'node_modules/zone.js/dist/long-stack-trace-zone.js',
      'node_modules/zone.js/dist/sync-test.js',
      'node_modules/zone.js/dist/async-test.js',
      'node_modules/zone.js/dist/fake-async-test.js',
      'node_modules/zone.js/dist/proxy.js',
      'node_modules/zone.js/dist/jasmine-patch.js',

      'node_modules/reflect-metadata/Reflect.js',

      // RxJs.
      { pattern: 'node_modules/rxjs/**/*.js', included: false, watched: false },
      { pattern: 'node_modules/rxjs/**/*.js.map', included: false, watched: false },

      // paths loaded via module imports
      // Angular itself
      { pattern: 'node_modules/@angular/**/*.js', included: false, watched: true },

      { pattern: './src/appsNg2/**/build/**/*.js', included: false, watched: true },
      { pattern: './src/appsNg2/**/build/**/*.html', included: false, watched: true, served: true },
      { pattern: './src/appsNg2/**/build/**/*.css', included: false, watched: true, served: true },
      { pattern: 'node_modules/systemjs/dist/system-polyfills.js', included: false, watched: false }, // PhantomJS2 (and possibly others) might require it

      'karma-test-shim-ng2.js'
    ],


    // list of files to exclude
    exclude: [
      'node_modules/**/*spec.js'
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      'src/appsNg2/**/build/**/!(*spec).js': ['coverage']
    },

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'coverage'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: [
      'PhantomJS'
    ],

    /*
    customLaunchers: {
      Chrome_travis_ci: {
        base: 'Chrome',
        flags: ['--no-sandbox']
      }
    },
    */

   coverageReporter: {
      type: 'text-summary'
   },

// Enable when checking test paths is needed
  //  coverageReporter: {
	// 		type: 'lcov',
	// 		dir: 'reports',
	// 		subdir: 'coverage-ng2'
	// 	},

    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Increased the default timeout as it was timing out on Windows
    browserNoActivityTimeout: 30000,

    // Passing command line arguments to tests
    client: {
      files: argv.files
    }
  });

  /*
  if (process.env.APPVEYOR) {
    config.browsers = ['IE'];
    config.singleRun = true;
    config.browserNoActivityTimeout = 90000; // Note: default value (10000) is not enough
  }

  if (process.env.TRAVIS || process.env.CIRCLECI) {
    config.browsers = ['Chrome_travis_ci'];
    config.singleRun = true;
  }
  */
};
