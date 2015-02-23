/*global angular*/
'use strict';

(function() {
	var app = angular.module('addVariable', []);

	app.controller('AddVariableController', ['$scope', function($scope) {
		this.properties = [];
		this.methods = [];
		this.scales = [];
		this.types = [];

		$scope.saveVariable = function() {
			alert('Save variable');
		};

		$scope.addProperty = function() {
			alert('Add property');
		};

		$scope.addMethod = function() {
			alert('Add method');
		};

		$scope.addScale = function() {
			alert('Add scale');
		};
	}]);

}());
