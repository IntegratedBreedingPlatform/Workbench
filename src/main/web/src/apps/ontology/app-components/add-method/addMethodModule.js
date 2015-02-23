/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addMethod', []);

	app.controller('AddMethodController', ['$scope', function($scope) {
		$scope.saveMethod = function(e) {
			e.preventDefault();
			alert('Save method');
		};
	}]);

}());
