'use strict';

var baseConfig = require('./karma.conf.js');

module.exports = function (config) {
	baseConfig(config);

	// Override base config
	config.set({
		logLevel: config.LOG_DEBUG,
		singleRun: true,
		colors: false,
		autoWatch: false,
		reporters: ['progress', 'junit', 'coverage'],
		preprocessors: {
			'../webapp/WEB-INF/static/views/**/*.html': 'ng-html2js',
			'src/apps/**/*.js': ['coverage']
		},
		junitReporter: {
			outputFile: 'reports/junit/junit.xml'
		},
		coverageReporter: {
			type: 'lcov',
			dir: 'reports',
			subdir: 'coverage'
		}
	});
};
