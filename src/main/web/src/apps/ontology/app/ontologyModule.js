/*global angular*/
'use strict';

(function() {
	var VIEWS_LOCATION = '../static/views/ontology/',
		app = angular.module('ontology', ['ngRoute', 'variables', 'properties', 'addVariable', 'addProperty']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/properties', {
				controller: 'PropertiesController',
				controllerAs: 'propsCtrl',
				templateUrl: VIEWS_LOCATION + 'propertiesView.html'
			})
			.when('/variables', {
				controller: 'VariablesController',
				controllerAs: 'varsCtrl',
				templateUrl: VIEWS_LOCATION + 'variablesView.html'
			})
			.when('/add/variable', {
				controller: 'AddVariableController',
				templateUrl: VIEWS_LOCATION + 'addVariableView.html'
			})
			.when('/add/property', {
				controller: 'AddPropertyController',
				templateUrl: VIEWS_LOCATION + 'addPropertyView.html'
			});
	}]);

	app.controller('OntologyController', ['$scope', '$location', function($scope, $location) {
		$scope.panelOpen = {show: false};

		$scope.addNewSelection = function() {
			$scope.panelOpen.show = true;
		};

		$scope.addNew = function(e, path) {
			e.preventDefault();
			$location.path('/add/' + path);
			$scope.panelOpen.show = false;
		};
	}]);

}());
