/*global angular*/
'use strict';

(function() {
	var app = angular.module('methodsView', ['methods', 'list', 'panel']);

	app.controller('MethodsController', ['$scope', 'methodsService', 'panelService',
		function($scope, methodsService, panelService) {
			var ctrl = this;
			this.methods = [];

			$scope.panelName = 'methods';

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

			$scope.showMethodDetails = function() {
				methodsService.getMethod($scope.selectedItem.id).then(function(method) {
					$scope.selectedMethod = method;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.selectedItem = {id: null};
			$scope.selectedMethod = null;
		}
	]);
}());
