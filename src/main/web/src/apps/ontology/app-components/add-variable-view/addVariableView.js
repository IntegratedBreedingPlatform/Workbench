/*global angular*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['variables', 'properties', 'methods', 'scales', 'variableState', 'utilities']);

	app.controller('AddVariableController', ['$scope', '$location', 'variableService', 'variablesService', 'propertiesService',
		'methodsService', 'scalesService', 'variableStateService', 'serviceUtilities',

		function($scope, $location, variableService, variablesService, propertiesService, methodsService, scalesService,
			variableStateService, serviceUtilities) {

			var storedData;

			// The select2 input needs to be able to call length on the types array before the data is returned.
			$scope.data = {
				types: []
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
				}, serviceUtilities.genericAndRatherUselessErrorHandler);

				methodsService.getMethods().then(function(methods) {
					$scope.data.methods = methods;
				}, serviceUtilities.genericAndRatherUselessErrorHandler);

				scalesService.getScales().then(function(scales) {
					$scope.data.scales = scales;
				}, serviceUtilities.genericAndRatherUselessErrorHandler);

				variablesService.getTypes().then(function(types) {
					$scope.data.types = types;
				}, serviceUtilities.genericAndRatherUselessErrorHandler);
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
