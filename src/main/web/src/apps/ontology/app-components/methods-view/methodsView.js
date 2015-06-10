/*global angular*/
'use strict';

(function() {
	var app = angular.module('methodsView', ['methods', 'list', 'panel', 'methodDetails', 'utilities', 'search']),
		DELAY = 400;

	app.controller('MethodsController', ['$scope', 'methodsService', 'panelService', '$timeout', 'collectionUtilities',
		function($scope, methodsService, panelService, $timeout, collectionUtilities) {
			var ctrl = this;

			ctrl.methods = [];
			ctrl.showThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'description'];
			ctrl.problemGettingList = false;

			$scope.filterByProperties = ctrl.colHeaders;
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
			}, function() {
				ctrl.showThrobberWrapper = false;
				ctrl.problemGettingList = true;
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
						$scope.selectedMethod = updatedMethod;
					} else {
						ctrl.methods.splice(selectedIndex, 1);
					}
				}
			};

			// An object only containing the selected item's id. This format is required for passing to the list directive.
			$scope.selectedItem = {id: null};
			// Contains the entire selected method object once it has been updated.
			$scope.selectedMethod = null;
		}
	]);
}());
