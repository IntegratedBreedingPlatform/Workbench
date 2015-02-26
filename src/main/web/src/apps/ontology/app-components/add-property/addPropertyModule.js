/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['properties', 'variableState']);

	// TODO Implement useful error handling
	function genericAndRatherUselessErrorHandler(error) {
		if (console) {
			console.log(error);
		}
	}

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertyService', 'propertiesService',
		'variableStateService',
		function($scope, $location, $window, propertyService, propertiesService, variableStateService) {

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
					}, genericAndRatherUselessErrorHandler);
				} else {
					// FIXME Go somewhere more useful
					$location.path('/properties');
				}
			};
		}
	]);
} ());
