/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods', 'variableState', 'utilities']);

	app.controller('AddMethodController', ['$scope', '$location', '$window', 'methodsService', 'variableStateService', 'serviceUtilities',
		function($scope, $location, $window, methodsService, variableStateService, serviceUtilities) {

			$scope.saveMethod = function(e, method) {
				e.preventDefault();

				// Reset server errors
				$scope.serverErrors = {};

				if ($scope.amForm.$valid) {
					methodsService.addMethod(method).then(function(response) {
						// If we successfully added the method, continue..
						method.id = response.id;
						if (variableStateService.updateInProgress()) {
							variableStateService.setMethod(method.id, method.name).then(function() {
								$window.history.back();
							}, serviceUtilities.genericAndRatherUselessErrorHandler);
						} else {
							// FIXME Go somewhere more useful
							$location.path('/methods');
						}
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
					});
				}
			};
		}
	]);
}());

