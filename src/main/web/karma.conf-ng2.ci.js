'use strict';

var baseConfig = require('./karma.conf-ng2.js');

module.exports = function(config) {
	baseConfig(config);

	// Override base config
	config.set({
		logLevel: config.LOG_DEBUG,
		singleRun: true,
		colors: false,
		autoWatch: false,
		reporters: ['progress', 'junit', 'coverage'],
		junitReporter: {
			// These will not be interpreted unless the file name starts with TESTS-,
			// but right now the parsing is failing (see jira.codehaus.org/browse/SONARJS-304)
			// so I'm "disabling" them for now
			outputFile: 'reports/junit/js-ng2-tests-junit.xml'
		},
		coverageReporter: {
			type: 'lcov',
			dir: 'reports',
			subdir: 'coverage-ng2'
		}
	});
};
