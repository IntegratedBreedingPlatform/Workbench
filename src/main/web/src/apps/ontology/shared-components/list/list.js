/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', ['utilities']);

	listModule.directive('omList', ['$rootScope', 'mathUtilities', '$filter', function($rootScope, mathUtilities, $filter) {

		return {
			restrict: 'E',
			scope: {
				colHeaders: '=omColHeaders',
				data: '=omData',
				parentClickHandler: '&omOnClick',
				selectedItem: '=omSelectedItem',
				itemFilter: '=omItemFilter'
			},

			controller: function($scope) {
				$scope.isAnyItemShown = true;
				// Actual index of the item in the full list
				$scope.activeItemIndex = 0;
				// Index of the item in the visible list which doesn't include hidden rows
				$scope.visibleItemIndex = 0;
				// Lookup object to avoid searching through array for item by id many times
				// Keys are the item's id and the values are the item's index in the full list
				$scope.indexOfItems = {};

				$scope.isString = function(object) {
					return typeof object === 'string';
				};

				$scope.isAction = function(object) {
					return typeof object === 'object' && object.iconValue;
				};

				$scope.isNotActionHeader = function(object) {
					return typeof object === 'string' && object.indexOf('action-') !== 0;
				};

				// Exposed for testing
				$scope.isItemFilteredOut = function(item, filter) {
					return ($filter('filter')([item], filter)).length === 0;
				};
			},

			link: function(scope, element) {
				var tbody = element.find('tbody'),

					trNative = function() {
						return tbody.find('tr')[scope.activeItemIndex];
					};

				// Apply the filter to the list whenever it changes
				scope.$watch('itemFilter', function(newFilterVal, prevFilterVal) {
					if (newFilterVal !== prevFilterVal) {
						scope.filterItems(newFilterVal);
					}
				});

				scope.$watch('data', function(data) {
					var i;

					if (!scope.numberOfItemsShown && data && data.length > 0) {
						scope.numberOfItemsShown = data.length;
						// Holds the items that are currently shown to the user
						scope.shownItems = data;

						for (i = 0; i < data.length; i++) {
							scope.indexOfItems[data[i].id] = i;
						}
					}
				});

				scope.filterItems = function(filter) {
					var shownItems = [];

					scope.data.forEach(function(item) {
						// Add value to item about whether it is filtered out or not so that we can either show or hide it
						item.isHidden = scope.isItemFilteredOut(item, filter);
						if (!item.isHidden) {
							shownItems.push(item);
						}
					});

					scope.shownItems = shownItems;
					scope.numberOfItemsShown = shownItems.length;
					scope.isAnyItemShown = !!shownItems.length;
					// Set the active item index to be the first visible item in the list
					scope.activeItemIndex = scope.getActiveItemIndex(0);
				};

				scope.scroll = function(scrollElement, change, duration, start, currentTime) {
					var INCREMENT = 20;

					currentTime += INCREMENT;
					scrollElement.scrollTop = mathUtilities.easeInOutQuad(currentTime, start, change, duration);
					if (currentTime < duration) {
						setTimeout(scope.scroll, INCREMENT, scrollElement, change, duration, start, currentTime);
					}
				};

				scope.selectItem = function(index, id) {
					var removePanelClose,
						item = scope.data[index];

					scope.activeItemIndex = index;
					scope.visibleItemIndex = scope.shownItems.indexOf(item);
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

				scope.getActiveItemIndex = function(visibleItemIndex) {
					var shownItem = scope.shownItems[visibleItemIndex];

					return shownItem ? scope.indexOfItems[shownItem.id] : null;
				};

				scope.checkKeyDown = function(e) {
					var key = e.which,
						SCROLL_DURATION = 100,
						CURRENT_TIME = 0;

					e.preventDefault();

					if (key === 40) {
						// Down
						if (scope.visibleItemIndex < scope.numberOfItemsShown - 1) {
							scope.visibleItemIndex += 1;

							scope.activeItemIndex = scope.getActiveItemIndex(scope.visibleItemIndex);
						}
						if (trNative() && !scope.isScrolledIntoView(trNative())) {
							scope.scroll(document.body, trNative().offsetHeight * 2, SCROLL_DURATION, document.body.scrollTop,
								CURRENT_TIME);
						}
					} else if (key === 38) {
						// Up
						if (scope.visibleItemIndex > 0) {
							scope.visibleItemIndex -= 1;

							scope.activeItemIndex = scope.getActiveItemIndex(scope.visibleItemIndex);
						}
						if (trNative() && !scope.isScrolledIntoView(trNative())) {
							scope.scroll(document.body, trNative().offsetHeight * -1.5, SCROLL_DURATION, document.body.scrollTop,
								CURRENT_TIME);
						}
					} else if (key === 13) {
						// Enter
						scope.selectItem(scope.activeItemIndex, scope.data[scope.activeItemIndex].id);
					}
				};

			},
			templateUrl: 'static/views/ontology/list.html'
		};
	}]);
}());
