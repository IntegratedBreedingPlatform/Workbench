/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['properties']);

	app.controller('AddVariableController', ['$scope', '$location', 'addVariableService', 'propertiesService',
		function($scope, $location, addVariableService, propertiesService) {

		var ctrl = this;

		ctrl.properties = [];
		ctrl.methods = [];
		ctrl.scales = [];
		ctrl.types = [];

		propertiesService.getProperties().then(function(properties) {
			ctrl.properties = properties;
		});

		$scope.numericVariable = true;

		// Restore state in case we were half way through creating variable
		$scope.variable = angular.copy(addVariableService.getVariableState());

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
	}]);

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
			}
		};
	}]);

}());
