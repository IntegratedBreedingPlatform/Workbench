'use strict';

var changed = require('gulp-changed'),
	destRoot = '../webapp/WEB-INF',
	gulp = require('gulp');

gulp.task('html', ['pages', 'angularViews']);

gulp.task('pages', function() {
	var dest = destRoot + '/pages';

	return gulp.src('src/pages/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});

gulp.task('angularViews', function() {
	var dest = destRoot + '/static/views';

	return gulp.src('src/app/**/*.html')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});
