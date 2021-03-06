/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods', 'variableState', 'utilities', 'input', 'textArea', 'errorList']);

	app.controller('AddMethodController', ['$scope', '$location', '$window', 'methodsService', 'methodFormService', 'variableStateService',
		'serviceUtilities', 'formUtilities', function($scope, $location, $window, methodsService, methodFormService, variableStateService,
			serviceUtilities, formUtilities) {

			var ADD_VARIABLE_PATH = '/add/variable',
				METHODS_PATH = '/methods';

			$scope.method = {};

			$scope.saveMethod = function(e, method) {
				e.preventDefault();

				// Reset server errors
				$scope.serverErrors = {};

				if ($scope.amForm.$valid) {
					$scope.submitted = true;

					methodsService.addMethod(method).then(function(response) {
						// If we successfully added the method, continue..
						method.id = response.id;
						if (variableStateService.updateInProgress()) {

							variableStateService.setMethod(method.id, method.name).finally(function() {
								$location.path(ADD_VARIABLE_PATH);
							});
						} else {
							$location.path(METHODS_PATH);
						}
					}, function(response) {
						$scope.amForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.submitted = false;
					});
				}
			};

			$scope.cancel = function(e) {
				var path = variableStateService.updateInProgress() ? ADD_VARIABLE_PATH : METHODS_PATH;

				e.preventDefault();
				formUtilities.cancelAddHandler($scope, !methodFormService.formEmpty($scope.method), path);
			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'amForm');
		}
	]);

	app.factory('methodFormService', function() {
		return {
			formEmpty: function(model) {
				return !!!model.name && !!!model.description;
			}
		};
	});
}());
