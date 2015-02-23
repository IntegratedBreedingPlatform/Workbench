/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addProperty', []);

	app.controller('AddPropertyController', ['$scope', function($scope) {
		this.classes = [];

		$scope.saveProperty = function(e) {
			e.preventDefault();
			alert('Save property');
		};
	}]);

}());
