/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['variables', 'properties', 'methods', 'scales', 'variableState']);

	// TODO Implement useful error handling
	function genericAndRatherUselessErrorHandler(error) {
		if (console) {
			console.log(error);
		}
	}

	app.controller('AddVariableController', ['$scope', '$location', 'variableService', 'variablesService', 'propertiesService',
		'methodsService', 'scalesService', 'variableStateService',
		function($scope, $location, variableService, variablesService, propertiesService, methodsService, scalesService,
			variableStateService) {

			var storedData;

			// Whether or not we want to display the expected range widget
			$scope.showRangeWidget = false;

			// If we were half way through editing, we don't need to fetch everything again - we just need to copy over the stored state
			if (variableStateService.updateInProgress()) {

				storedData = variableStateService.getVariableState();
				$scope.variable = storedData.variable;
				$scope.data = storedData.scopeData;

			} else {

				$scope.data = {};

				propertiesService.getProperties().then(function(properties) {
					$scope.data.properties = properties;
				}, genericAndRatherUselessErrorHandler);

				methodsService.getMethods().then(function(methods) {
					$scope.data.methods = methods;
				}, genericAndRatherUselessErrorHandler);

				scalesService.getScales().then(function(scales) {
					$scope.data.scales = scales;
				}, genericAndRatherUselessErrorHandler);

				variablesService.getTypes().then(function(types) {
					$scope.data.types = types;
				}, genericAndRatherUselessErrorHandler);
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

			$scope.addProperty = function(e) {
				e.preventDefault();

				// Persist the current state of the variable, so we can return to editing once we've finished
				variableStateService.storeVariableState($scope.variable, $scope.data);
				$location.path('/add/property');
			};

			$scope.addMethod = function(e) {
				e.preventDefault();
				alert('Add method');
			};

			$scope.addScale = function(e) {
				e.preventDefault();
				alert('Add scale');
			};
		}
	]);

}());
