/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['properties']);

	app.controller('AddVariableController', ['$scope', '$location', 'addVariableService', 'propertiesService',
		function($scope, $location, addVariableService, propertiesService) {

			$scope.methods = [];
			$scope.scales = [];
			$scope.types = [];

			$scope.properties = [];

			// Restore state in case we were half way through creating variable
			$scope.variable = angular.copy(addVariableService.getVariableState());

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

			$scope.numericVariable = true;

			$scope.saveVariable = function(e, variable) {
				e.preventDefault();
				addVariableService.saveVariable(variable);
			};

			$scope.addProperty = function(e, variable) {
				e.preventDefault();

				addVariableService.updateVariableState(variable);

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

	app.service('addVariableService', [function() {

		var variable = {};

		return {
			updateVariableState: function(updatedVariable) {
				variable = angular.copy(updatedVariable);
			},

			saveVariable: function(variable) {
				// TODO Call actual save functionality
				console.log('Saving variable');

				// If successful..
				variable = {};
			},

			getVariableState: function() {
				return variable;
			},

			addProperty: function(property) {
				variable.property = property;
			}
		};
	}]);

}());
