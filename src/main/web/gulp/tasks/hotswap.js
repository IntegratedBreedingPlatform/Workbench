'use strict';

var changed = require('gulp-changed'),
	argv = require('yargs').argv,
	parser = require('properties-parser'),
	gulp = require('gulp');

gulp.task('hotswap', function() {

	var properties,
		tomcatLocation,
		dest;

	if (argv.env) {

		properties = parser.read('gulp.properties');
		tomcatLocation = properties[argv.env];

		if (!tomcatLocation) {
			console.warn('Could not find property ' + argv.env + ' in gulp.properties.');
		} else {
			dest = tomcatLocation + '/webapps/ibpworkbench/WEB-INF';

			return gulp.src('../webapp/WEB-INF/**')
				.pipe(changed(dest))
				.pipe(gulp.dest(dest));
		}

	} else {
		console.warn('Please provide the env property if you wish to hotswap.');
	}
});
