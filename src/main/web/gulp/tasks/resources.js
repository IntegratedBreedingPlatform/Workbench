'use strict';

var changed = require('gulp-changed'),
	destRoot = '../webapp/WEB-INF',
	gulp = require('gulp');

gulp.task('resources', function() {
	var dest = destRoot + '/static/resources';

	return gulp.src('src/resources/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});
