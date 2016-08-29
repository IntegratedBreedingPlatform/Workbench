'use strict';

var gulp = require('gulp'),
	del = require('del'),
	fs = require('fs'),
	srcRoot = 'src/appsNg2',
	destRoot = '../webapp/WEB-INF/pages/angular2',
	es = require('event-stream'),
	path = require('path'),
	typescript = require('gulp-typescript'),
	tscConfig = require('./tsconfig.json');

function getFoldersNg2(dir) {
	return fs.readdirSync(dir)
		.filter(function(file) {
			return fs.statSync(path.join(dir, file)).isDirectory();
		});
}

gulp.task('angular2Clean', function() {
  return del(['src/appsNg2/**/build']);
});

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
        'zone.js/dist/**',
        '@angular/**'
    ], { cwd: 'node_modules/**' }) /* Glob required here. */
        .pipe(gulp.dest(path.join(destRoot, 'lib')));
});

gulp.task('angular2', ['angular2Clean', 'build'], function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {
		
		return gulp.src(path.join(srcRoot, folder, '**/*.ts'))
			.pipe(typescript(tscConfig.compilerOptions))
			.pipe(gulp.dest(path.join(srcRoot, folder, 'build')));
	});

	return es.merge.apply(null, tasks);
});

gulp.task('angular2Resources', ['angular2'], function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {
		
    // TODO get all js recursively, exclude build folder
		return gulp.src([path.join(srcRoot, folder, '*.js'),
                     path.join(srcRoot, folder, '**/*.html')])
			.pipe(gulp.dest(path.join(srcRoot, folder, 'build')));
	});

	return es.merge.apply(null, tasks);
});

gulp.task('angular2Dist', ['angular2', 'angular2Resources', 'angular2Libs'], function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {
		
		return gulp.src(path.join(srcRoot, folder, 'build/**'))
			.pipe(gulp.dest(path.join(destRoot , folder)));
	});

	return es.merge.apply(null, tasks);

});
