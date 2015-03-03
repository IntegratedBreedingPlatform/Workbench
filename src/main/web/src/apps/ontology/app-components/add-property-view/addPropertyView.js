/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['ngSanitize', 'ui.select', 'properties', 'variableState', 'utilities']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertyService', 'propertiesService',
		'variableStateService', 'serviceUtilities',
		function($scope, $location, $window, propertyService, propertiesService, variableStateService, serviceUtilities) {

			$scope.classes = [];

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
					}, serviceUtilities.genericAndRatherUselessErrorHandler);
				} else {
					// FIXME Go somewhere more useful
					$location.path('/properties');
				}
			};
		}
	]);
} ());
