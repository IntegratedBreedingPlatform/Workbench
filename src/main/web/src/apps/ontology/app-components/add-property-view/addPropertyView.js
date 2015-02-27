/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['properties', 'variableState']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertyService', 'propertiesService',
		'variableStateService',
		function($scope, $location, $window, propertyService, propertiesService, variableStateService) {

			var ctrl = this;

			// TODO Implement useful error handling

			// Exposed on the controller for testing
			ctrl.genericAndRatherUselessErrorHandler = function(error) {
				if (console) {
					console.log(error);
				}
			};

			// TODO Error handling
			propertiesService.getClasses().then(function(classes) {
				$scope.classes = classes;
			});

			$scope.saveProperty = function(e, property) {
				e.preventDefault();

				// TODO Error handling - only set the property if it saved
				propertyService.saveProperty(property);

				if (variableStateService.updateInProgress()) {

					// FIXME Change to ID
					variableStateService.setProperty(property.name).then(function() {
						$window.history.back();
					}, ctrl.genericAndRatherUselessErrorHandler);
				} else {
					// FIXME Go somewhere more useful
					$location.path('/properties');
				}
			};
		}
	]);
} ());
