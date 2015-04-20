/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['formFields', 'multiSelect', 'input', 'textArea', 'properties', 'variableState', 'utilities']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertiesService', 'variableStateService',
		'propertyFormService', 'serviceUtilities', 'formUtilities',
		function($scope, $location, $window, propertiesService, variableStateService, propertyFormService, serviceUtilities,
			formUtilities) {

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
					$scope.submitted = true;

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

			$scope.cancel = function(e) {
				e.preventDefault();
				formUtilities.cancelAddHandler($scope, !propertyFormService.formEmpty($scope.property));
			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'apForm');
		}
	]);

	app.factory('propertyFormService', [function() {
		return {
			formEmpty: function(model) {
				return !!!model.name &&
					!!!model.description &&
					!!!model.cropOntologyId &&
					(angular.isUndefined(model.classes) ||
						(angular.isArray(model.classes) && model.classes.length === 0));
			}
		};
	}]);
} ());
