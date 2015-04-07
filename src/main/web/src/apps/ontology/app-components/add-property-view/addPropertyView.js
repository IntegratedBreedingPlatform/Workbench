/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['formFields', 'multiSelect', 'input', 'textArea', 'properties', 'variableState', 'utilities']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertiesService', 'variableStateService',
		'serviceUtilities',
		function($scope, $location, $window, propertiesService, variableStateService, serviceUtilities) {

			$scope.property = {
				classes: []
			};
			$scope.classes = [];

			propertiesService.getClasses().then(function(classes) {
				$scope.classes = classes;
			}, serviceUtilities.genericAndRatherUselessErrorHandler);

			$scope.saveProperty = function(e, property) {
				e.preventDefault();

				if ($scope.apForm.$valid) {
					propertiesService.addProperty(property).then(function(response) {
						property.id = response.id;
						if (variableStateService.updateInProgress()) {
							variableStateService.setProperty(property.id, property.name).finally(function() {
								$window.history.back();
							});
						} else {
							// FIXME Go somewhere more useful
							$location.path('/properties');
						}
					}, serviceUtilities.genericAndRatherUselessErrorHandler);
				}
			};
		}
	]);
} ());
