/*global angular*/
'use strict';

(function() {
	var VIEWS_LOCATION = '../static/views/ontology/',
		app = angular.module('ontology', ['ngRoute', 'variables', 'properties']);

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
			});
	}]);
}());

/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', []);

	listModule.directive('list', function() {
		return {
			restrict: 'E',
			scope: {
				data: '=data'
			},
			templateUrl: '../static/views/ontology/listView.html'
		};
	});
}());

/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['list']);

	app.controller('PropertiesController', ['$scope', 'propertiesService', function($scope, propertiesService) {
		var ctrl = this;
		this.properties = [];

		propertiesService.getProperties().then(function(properties) {
			ctrl.properties = properties;
		});
	}]);

	app.service('propertiesService', ['$http', '$q', function($http, $q) {
		function successHandler(response) {
			return response.data;
		}

		function failureHandler(response) {
			var errorMessage = 'An unknown error occurred.';

			if (!angular.isObject(response.data)) {
				if (response.status === 400) {
					errorMessage = 'Request was malformed.';
				}
				return $q.reject(errorMessage);
			}
		}

		return {
			getProperties: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties');

				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());

/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['list']);

	app.controller('VariablesController', ['$scope', 'variablesService', function($scope, variablesService) {
		var ctrl = this;
		this.variables = [];

		variablesService.getVariables().then(function(variables) {
			ctrl.variables = variables;
		});
	}]);

	app.service('variablesService', ['$http', '$q', function($http, $q) {
		function successHandler(response) {
			return response.data;
		}

		function failureHandler(response) {
			var errorMessage = 'An unknown error occurred.';

			if (!angular.isObject(response.data)) {
				if (response.status === 400) {
					errorMessage = 'Request was malformed.';
				}
				return $q.reject(errorMessage);
			}
		}

		return {
			getVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/variables');

				return request.then(successHandler, failureHandler);
			}
		};
	}]);

}());
