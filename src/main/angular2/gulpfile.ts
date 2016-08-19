"use strict";

const gulp = require("gulp");
const del = require("del");
const tsc = require("gulp-typescript");
const merge = require('merge-stream');

const CONFIGS = [require('./config/angular2-poc.json')];
const outDir = '../webapp/WEB-INF/pages/angular2/';

/**
 * Remove build directory.
 */
gulp.task('clean', (cb) => {
    let tasks = CONFIGS.map(config => {
        return del([config.compilerOptions.rootDir + "/build"], cb);
    });
 
    return merge(tasks);

});

/**
 * Compile TypeScript sources and create sourcemaps in build directory.
 */
gulp.task("resources", () => {
    let tasks = CONFIGS.map(config => {
        return gulp.src([config.compilerOptions.rootDir + "/**/*", "!**/*.ts", "!build"])
            .pipe(gulp.dest(config.compilerOptions.rootDir + "build"));
    });

    return merge(tasks);

});

/**
 * Compile TypeScript sources and create sourcemaps in build directory.
 */
gulp.task("compile", () => {
    let tasks = CONFIGS.map(config => {
        let tsResult = gulp.src(config.compilerOptions.rootDir + "/**/*.ts")
            .pipe(tsc(config.compilerOptions));
        return tsResult.js
            .pipe(gulp.dest(config.compilerOptions.rootDir + "build"));
    });

    return merge(tasks);

});

/**
 * Copy all required libraries into build directory.
 * Shared among all apps
 */
gulp.task("libs", () => {
    return gulp.src([
        'es6-shim/es6-shim.min.js',
        'systemjs/dist/system-polyfills.js',
        'systemjs/dist/system.src.js',
        'reflect-metadata/Reflect.js',
        'rxjs/**',
        'zone.js/dist/**',
        '@angular/**'
    ], {cwd: "node_modules/**"}) /* Glob required here. */
    .pipe(gulp.dest(outDir + "lib"));
});

/**
 * Copy to webapps.
 */
gulp.task("dist", ['compile', 'resources', 'libs'], () => {
    let tasks = CONFIGS.map(config => {
        return gulp.src(config.compilerOptions.rootDir + "/build/**")
            .pipe(gulp.dest(config.compilerOptions.outDir));
    });

    return merge(tasks);
});

/**
 * Build the project.
 */
gulp.task("build", ['dist'], () => {
    console.log("Building Angular2 apps ...");
});