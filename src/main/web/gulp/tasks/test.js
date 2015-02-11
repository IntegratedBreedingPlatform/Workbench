'use strict';

var gulp = require('gulp'),
	argv = require('yargs').argv,
	path = require('path'),
	karma = require('karma').server,
	replace = require('gulp-replace');

function postProcessLCOV() {
	return gulp.src('reports/coverage/lcov.info')
		.pipe(replace('SF:.', 'SF:src/main/web'))
		.pipe(gulp.dest('reports/coverage'));
}

gulp.task('test', function (done) {
	var karmaConf = argv.ci ? 'karma.conf.ci.js' : 'karma.conf.js',
		cb = argv.ci ? postProcessLCOV : done;

	karma.start({
		configFile: path.join(__dirname, '/../../', karmaConf),
		singleRun: true
	}, cb);
});
