/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', ['utilities', 'paginator']);

	listModule.directive('omList', ['selectedItemService', function(selectedItemService) {

		return {
			restrict: 'E',
			scope: {
				colHeaders: '=omColHeaders',
				data: '=omData',
				parentClickHandler: '&omOnClick',
				selectedItem: '=omSelectedItem',
				itemFilter: '=omItemFilter',
				propertiesToFilter: '=omPropertiesToFilter',
				pagination: '=omPagination',
				listName: '@omListName',
				optionsFilter: '=omOptionsFilter'
			},

			controller: function($scope) {
				$scope.activeItemId = selectedItemService.getSelectedItem().id;

				// Set the max no of rows to 50 if pagination is enabled, otherwise set to -1
				$scope.rowsPerPage = $scope.pagination ? 50 : -1;

				$scope.isString = function(object) {
					return typeof object === 'string';
				};

				$scope.isAction = function(object) {
					return typeof object === 'object' && object.iconValue;
				};

				$scope.isNotActionHeader = function(object) {
					return typeof object === 'string' && object.indexOf('action-') !== 0;
				};

				$scope.filterByProperties = function(item) {
					if (!$scope.itemFilter) {
						return true;
					}
					return $scope.propertiesToFilter.some(function(property) {
						var value = item[property].toLowerCase(),
							searchText = $scope.itemFilter.toLowerCase();

						if (value.indexOf(searchText) > -1) {
							return true;
						}
					}, this);
				};

				$scope.filterByOptions = function(item) {
					if ($scope.optionsFilter) {
						return $scope.optionsFilter(item);
					} else {
						return function() {
							return true;
						};
					}
				};
			},

			link: function(scope) {

				scope.$watch('data', function(data, prevData) {
					var isDataPopulated = data && data.length > 0,
						isFirstLoad = isDataPopulated && !scope.numberOfItemsShown,
						hasLengthChanged = isDataPopulated && data.length !== prevData.length;

					if (isFirstLoad || hasLengthChanged) {
						scope.numberOfItemsShown = data.length;
					}
				}, true);

				scope.$watch(selectedItemService.getSelectedItem, function(item, prevItem, scope) {
					if (item.list !== scope.listName) {
						scope.activeItemId = null;
					} else {
						scope.activeItemId = item.id;
					}
				}, true);

				scope.selectItem = function(index, id) {
					selectedItemService.setSelectedItem(id, scope.listName);
					scope.activeItemId = id;
					scope.selectedItem.id = id;

					scope.parentClickHandler();
				};

				scope.toggleFavourites = function(index, id, event, object) {
					event.stopPropagation();
					scope.selectedItem.id = id;
					object.iconFunction();
				};

			},
			templateUrl: 'static/views/ontology/list.html'
		};
	}]);

	listModule.service('selectedItemService', function() {
		var selectedItemId = null,
			selectedItemList = null;

		return {
			getSelectedItem: function() {
				return {
					id: selectedItemId,
					list: selectedItemList
				};
			},
			setSelectedItem: function(itemId, list) {
				selectedItemId = itemId;
				selectedItemList = list;
			}
		};
	});
}());
