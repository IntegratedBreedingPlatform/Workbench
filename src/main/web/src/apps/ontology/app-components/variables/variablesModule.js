/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['list', 'panel']);

	app.controller('VariablesController', ['variablesService', function(variablesService) {
		var ctrl = this;
		this.variables = [];

		ctrl.colHeaders = ['Name', 'Property', 'Method', 'Scale', 'action-favourite'];

		variablesService.getVariables().then(function(variables) {

			ctrl.variables = variables.map(function(item) {
				return {
					Name: item.name,
					Property: item.property.name,
					Method: item.method.name,
					Scale: item.scale.name,
					'action-favourite': item.favourite
				};
			});
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
