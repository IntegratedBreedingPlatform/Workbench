/*global angular*/
'use strict';

(function() {
	var app = angular.module('variablesView', ['list', 'panel', 'variables', 'variableDetails']);


	function transformDetailedVariableToDisplayFormat(variable, id) {
		return {
			id: id,
			Name: variable.name,
			Property: variable.propertySummary && variable.propertySummary.name || '',
			Method: variable.methodSummary && variable.methodSummary.name || '',
			Scale: variable.scale && variable.scale.name || '',
			'action-favourite': variable.favourite
		};
	}

	function transformVariableToDisplayFormat(variable) {
		return {
			id: variable.id,
			Name: variable.name,
			Property: variable.propertySummary && variable.propertySummary.name || '',
			Method: variable.methodSummary && variable.methodSummary.name || '',
			Scale: variable.scaleSummary && variable.scaleSummary.name || '',
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

			ctrl.transformToDisplayFormat = transformToDisplayFormat;
			/* Exposed for testing */
			ctrl.transformVariableToDisplayFormat = transformVariableToDisplayFormat;
			ctrl.transformDetailedVariableToDisplayFormat = transformDetailedVariableToDisplayFormat;

			$scope.panelName = 'variables';

			ctrl.colHeaders = ['Name', 'Property', 'Method', 'Scale', 'action-favourite'];

			variablesService.getFavouriteVariables().then(function(variables) {
				ctrl.favouriteVariables = ctrl.transformToDisplayFormat(variables);
			});

			variablesService.getVariables().then(function(variables) {
				ctrl.variables = ctrl.transformToDisplayFormat(variables);
			});

			$scope.showVariableDetails = function() {

				// Ensure the previously selected variable doesn't show in the panel before we've retrieved the new one
				$scope.selectedVariable = null;

				variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
					$scope.selectedVariable = variable;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.updateSelectedVariable = function(updatedVariable) {

				var selectedVariableIndex = -1,
					favouriteVariableIndex = -1,
					transformedVariable = transformDetailedVariableToDisplayFormat(updatedVariable, $scope.selectedItem.id);

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

				// Not much we can really do if we don't find it in the list. Just don't update.
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
