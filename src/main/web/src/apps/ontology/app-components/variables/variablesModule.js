/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['list', 'panel']);

	app.controller('VariablesController', ['$scope', 'variablesService', function($scope, variablesService) {
		var ctrl = this;
		this.variables = [];

		ctrl.colHeaders = ['Name', 'Property', 'Method', 'Scale', 'action-favourite'];

		variablesService.getVariables().then(function(variables) {

			ctrl.variables = variables.map(function(item) {
				return {
					id: item.id,
					Name: item.name,
					Property: item.property.name,
					Method: item.method.name,
					Scale: item.scale.name,
					'action-favourite': item.favourite
				};
			});
		});

		$scope.panelOpen = {show: false};

		$scope.showVariableDetails = function() {

			variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
				$scope.selectedVariable = variable;
			});

			$scope.panelOpen.show = true;
		};

		$scope.selectedItem = {id: null};
		$scope.selectedVariable = null;

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
			}
		};
	}]);

}());
