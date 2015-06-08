/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods', 'variableState', 'utilities']);

	app.controller('AddMethodController', ['$scope', '$location', '$window', 'methodsService', 'methodFormService', 'variableStateService',
		'serviceUtilities', 'formUtilities', function($scope, $location, $window, methodsService, methodFormService, variableStateService,
			serviceUtilities, formUtilities) {

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
								$window.history.back();
							});
						} else {
							$location.path('/methods');
						}
					}, function(response) {
						$scope.amForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.submitted = false;
					});
				}
			};

			$scope.cancel = function(e) {
				e.preventDefault();
				formUtilities.cancelAddHandler($scope, !methodFormService.formEmpty($scope.method));
			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'amForm');
		}
	]);

	app.factory('methodFormService', [function() {
		return {
			formEmpty: function(model) {
				return !!!model.name && !!!model.description;
			}
		};
	}]);
}());
