'use strict';

var gulp = require('gulp'),
	uglify = require('gulp-uglify'),
	argv = require('yargs').argv,
	gulpif = require('gulp-if'),
	flatten = require('gulp-flatten'),
	changed = require('gulp-changed'),

	srcRoot = 'src/apps',
	destRoot = '../webapp/WEB-INF/static/',

	fs = require('fs'),
	path = require('path'),
	es = require('event-stream'),
	gulp = require('gulp'),
	concat = require('gulp-concat'),
	uglify = require('gulp-uglify'),

	minifyCSS = require('gulp-minify-css'),
	prefix = require('gulp-autoprefixer'),
	pixrem = require('gulp-pixrem'),
	sass = require('gulp-dart-sass'),

	jshint = require('gulp-jshint');

function getFolders(dir) {
	return fs.readdirSync(dir)
		.filter(function(file) {
			return fs.statSync(path.join(dir, file)).isDirectory();
		});
}

gulp.task('angular', ['angularJs', 'angularPages', 'angularViews', 'angularSass']);

gulp.task('angularJs', function() {
	var folders = getFolders(srcRoot);

	var tasks = folders.map(function(folder) {

		return gulp.src(path.join(srcRoot, folder, '**/*.js'))
			.pipe(jshint().on('error', function() {
				// Ignore errors as they are already being printed to the console and we still
				// want to create the built js.
			}))
			.pipe(jshint.reporter('jshint-stylish'))
			.pipe(gulpif(argv.release, uglify()))
			.pipe(concat(folder + '.js'))
			.pipe(gulp.dest(destRoot + 'js'));
	});

	return es.merge.apply(null, tasks);
});

gulp.task('angularSass', function() {
	var folders = getFolders(srcRoot);

	var tasks = folders.map(function(folder) {

		return gulp.src(path.join(srcRoot, folder, '**/*.scss'))
			.pipe(sass())
			.pipe(prefix('last 2 versions', 'ie 8'))
			.pipe(pixrem())
			.pipe(gulpif(argv.release, minifyCSS()))
			.pipe(concat(folder + '.css'))
			.pipe(gulp.dest(path.join(destRoot, 'css')));
	});

	return es.merge.apply(null, tasks);
});

gulp.task('angularPages', function() {
	var folders = getFolders(srcRoot),
		dest = path.join(destRoot, '..', 'pages'),
		tasks;

	tasks = folders.map(function(folder) {

		return gulp.src(path.join(srcRoot, folder, 'app/*.html'))
			.pipe(changed(dest))
			.pipe(gulp.dest(dest));
	});

	return es.merge.apply(null, tasks);
});

gulp.task('angularViews', function() {
	var folders = getFolders(srcRoot),
		tasks;

	tasks = folders.map(function(folder) {

		var dest = path.join(destRoot, 'views', folder);

		return gulp.src([path.join(srcRoot, folder, '**/**.html'), '!' + path.join(srcRoot, folder, 'app/*.html')])
			.pipe(changed(dest))
			.pipe(flatten())
			.pipe(gulp.dest(dest));
	});

	return es.merge.apply(null, tasks);
});
