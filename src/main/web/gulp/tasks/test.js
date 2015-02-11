'use strict';

var gulp = require('gulp'),
	argv = require('yargs').argv,
	path = require('path'),
	karma = require('karma').server;

gulp.task('test', function (done) {
	var karmaConf = argv.ci ? 'karma.conf.ci.js' : 'karma.conf.js';

	karma.start({
		configFile: path.join(__dirname, '/../../', karmaConf),
		singleRun: true
	}, done);
});
