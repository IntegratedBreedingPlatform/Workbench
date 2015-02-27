/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods', 'variableState']);


	app.controller('AddMethodController', ['$scope', '$location', '$window', 'methodService', 'variableStateService',
		function($scope, $location, $window, methodService, variableStateService) {

			var ctrl = this;

			// TODO Implement useful error handling

			// Exposed on the controller for testing
			ctrl.genericAndRatherUselessErrorHandler = function(error) {
				if (console) {
					console.log(error);
				}
			};

			$scope.saveMethod = function(e, method) {
				e.preventDefault();

				// TODO Error handling - only set the method if it saved
				methodService.saveMethod(method);

				if (variableStateService.updateInProgress()) {
					// FIXME Change to ID
					variableStateService.setMethod(method.name).then(function() {
						$window.history.back();
					}, ctrl.genericAndRatherUselessErrorHandler);
				} else {
					// FIXME Go somewhere more useful
					$location.path('/methods');
				}

			};
		}
	]);
}());
