'use strict';

var changed = require('gulp-changed'),
	dest = '../webapp/WEB-INF/static/lib',
	gulp = require('gulp');

gulp.task('fonts', function() {
	return gulp.src('src/fonts/lib/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});
