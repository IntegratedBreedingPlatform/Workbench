'use strict';

var gulp = require('gulp'),
	rimraf = require('rimraf');

gulp.task('clean', function(cb) {
	rimraf('../webapp/WEB-INF/static', cb);
});
