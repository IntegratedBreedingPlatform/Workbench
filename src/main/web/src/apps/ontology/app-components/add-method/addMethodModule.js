/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods']);

	app.controller('AddMethodController', ['$scope', '$location', 'methodService', function($scope, $location, methodService) {
		$scope.saveMethod = function(e, method) {
			e.preventDefault();

			// TODO Error handling - only set the method if it saved
			methodService.saveMethod(method);

			// FIXME Go somewhere more useful
			$location.path('/methods');
		};
	}]);
}());
