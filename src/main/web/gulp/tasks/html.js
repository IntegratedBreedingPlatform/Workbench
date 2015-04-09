'use strict';

var changed = require('gulp-changed'),
	dest = '../webapp/WEB-INF/pages',
	destMail = '../resources/mail-templates',
	gulp = require('gulp');

gulp.task('html', function() {
	return gulp.src('src/pages/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});


gulp.task('mail', function() {
	return gulp.src('src/mail/**')
		.pipe(changed(destMail))
		.pipe(gulp.dest(destMail));
});