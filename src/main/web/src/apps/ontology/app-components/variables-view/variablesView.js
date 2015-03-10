/*global angular*/
'use strict';

(function() {
	var app = angular.module('variablesView', ['list', 'panel', 'variables', 'variableDetails']);

	function transformVariableToDisplayFormat(variable, id) {
		return {
				id: variable.id || id,
				Name: variable.name,
				Property: variable.property && variable.property.name || '',
				Method: variable.method && variable.method.name || '',
				Scale: variable.scale && variable.scale.name || '',
				'action-favourite': variable.favourite
			};
	}

	function transformToDisplayFormat(variables) {
		// TODO: check that variable has an ID and name
		return variables.map(transformVariableToDisplayFormat);
	}

	app.controller('VariablesController', ['$scope', 'variablesService', 'panelService',
		function($scope, variablesService, panelService) {
			var ctrl = this;
			ctrl.variables = [];
			ctrl.favouriteVariables = [];

			/* Exposed for testing */
			ctrl.transformToDisplayFormat = transformToDisplayFormat;

			$scope.panelName = 'variables';

			ctrl.colHeaders = ['Name', 'Property', 'Method', 'Scale', 'action-favourite'];

			variablesService.getFavouriteVariables().then(function(variables) {
				ctrl.favouriteVariables = ctrl.transformToDisplayFormat(variables);
			});

			variablesService.getVariables().then(function(variables) {
				ctrl.variables = ctrl.transformToDisplayFormat(variables);
			});

			$scope.showVariableDetails = function() {
				variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
					$scope.selectedVariable = variable;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.updateSelectedVariable = function(updatedVariable) {

				var selectedVariableIndex = -1,
					favouriteVariableIndex = -1,
					transformedVariable = transformVariableToDisplayFormat(updatedVariable, $scope.selectedItem.id);

				$scope.selectedVariable = updatedVariable;

				ctrl.variables.some(function(variable, index) {
					if (variable.id === $scope.selectedItem.id) {
						selectedVariableIndex = index;
						return true;
					}
				});

				ctrl.favouriteVariables.some(function(variable, index) {
					if (variable.id === $scope.selectedItem.id) {
						favouriteVariableIndex = index;
						return true;
					}
				});

				// TODO Error handling
				if (selectedVariableIndex !== -1) {
					ctrl.variables[selectedVariableIndex] = transformedVariable;
				}

				// TODO Remove if no longer a favourite
				if (favouriteVariableIndex !== -1) {
					ctrl.favouriteVariables[favouriteVariableIndex] = transformedVariable;
				}
			};

			$scope.selectedItem = {id: null};
			$scope.selectedVariable = {};
		}
	]);

}());
