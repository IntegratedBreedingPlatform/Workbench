/*global angular*/
'use strict';

(function() {
	var app = angular.module('variablesView', ['list', 'panel', 'variables', 'variableDetails']);

	function transformToDisplayFormat(variables) {
		// TODO: check that variable has an ID and name
		return variables.map(function(variable){
			return {
				id: variable.id,
				Name: variable.name,
				Property: variable.property && variable.property.name || '',
				Method: variable.method && variable.method.name || '',
				Scale: variable.scale && variable.scale.name || '',
				'action-favourite': variable.favourite
			};
		});
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

			$scope.selectedItem = {id: null};
			$scope.selectedVariable = null;
		}
	]);

}());
