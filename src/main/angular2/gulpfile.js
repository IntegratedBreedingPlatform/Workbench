'use strict';
var gulp = require('gulp');
var del = require('del');
var tsc = require('gulp-typescript');
var merge = require('merge-stream');
var CONFIGS = [require('./config/angular2-poc.json')];
var outDir = '../webapp/WEB-INF/pages/angular2/';

/**
 * Remove build directory.
 */
gulp.task('clean', function (cb) {
    try {
        var tasks = CONFIGS.map(function (config) {
            return del([config.compilerOptions.rootDir + '/build'], cb);
        });
        return merge(tasks);
    }
    catch (e) {
        // TODO [TypeError: source.once is not a function]
        console.log(e);
    }
});

/**
 * Compile TypeScript sources and create sourcemaps in build directory.
 */
gulp.task('resources', function () {
    var tasks = CONFIGS.map(function (config) {
        return gulp.src([config.compilerOptions.rootDir + '/**/*', '!**/*.ts', '!build'])
            .pipe(gulp.dest(config.compilerOptions.rootDir + 'build'));
    });
    return merge(tasks);
});

/**
 * Compile TypeScript sources and create sourcemaps in build directory.
 */
gulp.task('compile', function () {
    var tasks = CONFIGS.map(function (config) {
        var tsResult = gulp.src(config.compilerOptions.rootDir + '/**/*.ts')
            .pipe(tsc(config.compilerOptions));
        return tsResult.js
            .pipe(gulp.dest(config.compilerOptions.rootDir + 'build'));
    });
    return merge(tasks);
});

/**
 * Copy all required libraries into build directory.
 * Shared among all apps
 */
gulp.task('libs', function () {
    return gulp.src([
        'es6-shim/es6-shim.min.js',
        'systemjs/dist/system-polyfills.js',
        'systemjs/dist/system.src.js',
        'reflect-metadata/Reflect.js',
        'rxjs/**',
        'zone.js/dist/**',
        '@angular/**'
    ], { cwd: 'node_modules/**' }) /* Glob required here. */
        .pipe(gulp.dest(outDir + 'lib'));
});

/**
 * Copy to webapps.
 */
gulp.task('dist', ['compile', 'resources', 'libs'], function () {
    var tasks = CONFIGS.map(function (config) {
        return gulp.src(config.compilerOptions.rootDir + '/build/**')
            .pipe(gulp.dest(config.compilerOptions.outDir));
    });
    return merge(tasks);
});

/**
 * Build the project.
 */
gulp.task('build', ['dist'], function () {
    console.log('Building Angular2 apps ...');
});
