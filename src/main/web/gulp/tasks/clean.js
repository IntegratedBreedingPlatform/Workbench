'use strict';

var gulp = require('gulp'),
	del = require('del');

gulp.task('clean', function(cb) {
	del(['../webapp/WEB-INF/static', '../webapp/WEB-INF/pages','../resources/mail-templates'], {force: true}, cb);
});