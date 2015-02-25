/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', ['methods']);

	app.controller('AddMethodController', ['$scope', 'methodService', function($scope, methodService) {
		$scope.saveMethod = function(e, method) {
			e.preventDefault();

			// TODO Error handling - only set the method if it saved
			methodService.saveMethod(method);
		};
	}]);
}());
