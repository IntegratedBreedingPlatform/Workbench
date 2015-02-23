/*global angular*/
'use strict';

(function() {
	var VIEWS_LOCATION = '../static/views/ontology/',
		app = angular.module('ontology', ['ngRoute', 'variables', 'properties', 'addVariable', 'addProperty', 'addMethod', 'addScale']);

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
			})
			.when('/add/method', {
				controller: 'AddMethodController',
				templateUrl: VIEWS_LOCATION + 'addMethodView.html'
			})
			.when('/add/scale', {
				controller: 'AddScaleController',
				templateUrl: VIEWS_LOCATION + 'addScaleView.html'
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
