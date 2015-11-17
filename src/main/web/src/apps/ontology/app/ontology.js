/*global angular*/
'use strict';

(function() {
	var VIEWS_LOCATION = 'static/views/ontology/',
		app = angular.module('ontology', ['ngRoute', 'variablesView', 'propertiesView', 'methodsView', 'scalesView', 'addVariable',
		'addProperty', 'addMethod', 'addScale', 'pascalprecht.translate', 'keyTrap', 'config', 'panel', 'bmsAuth']);

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
			.when('/methods', {
				controller: 'MethodsController',
				controllerAs: 'methodsCtrl',
				templateUrl: VIEWS_LOCATION + 'methodsView.html'
			})
			.when('/scales', {
				controller: 'ScalesController',
				controllerAs: 'scalesCtrl',
				templateUrl: VIEWS_LOCATION + 'scalesView.html'
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
			})
			.otherwise({
				redirectTo: '/variables'
			});
	}]);

	app.config(['$translateProvider', function($translateProvider) {
		$translateProvider.useStaticFilesLoader({
			prefix: '/ibpworkbench/controller/static/resources/locale-',
			suffix: '.json'
		});
		$translateProvider.preferredLanguage('en');
	}]);
	
	app.config(['$httpProvider', function($httpProvider) {
		$httpProvider.interceptors.push('authInterceptor');
		$httpProvider.interceptors.push('authExpiredInterceptor');
	}]);
	
	app.config(['localStorageServiceProvider', function(localStorageServiceProvider){
		localStorageServiceProvider.setPrefix('bms');
	}]);

	app.controller('OntologyController', ['$scope', '$location', '$window', 'panelService',
		function($scope, $location, $window, panelService) {

			var urls = ['methods', 'variables', 'scales', 'properties'];

			$scope.panelName = 'addNew';
			$scope.activeTab = 'variables';

			$scope.addNewSelection = function() {
				panelService.showPanel($scope.panelName);
			};

			$scope.addNew = function(e, path) {
				e.preventDefault();
				panelService.hidePanel();
				$location.path('/add/' + path);
				//Deselect all tabs on redirection to the new item view
				$scope.activeTab = '';
			};

			$scope.$on('$locationChangeStart', function(event, newUrl, oldUrl) {
				urls.some(function(url) {
					if (newUrl.indexOf(url, newUrl.length - url.length) !== -1) {
						$scope.activeTab = url;
					}
				});
				$scope.previousUrl = oldUrl;
			});

			$scope.setAsActive = function(value) {
				$scope.activeTab = value;
			};
		}
	]);

}());
