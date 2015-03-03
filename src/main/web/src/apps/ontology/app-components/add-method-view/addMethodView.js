/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods', 'variableState', 'utilities']);

	app.controller('AddMethodController', ['$scope', '$location', '$window', 'methodService', 'variableStateService', 'serviceUtilities',
		function($scope, $location, $window, methodService, variableStateService, serviceUtilities) {

			$scope.saveMethod = function(e, method) {
				e.preventDefault();

				// TODO Error handling - only set the method if it saved
				methodService.saveMethod(method);

				if (variableStateService.updateInProgress()) {
					// FIXME Change to ID
					variableStateService.setMethod(method.name).then(function() {
						$window.history.back();
					}, serviceUtilities.genericAndRatherUselessErrorHandler);
				} else {
					// FIXME Go somewhere more useful
					$location.path('/methods');
				}

			};
		}
	]);
}());
