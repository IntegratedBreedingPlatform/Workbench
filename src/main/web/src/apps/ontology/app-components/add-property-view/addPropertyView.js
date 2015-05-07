/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['formFields', 'tagSelect', 'input', 'textArea', 'properties', 'variableState', 'utilities']);

	app.controller('AddPropertyController', ['$scope', '$location', '$window', 'propertiesService', 'variableStateService',
		'propertyFormService', 'serviceUtilities', 'formUtilities',
		function($scope, $location, $window, propertiesService, variableStateService, propertyFormService, serviceUtilities,
			formUtilities) {

			$scope.property = {
				classes: []
			};
			$scope.classes = [];

			// Reset server errors
			$scope.serverErrors = {};

			propertiesService.getClasses().then(function(classes) {
				$scope.classes = classes;
			}, function(response) {
				$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
			});

			$scope.saveProperty = function(e, property) {
				e.preventDefault();

				// Reset server errors
				$scope.serverErrors = {};

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
					}, function(response) {
						$scope.apForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.submitted = false;
					});
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
