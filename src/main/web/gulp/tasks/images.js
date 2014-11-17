'use strict';

var changed = require('gulp-changed'),
	gulp = require('gulp'),
	imagemin = require('gulp-imagemin');

gulp.task('images', function() {
	var dest = '../webapp/WEB-INF/static/images';

	return gulp.src('./src/images/**')
		.pipe(changed(dest))
		.pipe(imagemin())
		.pipe(gulp.dest(dest));
});
