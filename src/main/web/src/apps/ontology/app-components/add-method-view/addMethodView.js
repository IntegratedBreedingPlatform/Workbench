/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods', 'variableState']);

	// TODO Implement useful error handling
	function genericAndRatherUselessErrorHandler(error) {
		if (console) {
			console.log(error);
		}
	}

	app.controller('AddMethodController', ['$scope', '$location', '$window', 'methodService', 'variableStateService',
		function($scope, $location, $window, methodService, variableStateService) {
			$scope.saveMethod = function(e, method) {
				e.preventDefault();

				// TODO Error handling - only set the method if it saved
				methodService.saveMethod(method);

				if (variableStateService.updateInProgress()) {

					// FIXME Change to ID
					variableStateService.setMethod(method.name).then(function() {
						$window.history.back();
					}, genericAndRatherUselessErrorHandler);
				} else {
					// FIXME Go somewhere more useful
					$location.path('/methods');
				}

			};
		}
	]);
}());
