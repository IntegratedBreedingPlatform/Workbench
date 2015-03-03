/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', []);

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
			// Variables services (plural)
			getVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/variables');
				return request.then(successHandler, failureHandler);
			},

			getFavouriteVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/variables?favourite=true');
				return request.then(successHandler, failureHandler);
			},

			addVariable: function(variable) {
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/variables', variable);
				return request.then(successHandler, failureHandler);
			},

			// Variable services (on a specific variable)
			getVariable: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/variables/:id');
				return request.then(successHandler, failureHandler);
			},

			// Variable Types services (plural)
			getTypes: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/variableTypes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);

}());
