'use strict';

var gulp = require('gulp');

gulp.task('watch', ['build'], function() {
	gulp.watch('src/sass/**', ['sass']);
	gulp.watch('src/images/**', ['images']);
	gulp.watch('src/pages/**', ['html']);
	gulp.watch('src/fonts/**', ['fonts']);
	gulp.watch('src/js/**', ['js']);

	// Angular
	gulp.watch('src/apps/**/*.js', ['angularJs']);
	gulp.watch('src/apps/**/*.scss', ['angularSass']);
	gulp.watch('src/apps/**/*.html', ['angularPages', 'angularViews']);

	// Live update
	gulp.watch('../webapp/WEB-INF/**', ['hotswap']);
});
