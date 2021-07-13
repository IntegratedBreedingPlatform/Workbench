'use strict';

var argv = require('yargs').argv,
	cache = require('gulp-cached'),
	gulp = require('gulp'),
	gulpif = require('gulp-if'),
	handleErrors = require('../util/handleErrors'),
	minifyCSS = require('gulp-minify-css'),
	prefix = require('gulp-autoprefixer'),
	pixrem = require('gulp-pixrem'),
	concat = require('gulp-concat'),
	sass = require('gulp-dart-sass');

gulp.task('sass', ['libSass', 'clientSass', 'ontologySass']);

gulp.task('clientSass', function() {

	// TODO Add in source maps when this issue is resolved
	return gulp.src('src/sass/*.scss')
		.pipe(cache('sass'))
		.pipe(sass())
		.pipe(prefix('last 2 versions', 'ie 8'))
		.pipe(gulpif(argv.prod, minifyCSS()))
		.pipe(pixrem())
		.pipe(gulp.dest('../webapp/WEB-INF/static/css'))
		.on('error', handleErrors);
});

gulp.task('ontologySass', function() {

	// TODO Add in source maps when this issue is resolved
	return gulp.src('src/apps/ontology/**/*.scss')
		.pipe(cache('sass'))
		.pipe(sass())
		.pipe(prefix('last 2 versions', 'ie 8'))
		.pipe(gulpif(argv.prod, minifyCSS()))
		.pipe(pixrem())
		.pipe(concat('ontology.css'))
		.pipe(gulp.dest('../webapp/WEB-INF/static/css'))
		.on('error', handleErrors);
});

gulp.task('libSass', function() {

	// TODO Add in source maps when this issue is resolved
	return gulp.src('src/sass/lib/**')
		.pipe(cache('sass'))
		.pipe(sass())
		.pipe(gulpif(argv.release, minifyCSS()))
		.pipe(gulp.dest('../webapp/WEB-INF/static/lib'))
		.on('error', handleErrors);
});
