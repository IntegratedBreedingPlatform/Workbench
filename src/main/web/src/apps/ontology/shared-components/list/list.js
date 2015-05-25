/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', ['utilities']);

	listModule.directive('omList', ['$rootScope', 'mathUtilities', '$filter', function($rootScope, mathUtilities, $filter) {

		function isItemFilteredOut(item, filter) {
			return ($filter('filter')([item], filter)).length === 0;
		}

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

				$scope.isString = function(object) {
					return typeof object === 'string';
				};

				$scope.isAction = function(object) {
					return typeof object === 'object' && object.iconValue;
				};

				$scope.isNotActionHeader = function(object) {
					return object && typeof object === 'string' && object.indexOf('action-') !== 0;
				};

				// Exposed for testing
				$scope.isItemFilteredOut = isItemFilteredOut;
			},

			link: function(scope, element) {
				var activeItemIndex = 0,
					tbody = element.find('tbody'),
					ACTIVE_STYLE = 'active',

					trNative = function() {
						return tbody.find('tr')[activeItemIndex];
					},
					trAngular = function() {
						return angular.element(trNative());
					};

				// Apply the filter to the list whenever it changes
				scope.$watch('itemFilter', function(newFilterVal, prevFilterVal) {
					if (newFilterVal !== prevFilterVal) {
						scope.filterItems(newFilterVal);
					}
				});

				scope.filterItems = function(filter) {
					var itemsShownStatus = [];

					scope.data.forEach(function(item) {
						// Add value to item about whether it is filtered out or not so that we can either show or hide it
						item.isHidden = isItemFilteredOut(item, filter);
						// Add to list of filtered item values so that we can flatten the array to find out whether at least one is shown
						itemsShownStatus.push(!item.isHidden);
					});

					// Reduce array of filtered items down to a single boolean for whether any item is shown
					scope.isAnyItemShown = itemsShownStatus.reduce(function(prevVal, item) {
						if (prevVal) {
							return prevVal;
						}
						return item;
					}, false);
				};

				scope.scroll = function(scrollElement, change, duration, start, currentTime) {
					var INCREMENT = 20;

					currentTime += INCREMENT;
					scrollElement.scrollTop = mathUtilities.easeInOutQuad(currentTime, start, change, duration);
					if (currentTime < duration) {
						setTimeout(scope.scroll, INCREMENT, scrollElement, change, duration, start, currentTime);
					}
				};

				scope.updateActiveItem = function(index) {
					trAngular().removeClass(ACTIVE_STYLE);
					activeItemIndex = index;
					trAngular().addClass(ACTIVE_STYLE);
				};

				scope.selectItem = function(index, id) {
					var removePanelClose;

					scope.updateActiveItem(index);
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
					scope.updateActiveItem(index);
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

				element.on('keydown', function(e) {
					var key = e.which,
						// TODO: change the keyboard nav so that it works correctly when a filter is applied.
						// For now, minus one off the length for now to account for the "no items found" row.
						numberOfItems = tbody.find('tr').length - 1,
						SCROLL_DURATION = 100,
						CURRENT_TIME = 0;

					e.preventDefault();

					// Down
					if (key === 40) {
						if (activeItemIndex < numberOfItems) {
							scope.updateActiveItem(activeItemIndex + 1);

							if (trNative() && !scope.isScrolledIntoView(trNative())) {
								scope.scroll(document.body, trNative().offsetHeight * 2, SCROLL_DURATION, document.body.scrollTop,
									CURRENT_TIME);
							}
						}
					}

					// Up
					if (key === 38) {
						if (activeItemIndex > 0) {
							scope.updateActiveItem(activeItemIndex - 1);
						}
						if (trNative() && !scope.isScrolledIntoView(trNative())) {
							scope.scroll(document.body, trNative().offsetHeight * -1.5, SCROLL_DURATION, document.body.scrollTop,
								CURRENT_TIME);
						}
					}

					// Enter
					if (key === 13) {
						scope.selectItem(activeItemIndex, scope.data[activeItemIndex].id);
					}
				});

				element.find('table').on('focus', function(e) {
					e.preventDefault();
					// Mark first item as active
					trAngular().addClass(ACTIVE_STYLE);
				});

				element.find('table').on('blur', function(e) {
					e.preventDefault();
					// Mark first item as active
					trAngular().removeClass(ACTIVE_STYLE);
				});

				element.find('table')[0].focus();
			},
			templateUrl: 'static/views/ontology/list.html'
		};
	}]);
}());
