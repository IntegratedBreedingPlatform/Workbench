/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addVariable', []);

	app.controller('AddVariableController', ['$scope', '$location', function($scope, $location) {
		this.properties = [];
		this.methods = [];
		this.scales = [];
		this.types = [];

		$scope.numericVariable = true;

		$scope.saveVariable = function(e) {
			e.preventDefault();
			alert('Save variable');
		};

		$scope.addProperty = function(e) {
			e.preventDefault();
			$location.path('/add/property');
		};

		$scope.addMethod = function(e) {
			e.preventDefault();
			alert('Add method');
		};

		$scope.addScale = function(e) {
			e.preventDefault();
			alert('Add scale');
		};
	}]);

}());
