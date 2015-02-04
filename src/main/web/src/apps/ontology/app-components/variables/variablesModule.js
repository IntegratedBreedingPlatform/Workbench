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
			if (!angular.isObject(response.data) || !response.data.message) {
				return $q.reject('An unknown error occurred.');
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
