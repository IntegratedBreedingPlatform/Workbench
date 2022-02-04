var gulp = require('gulp');
var flatten = require('gulp-flatten');

gulp.task('lib', ['brapi-fieldmap', 'common-libs', 'brapi-study-comparison', 'brapi-sync']);
gulp.task('brapi-fieldmap', ['brapi-fieldmap-js', 'brapi-fieldmap-css']);
gulp.task('brapi-sync', ['brapi-sync-main', 'brapi-sync-static']);


gulp.task('common-libs', function() {

	return gulp.src([
		'@solgenomics/brapijs/build/BrAPI.js',
		'@turf/turf/turf.js',
		'leaflet/dist/leaflet.js',
		'leaflet-path-transform/dist/L.Path.Transform.js',
		'leaflet-search/dist/leaflet-search.src.js',
	], { cwd: 'node_modules/**' }) /* Glob required here. */
		.pipe(gulp.dest('../webapp/WEB-INF/static/js/lib'));
});

gulp.task('brapi-fieldmap-js', function() {

	return gulp.src([
		'@solgenomics/brapi-fieldmap/dist/BrAPIFieldmap.js'
	], { cwd: 'node_modules/**' }) /* Glob required here. */
		.pipe(gulp.dest('../webapp/WEB-INF/static/js/lib'));
});

gulp.task('brapi-fieldmap-css', function() {

	return gulp.src([
		'leaflet-search/dist/leaflet-search.min.css',
		'leaflet-search/images/loader.gif',
		'leaflet-search/images/search-icon.png',
		'leaflet/dist/leaflet.css'
	], { cwd: 'node_modules/**' }) /* Glob required here. */
		.pipe(gulp.dest('../webapp/WEB-INF/static/lib'));
});

gulp.task('brapi-study-comparison', function() {

	return gulp.src([
		'@solgenomics/brapi-study-comparison/build/StudyComparison.js'
	], { cwd: 'node_modules/**' }) /* Glob required here. */
		.pipe(gulp.dest('../webapp/WEB-INF/static/js/lib'));
});


gulp.task('brapi-sync-main', function() {
	return gulp.src([
		'brapi-sync/dist/brapi-sync-angular/*.*'
	], { cwd: 'node_modules/**' })/* Glob required here. */
		.pipe(flatten())
		.pipe(gulp.dest('../webapp/WEB-INF/pages/brapi-sync'));
});

gulp.task('brapi-sync-static', function() {
	return gulp.src([
		'brapi-sync/dist/brapi-sync-angular/static/**'
	], { cwd: 'node_modules/**' })/* Glob required here. */
		.pipe(flatten())
		.pipe(gulp.dest('../webapp/WEB-INF/pages/brapi-sync/static'));
});
