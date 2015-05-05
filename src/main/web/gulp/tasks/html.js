'use strict';

var changed = require('gulp-changed'),
	destRoot = '../webapp/WEB-INF',
	destMail = '../resources/mail-templates',
	gulp = require('gulp');

gulp.task('html', ['pages', 'angularViews']);

gulp.task('pages', function() {
	var dest = destRoot + '/pages';

	return gulp.src('src/pages/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});


gulp.task('mail', function() {
	return gulp.src('src/mail/**')
		.pipe(changed(destMail))
		.pipe(gulp.dest(destMail));
});