/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['variables', 'properties', 'methods', 'scales']);

	app.controller('AddVariableController', ['$scope', '$location', 'variableService', 'variablesService', 'propertiesService',
		'methodsService', 'scalesService',
		function($scope, $location, variableService, variablesService, propertiesService, methodsService, scalesService) {

			// Restore state in case we were half way through creating variable
			$scope.variable = angular.copy(variableService.getVariableState());

			// TODO Error handling
			propertiesService.getProperties().then(function(properties) {
				$scope.properties = properties;

				// FIXME - Change to ID
				// Select the currently selected property if there is one
				if ($scope.variable.property) {
					$scope.properties.some(function(prop) {
						if (prop.name === $scope.variable.property.name) {
							$scope.variable.property = prop;
							return true;
						}
					});
				}
			});

			// TODO Error handling
			methodsService.getMethods().then(function(methods) {
				$scope.methods = methods;
			});

			// TODO Error handling
			scalesService.getScales().then(function(scales) {
				$scope.scales = scales;
			});

			// TODO Error handling
			variablesService.getTypes().then(function(types) {
				$scope.types = types;
			});

			$scope.numericVariable = true;

			$scope.saveVariable = function(e, variable) {
				e.preventDefault();
				// TODO Error handling
				variableService.saveVariable(variable);
			};

			$scope.addProperty = function(e, variable) {
				e.preventDefault();

				// TODO Error handling
				variableService.updateVariableState(variable);

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
