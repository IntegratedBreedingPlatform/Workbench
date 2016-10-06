'use strict';

var gulp = require('gulp'),
	del = require('del'),
	fs = require('fs'),
	srcRoot = 'src/appsNg2',
	destRoot = '../webapp/WEB-INF/pages/angular2',
	es = require('event-stream'),
	path = require('path'),
	typescript = require('gulp-typescript');

function getFoldersNg2(dir) {
	return fs.readdirSync(dir)
		.filter(function(file) {
			return fs.statSync(path.join(dir, file)).isDirectory();
		});
}

gulp.task('angular2Clean', function() {
  return del.sync(['src/appsNg2/**/build']);
});

gulp.task('angular2Ts', function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {
		var tsProject = typescript.createProject(path.join(srcRoot, folder, 'tsconfig.json'));

		return gulp.src(path.join(srcRoot, folder, '**/*.ts'))
			.pipe(typescript(tsProject))
			.pipe(gulp.dest(path.join(srcRoot, folder, 'build')));
	});

	return es.merge.apply(null, tasks);
});

// Give clean task a higher priority
gulp.task('angular2PrioritizeClean', ['angular2Clean']);
gulp.task('angular2CleanBuild', ['angular2PrioritizeClean', 'angular2Clean', 'angular2Ts']);

gulp.task('angular2Resources', ['angular2Ts'], function() {
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
});

gulp.task('angular2Dist', ['angular2Resources'], function() {
	var folders = getFoldersNg2(srcRoot);

	var tasks = folders.map(function(folder) {
		
		return gulp.src(path.join(srcRoot, folder, 'build/**'))
			.pipe(gulp.dest(path.join(destRoot , folder)));
	});

	return es.merge.apply(null, tasks);

});

// Give clean build task a higher priority
gulp.task('angular2PrioritizeCleanBuild', ['angular2CleanBuild']);
gulp.task('angular2CleanDist', ['angular2PrioritizeCleanBuild', 'angular2CleanBuild', 'angular2Dist']);

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

gulp.task('angular2', ['angular2CleanDist', 'angular2Libs']);
