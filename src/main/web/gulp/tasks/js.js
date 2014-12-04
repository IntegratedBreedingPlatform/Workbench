'use strict';

var changed = require('gulp-changed'),
	gulp = require('gulp'),
	uglify = require('gulp-uglify'),
	argv = require('yargs').argv,
	gulpif = require('gulp-if'),

	cache = require('gulp-cached'),
	jshint = require('gulp-jshint'),
	jshintStylish = require('jshint-stylish'),
	lazypipe = require('lazypipe'),

	destRoot = '../webapp/WEB-INF/static',

	hintAllTheThings;

hintAllTheThings = lazypipe()
	.pipe(cache, 'linting')
	.pipe(jshint)
	.pipe(jshint.reporter, jshintStylish);

gulp.task('js', ['libJs', 'clientJs']);

gulp.task('libJs', function() {

	var dest = destRoot + '/js/lib';

	return gulp.src('src/js/lib/**')
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});

// We don't cache here in case the user changes from prod to dev and I can't be bothered figuring out how to
// get that to work properly
gulp.task('clientJs', function() {

	var dest = destRoot + '/js';

	return gulp.src('src/js/*')
		.pipe(hintAllTheThings())
		.pipe(gulpif(argv.prod, uglify()))
		.pipe(gulp.dest(dest));
});
