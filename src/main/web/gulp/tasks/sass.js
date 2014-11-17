'use strict';

var argv = require('yargs').argv,
	cache = require('gulp-cached'),
	gulp = require('gulp'),
	gulpif = require('gulp-if'),
	handleErrors = require('../util/handleErrors'),
	minifyCSS = require('gulp-minify-css'),
	prefix = require('gulp-autoprefixer'),
	pixrem = require('gulp-pixrem'),
	sass = require('gulp-sass');

gulp.task('sass', ['libSass', 'clientSass']);

gulp.task('clientSass', function() {

	// TODO Add in source maps when this issue is resolved
	return gulp.src('src/sass/*.scss')
		.pipe(cache('sass'))
		.pipe(sass())
		.pipe(prefix('last 2 versions', 'ie 8'))
		.pipe(pixrem())
		.pipe(gulpif(argv.prod, minifyCSS()))
		.pipe(gulp.dest('../webapp/WEB-INF/static/css'))
		.on('error', handleErrors);
});

gulp.task('libSass', function() {

	// TODO Add in source maps when this issue is resolved
	return gulp.src('src/sass/lib/**')
		.pipe(cache('sass'))
		.pipe(sass())
		.pipe(gulpif(argv.prod, minifyCSS()))
		.pipe(gulp.dest('../webapp/WEB-INF/static/lib'))
		.on('error', handleErrors);
});
