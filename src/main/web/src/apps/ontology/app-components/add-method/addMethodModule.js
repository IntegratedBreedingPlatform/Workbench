/*global angular*/
'use strict';

(function() {
	var app = angular.module('addMethod', []);

	app.controller('AddMethodController', ['$scope', 'methodService', function($scope, methodService) {
		$scope.saveMethod = function(e, method) {
			e.preventDefault();

			// TODO Error handling - only set the method if it saved
			methodService.saveMethod(method);
		};
	}]);

	app.service('methodService', [function() {
		return {
			saveMethod: function(method) {
				// TODO Call actual save functionality
				console.log('Saving method');
				console.log(method);
			}
		};
	}]);

}());
