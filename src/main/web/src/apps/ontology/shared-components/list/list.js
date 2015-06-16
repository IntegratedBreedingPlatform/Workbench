/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', ['utilities', 'paginator']);

	listModule.directive('omList', ['$rootScope', 'mathUtilities', function($rootScope, mathUtilities) {

		return {
			restrict: 'E',
			scope: {
				colHeaders: '=omColHeaders',
				data: '=omData',
				parentClickHandler: '&omOnClick',
				selectedItem: '=omSelectedItem',
				itemFilter: '=omItemFilter',
				propertiesToFilter: '=omPropertiesToFilter',
				pagination: '=omPagination'
			},

			controller: function($scope) {
				$scope.activeItemIndex = 0;

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

			},

			link: function(scope, element) {
				var tbody = element.find('tbody'),

					trNative = function() {
						return tbody.find('tr')[scope.activeItemIndex];
					};

				scope.$watch('data', function(data, prevData) {
					var isDataPopulated = data && data.length > 0,
						isFirstLoad = isDataPopulated && !scope.numberOfItemsShown,
						hasLengthChanged = isDataPopulated && data.length !== prevData.length;

					if (isFirstLoad || hasLengthChanged) {
						scope.numberOfItemsShown = data.length;
					}
				}, true);

				scope.scroll = function(scrollElement, change, duration, start, currentTime) {
					var INCREMENT = 20;

					currentTime += INCREMENT;
					scrollElement.scrollTop = mathUtilities.easeInOutQuad(currentTime, start, change, duration);
					if (currentTime < duration) {
						setTimeout(scope.scroll, INCREMENT, scrollElement, change, duration, start, currentTime);
					}
				};

				scope.selectItem = function(index, id) {
					var removePanelClose;

					scope.activeItemIndex = index;
					scope.selectedItem.id = id;

					scope.parentClickHandler();
					removePanelClose = $rootScope.$on('panelClose', function() {
						//execute ONCE and destroy
						element.find('table')[0].focus();
						removePanelClose();
					});
				};

				scope.toggleFavourites = function(index, id, event, object) {
					event.stopPropagation();
					scope.activeItemIndex = index;
					scope.selectedItem.id = id;
					object.iconFunction();
				};

				scope.isScrolledIntoView = function(el) {
					if (el) {
						var elemTop = el.getBoundingClientRect().top,
						elemBottom = el.getBoundingClientRect().bottom;

						return (elemTop >= 0) && (elemBottom <= window.innerHeight);
					}
					return false;
				};

				scope.checkKeyDown = function(e) {
					var key = e.which,
						SCROLL_DURATION = 100,
						CURRENT_TIME = 0;

					e.preventDefault();

					if (key === 40) {
						// Down
						if (scope.activeItemIndex < scope.filteredData.length - 1) {
							scope.activeItemIndex += 1;
						}
						if (trNative() && !scope.isScrolledIntoView(trNative())) {
							scope.scroll(document.body, trNative().offsetHeight * 2, SCROLL_DURATION, document.body.scrollTop,
								CURRENT_TIME);
						}
					} else if (key === 38) {
						// Up
						if (scope.activeItemIndex > 0) {
							scope.activeItemIndex -= 1;
						}
						if (trNative() && !scope.isScrolledIntoView(trNative())) {
							scope.scroll(document.body, trNative().offsetHeight * -1.5, SCROLL_DURATION, document.body.scrollTop,
								CURRENT_TIME);
						}
					} else if (key === 13) {
						// Enter
						scope.selectItem(scope.activeItemIndex, scope.filteredData[scope.activeItemIndex].id);
					}
				};

			},
			templateUrl: 'static/views/ontology/list.html'
		};
	}]);
}());
