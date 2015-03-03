/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods', 'variableState', 'utilities']);

	app.controller('AddMethodController', ['$scope', '$location', '$window', 'methodsService', 'variableStateService', 'serviceUtilities',
		function($scope, $location, $window, methodsService, variableStateService, serviceUtilities) {

			$scope.saveMethod = function(e, method) {
				e.preventDefault();

				methodsService.addMethod(method).then(function() {
					// If we successfully added the method, continue..
					if (variableStateService.updateInProgress()) {
						// FIXME Change to ID
						variableStateService.setMethod(method.name).then(function() {
							$window.history.back();
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					} else {
						// FIXME Go somewhere more useful
						$location.path('/methods');
					}
				}, serviceUtilities.genericAndRatherUselessErrorHandler);
			};
		}
	]);
}());
