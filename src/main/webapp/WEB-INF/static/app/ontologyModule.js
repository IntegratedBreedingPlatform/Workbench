/*global angular*/
'use strict';

var VIEWS_LOCATION = '../static/views/components',
	app = angular.module('ontology', ['ngRoute']);

app.config(function($routeProvider) {

	$routeProvider
		.when('/properties', {
			templateUrl: VIEWS_LOCATION + '/properties/propertiesView.html'
		})
		.when('/variables', {
			templateUrl: VIEWS_LOCATION + '/variables/variablesView.html'
		});
});
