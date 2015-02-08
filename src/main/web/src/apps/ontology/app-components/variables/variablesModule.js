/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['list']);

	app.controller('VariablesController', ['variablesService', function(variablesService) {
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
