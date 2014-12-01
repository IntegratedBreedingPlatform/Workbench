'use strict';

var changed = require('gulp-changed'),
	gulp = require('gulp'),
	imagemin = require('gulp-imagemin');

gulp.task('images', ['appImages', 'libImages']);

gulp.task('appImages', function() {
	var dest = '../webapp/WEB-INF/static/images';

	return gulp.src(['./src/images/**', '!./src/images/{lib,lib/**}'])
		.pipe(changed(dest))
		.pipe(imagemin())
		.pipe(gulp.dest(dest));
});

gulp.task('libImages', function() {
	var dest = '../webapp/WEB-INF/static/lib';

	return gulp.src('./src/images/lib/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});
