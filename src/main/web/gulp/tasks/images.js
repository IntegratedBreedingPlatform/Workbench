'use strict';

var changed = require('gulp-changed'),
	gulp = require('gulp');

	// FIXME (See below)
	//imagemin = require('gulp-imagemin');

gulp.task('images', ['appImages', 'libImages']);

gulp.task('appImages', function() {
	var dest = '../webapp/WEB-INF/static/images';

	return gulp.src(['./src/images/**', '!./src/images/{lib,lib/**}'])
		.pipe(changed(dest))
		// FIXME I'm temporarily disabling this until I get time to look at why running the build on windows vs
		// mac seems to produce png files that differ
		//.pipe(imagemin())
		.pipe(gulp.dest(dest));
});

gulp.task('libImages', function() {
	var dest = '../webapp/WEB-INF/static/lib';

	return gulp.src('./src/images/lib/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});
