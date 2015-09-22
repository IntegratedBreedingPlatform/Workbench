'use strict';

var baseConfig = require('./karma.conf.js');

module.exports = function(config) {
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
			// These will not be interpreted unless the file name starts with TESTS-,
			// but right now the parsing is failing (see jira.codehaus.org/browse/SONARJS-304)
			// so I'm "disabling" them for now
			outputFile: 'reports/junit/js-tests-junit.xml'
		},
		coverageReporter: {
			type: 'lcov',
			dir: 'reports',
			subdir: 'coverage'
		}
	});
};
