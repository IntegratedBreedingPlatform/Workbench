'use strict';

var gulp = require('gulp'),
	argv = require('yargs').argv,
	path = require('path'),
	karma = require('karma').server,
	replace = require('gulp-replace'),
	merge = require('merge-stream');

function postProcessReports() {
	var lcov = gulp.src('reports/coverage/lcov.info')
			.pipe(replace('SF:.', 'SF:src/main/web'))
			.pipe(gulp.dest('reports/coverage')),

		jsunit = gulp.src('reports/junit/TESTS-junit.xml')
			.pipe(replace('test_', 'test.'))
			.pipe(gulp.dest('reports/junit'));

	return merge(lcov, jsunit);
}

gulp.task('test', function (done) {
	var karmaConf = argv.ci ? 'karma.conf.ci.js' : 'karma.conf.js',
		cb = argv.ci ? postProcessReports : done;

	karma.start({
		configFile: path.join(__dirname, '/../../', karmaConf),
		singleRun: true
	}, cb);
});
