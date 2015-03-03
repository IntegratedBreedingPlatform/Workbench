/*global angular*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['variables', 'properties', 'methods', 'scales', 'variableState']);

	app.controller('AddVariableController', ['$scope', '$location', 'variableService', 'variablesService', 'propertiesService',
		'methodsService', 'scalesService', 'variableStateService',

		function($scope, $location, variableService, variablesService, propertiesService, methodsService, scalesService,
			variableStateService) {

			var ctrl = this,
				storedData;

			// The select2 input needs to be able to call length on the types array before the data is returned.
			$scope.data = {
				types: []
			};

			// TODO Implement useful error handling

			// Exposed on the controller for testing
			ctrl.genericAndRatherUselessErrorHandler = function(error) {
				if (console) {
					console.log(error);
				}
			};

			// Whether or not we want to display the expected range widget
			$scope.showRangeWidget = false;

			// If we were half way through editing, we don't need to fetch everything again - we just need to copy over the stored state
			if (variableStateService.updateInProgress()) {

				storedData = variableStateService.getVariableState();
				$scope.variable = storedData.variable;
				$scope.data = storedData.scopeData;

			} else {

				$scope.variable = {};

				propertiesService.getProperties().then(function(properties) {
					$scope.data.properties = properties;
				}, ctrl.genericAndRatherUselessErrorHandler);

				methodsService.getMethods().then(function(methods) {
					$scope.data.methods = methods;
				}, ctrl.genericAndRatherUselessErrorHandler);

				scalesService.getScales().then(function(scales) {
					$scope.data.scales = scales;
				}, ctrl.genericAndRatherUselessErrorHandler);

				variablesService.getTypes().then(function(types) {
					$scope.data.types = types;
				}, ctrl.genericAndRatherUselessErrorHandler);
			}

			// Show the expected range widget if the chosen scale has a numeric datatype
			$scope.$watch('variable.scale.dataType.name', function(newValue) {
				$scope.showRangeWidget = newValue === 'Numeric';
			});

			$scope.saveVariable = function(e, variable) {
				e.preventDefault();
				// TODO Error handling

				variableService.saveVariable(variable);
				variableStateService.reset();

				// FIXME Go somewhere more useful
				$location.path('/variables');
			};

			$scope.addNew = function(e, path) {
				e.preventDefault();

				// Persist the current state of the variable, so we can return to editing once we've finished
				variableStateService.storeVariableState($scope.variable, $scope.data);
				$location.path('/add/' + path);
			};
		}
	]);

}());
