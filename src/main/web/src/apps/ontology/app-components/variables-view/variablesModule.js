/*global angular*/
'use strict';

(function() {
	var app = angular.module('variablesView', ['list', 'panel', 'variables']);

	function toDisplayFormat(variable) {
		// TODO: check that variable has an ID and name
		return {
			id: variable.id,
			Name: variable.name,
			Property: variable.property && variable.property.name || '',
			Method: variable.method && variable.method.name || '',
			Scale: variable.scale && variable.scale.name || '',
			'action-favourite': variable.favourite
		};
	}

	app.controller('VariablesController', ['$scope', 'variablesService', function($scope, variablesService) {
		var ctrl = this;
		this.variables = [];
		this.favouriteVariables = [];

		ctrl.colHeaders = ['Name', 'Property', 'Method', 'Scale', 'action-favourite'];

		variablesService.getFavouriteVariables().then(function(variables) {
			ctrl.favouriteVariables = variables.map(toDisplayFormat);
		});

		variablesService.getVariables().then(function(variables) {
			ctrl.variables = variables.map(toDisplayFormat);
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

		/* Exposed for testing */
		this.toDisplayFormat = toDisplayFormat;
	}]);

}());
