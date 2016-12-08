'use strict';

var gulp = require('gulp'),
	del = require('del'),
	fs = require('fs'),
	gulpif = require('gulp-if'),
	argv = require('yargs').argv,
	srcRoot = 'src/appsNg2',
	destRoot = '../webapp/WEB-INF/pages/angular2',
	es = require('event-stream'),
	path = require('path'),
	typescript = require('gulp-typescript'),
	babel = require('gulp-babel'),
	uglify = require('gulp-uglify'),
	sourcemaps = require('gulp-sourcemaps');

function getFoldersNg2(dir) {
	return fs.readdirSync(dir)
		.filter(function(file) {
			return fs.statSync(path.join(dir, file)).isDirectory();
		});
}

gulp.task('angular2Clean', function() {
  return del.sync(['src/appsNg2/**/build']);
});

var angular2Ts = function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {
		var tsProject = typescript.createProject(path.join(srcRoot, folder, 'tsconfig.json'));

		return gulp.src(path.join(srcRoot, folder, '**/*.ts'))
		  .pipe(sourcemaps.init())
			.pipe(typescript(tsProject))
			// Transpile ES6 to ES5 using ES2015 preset, needed because PhantomJS does not support ES6
		  	.pipe(babel({ presets: ['es2015'] }))
			.pipe(gulpif(argv.release, uglify()))
			.pipe(sourcemaps.write('.'))
			.pipe(gulp.dest(path.join(srcRoot, folder, 'build')));
	});

	return es.merge.apply(null, tasks);
};

gulp.task('angular2Ts', angular2Ts);
gulp.task('angular2TsWithClean', ['angular2Clean'], angular2Ts);

var angular2Resources = function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {

	return gulp.src([path.join(srcRoot, folder, '*.js'),
					 path.join(srcRoot, folder, '**/*.css'),
					 path.join(srcRoot, folder, '**/*.json'),
					 path.join(srcRoot, folder, '**/*.html'),
					 "!" + path.join(srcRoot, folder, 'build', '**')])
		   .pipe(gulp.dest(path.join(srcRoot, folder, 'build')));
	});

	return es.merge.apply(null, tasks);
};

gulp.task('angular2Resources', ['angular2Ts'], angular2Resources);
gulp.task('angular2ResourcesWithClean', ['angular2TsWithClean'], angular2Resources);

var angular2Dist = function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {

		return gulp.src([path.join(srcRoot, folder, 'build/**'),"!"
		  + path.join(srcRoot, folder, 'build/**/*.spec.js'), "!"
			+ path.join(srcRoot, folder, 'build/**/*.spec.js.map')])
			.pipe(gulp.dest(path.join(destRoot , folder)));
	});

	return es.merge.apply(null, tasks);

};

gulp.task('angular2Dist', ['angular2Resources'], angular2Dist);
gulp.task('angular2DistWithClean', ['angular2ResourcesWithClean'], angular2Dist);

/**
 * Copy all required libraries into build directory.
 * Shared among all apps
 */
gulp.task('angular2Libs', function () {
    return gulp.src([
        'es6-shim/es6-shim.min.js',
        'systemjs/dist/system-polyfills.js',
        'systemjs/dist/system.src.js',
        'reflect-metadata/Reflect.js',
        'rxjs/**',
        'zone.js/dist/zone.js',
        '@angular/**/bundles/*.umd.min.js'
    ], { cwd: 'node_modules/**' }) /* Glob required here. */
        .pipe(gulp.dest(path.join(destRoot, 'lib')));
});

gulp.task('angular2', ['angular2DistWithClean', 'angular2Libs']);
