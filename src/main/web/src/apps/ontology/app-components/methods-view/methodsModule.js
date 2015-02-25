/*global angular*/
'use strict';

(function() {
	var app = angular.module('methodsView', ['methods', 'list', 'panel']);

	app.controller('MethodsController', ['$scope', 'methodsService', function($scope, methodsService) {
		var ctrl = this;
		this.methods = [];

		ctrl.colHeaders = ['Name', 'Description'];

		methodsService.getMethods().then(function(methods) {
			ctrl.methods = methods.map(function(item) {
				return {
					id: item.id,
					Name: item.name,
					Description: item.description
				};
			});
		});

		$scope.panelOpen = {show: false};

		$scope.showMethodDetails = function() {

			methodsService.getMethod($scope.selectedItem.id).then(function(method) {
				$scope.selectedMethod = method;
			});

			$scope.panelOpen.show = true;
		};

		$scope.selectedItem = {id: null};
		$scope.selectedMethod = null;
	}]);
}());
