/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['ngSanitize', 'ui.select', 'properties', 'variableState', 'utilities']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertiesService', 'variableStateService',
		'serviceUtilities',
		function($scope, $location, $window, propertiesService, variableStateService, serviceUtilities) {

			$scope.classes = [];

			propertiesService.getClasses().then(function(classes) {
				$scope.classes = classes;
			}, serviceUtilities.genericAndRatherUselessErrorHandler);

			$scope.saveProperty = function(e, property) {
				e.preventDefault();
				propertiesService.addProperty(property).then(function(response) {
					property.id = response.id;
					if (variableStateService.updateInProgress()) {
						variableStateService.setProperty(property.id).then(function() {
							$window.history.back();
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					} else {
						// FIXME Go somewhere more useful
						$location.path('/properties');
					}
				}, serviceUtilities.genericAndRatherUselessErrorHandler);

			};
		}
	]);
} ());
