/*global angular*/
'use strict';

(function() {
	var app = angular.module('methodsView', ['methods', 'list', 'panel', 'methodDetails', 'utilities']),
		DELAY = 400;

	app.controller('MethodsController', ['$scope', 'methodsService', 'panelService', '$timeout', 'collectionUtilities',
		function($scope, methodsService, panelService, $timeout, collectionUtilities) {
			var ctrl = this;

			ctrl.methods = [];
			ctrl.showThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'description'];

			$scope.panelName = 'methods';

			$timeout(function() {
				ctrl.showThrobber = true;
			}, DELAY);

			methodsService.getMethods().then(function(methods) {
				ctrl.methods = methods;
				ctrl.showThrobberWrapper = false;

				if (ctrl.methods.length === 0) {
					ctrl.showNoItemsMessage = true;
				}
			});

			$scope.showMethodDetails = function() {
				// Ensure the previously selected method doesn't show in the panel before we've retrieved the new one
				$scope.selectedMethod = null;

				methodsService.getMethod($scope.selectedItem.id).then(function(method) {
					$scope.selectedMethod = method;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.updateSelectedMethod = function(updatedMethod) {

				var selectedIndex = -1;

				if (updatedMethod) {
					updatedMethod.id = $scope.selectedItem.id;
				}

				ctrl.methods.some(function(method, index) {
					if (method.id === $scope.selectedItem.id) {
						selectedIndex = index;
						return true;
					}
				});

				// Not much we can really do if we don't find it in the list. Just don't update.
				if (selectedIndex !== -1) {
					if (updatedMethod) {
						ctrl.methods[selectedIndex] = updatedMethod;
						collectionUtilities.sortByName(ctrl.methods);
					} else {
						ctrl.methods.splice(selectedIndex, 1);
					}
				}
			};

			$scope.selectedItem = {id: null};
			$scope.selectedMethod = null;

		}
	]);
}());
