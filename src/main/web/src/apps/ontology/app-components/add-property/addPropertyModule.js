/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['properties', 'variables']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertyService', 'propertiesService', 'variableService',
		function($scope, $location, $window, propertyService, propertiesService, variableService) {

			// TODO Error handling
			propertiesService.getClasses().then(function(classes) {
				$scope.classes = classes;
			});

			$scope.saveProperty = function(e, property) {
				e.preventDefault();

				// TODO Error handling - only set the property if it saved
				propertyService.saveProperty(property);

				if (variableService.updateInProgress()) {
					variableService.setProperty(property);
					$window.history.back();
				}

				// FIXME Go somewhere more useful
				$location.path('/properties');
			};
		}
	]);
} ());
