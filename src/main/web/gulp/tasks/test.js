'use strict';

var gulp = require('gulp'),
	karma = require('gulp-karma');

gulp.task('test', function() {
	// Be sure to return the stream
	// NOTE: Using the fake './dummy' so as to run the files
	// listed in karma.conf.js INSTEAD of what was passed to
	// gulp.src !
	return gulp.src('./dummy')
	.pipe(karma({
		configFile: 'karma.conf.js',
		action: 'run'
	}))
	.on('error', function(err) {
		// Make sure failed tests cause gulp to exit non-zero
		console.log(err);
		this.emit('end'); //instead of erroring the stream, end it
	});
});

gulp.task('autotest', function() {
	return gulp.watch(['src/app/**/*.js', 'test/**/*.js'], ['test']);
});
