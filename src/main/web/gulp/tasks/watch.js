'use strict';

var gulp = require('gulp');

gulp.task('watch', ['build', 'angular2'], function() {
	gulp.watch('src/sass/**', ['sass']);
	gulp.watch('src/images/**', ['images']);
	gulp.watch('src/pages/**', ['html']);
	gulp.watch('src/resources/**', ['resources']);
	gulp.watch('src/fonts/**', ['fonts']);
	gulp.watch('src/js/**', ['js']);
	gulp.watch(['src/**/*.js', 'src/apps/**/*.html', 'test/**/*.js', '!src/js/lib/**', '!src/appsNg2/**'], ['test']);

	// Angular
	gulp.watch('src/apps/**/*.js', ['angularJs']);
	gulp.watch('src/apps/**/*.scss', ['angularSass']);
	gulp.watch('src/apps/**/*.html', ['angularPages', 'angularViews']);
	
	// Angular 2
	gulp.watch(['src/appsNg2/**/*', '!**/build/**'], ['angular2Dist']);

	// Live update
	gulp.watch('../webapp/WEB-INF/**', ['hotswap']);
});
