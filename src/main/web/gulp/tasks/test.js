'use strict';

var gulp = require('gulp'),
	mocha = require('gulp-mocha'),
	protractor = require('gulp-protractor').protractor,
	shell = require('gulp-shell');

gulp.task('webdriver-update', shell.task([
	'./node_modules/protractor/bin/webdriver-manager update'
]));

gulp.task('test', function() {
	return gulp.src(['test/unit/*.js'])
		.pipe(mocha({
			ui: 'bdd',
			reporter: 'nyan'
		}));
});

gulp.task('e2e-test', ['webdriver-update'], function() {
	return gulp.src(['test/e2e/*.js'])
		.pipe(protractor({
			configFile: './test/protractor.conf.js',
			args: ['--baseUrl', 'http://127.0.0.1:8000']
		}))
		.on('error', function(e) {
			throw e;
		});
});
