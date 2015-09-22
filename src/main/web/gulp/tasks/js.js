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

	var srcRoot = 'src/js/lib/**',
		src = argv.release ? srcRoot + '/*.js' : srcRoot,
		dest = destRoot + '/js/lib';

	return gulp.src(src)
		.pipe(changed(dest))
		.pipe(gulp.dest(dest));
});

gulp.task('clientJs', function() {

	var dest = destRoot + '/js';

	return gulp.src('src/js/*')
		.pipe(hintAllTheThings())
		.pipe(gulpif(argv.release, uglify()))
		.pipe(gulp.dest(dest));
});
