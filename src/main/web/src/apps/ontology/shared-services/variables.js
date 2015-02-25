/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('variables', []);

	app.service('variableService', [function() {

		var variable = {};

		return {
			updateVariableState: function(updatedVariable) {
				variable = angular.copy(updatedVariable);
			},

			saveVariable: function(variable) {
				// TODO Call actual save functionality
				console.log('Saving variable');

				// If successful..
				variable = {};
			},

			getVariableState: function() {
				return variable;
			},

			setProperty: function(property) {
				variable.property = property;
			}
		};
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
			getVariable: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/variables/:id');
				return request.then(successHandler, failureHandler);
			},

			getVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/variables');
				return request.then(successHandler, failureHandler);
			},

			getTypes: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/variableTypes');
				return request.then(successHandler, failureHandler);
			},

			getFavouriteVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/variables?favourite=true');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);

}());
