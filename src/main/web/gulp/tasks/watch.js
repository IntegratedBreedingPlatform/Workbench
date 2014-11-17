'use strict';

var gulp = require('gulp');

gulp.task('watch', ['build'], function() {
	gulp.watch('src/sass/**', ['sass']);
	gulp.watch('src/images/**', ['images']);
	gulp.watch('src/pages/**', ['html']);
	gulp.watch('src/fonts/**', ['fonts']);
	gulp.watch('src/js/**', ['js']);
	gulp.watch('../webapp/WEB-INF/**', ['hotswap']);
});
