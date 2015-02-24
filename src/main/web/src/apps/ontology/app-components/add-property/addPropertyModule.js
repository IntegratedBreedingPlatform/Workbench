/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['addVariable']);

	app.controller('AddPropertyController', ['$scope', '$window', 'addVariableService', function($scope, $window, addVariableService) {
		this.classes = [];

		$scope.saveProperty = function(e, property) {
			e.preventDefault();
			addVariableService.addProperty(property);
			$window.history.back();
		};
	}]);

}());
