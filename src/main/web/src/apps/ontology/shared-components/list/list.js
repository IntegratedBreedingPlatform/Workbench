/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', ['utilities']);

	listModule.directive('omList', ['$rootScope', 'mathUtilities', function($rootScope, mathUtilities) {
		return {
			restrict: 'E',
			scope: {
				colHeaders: '=omColHeaders',
				data: '=omData',
				parentClickHandler: '&omOnClick',
				selectedItem: '=omSelectedItem'
			},
			controller: function($scope) {
				$scope.isString = function(object) {
					return typeof object === 'string';
				};

				$scope.isAction = function(object) {
					return typeof object === 'object' && object.iconValue;
				};

				$scope.isNotActionHeader = function(object) {
					return object && typeof object === 'string' && object.indexOf('action-') !== 0;
				};
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
						numberOfItems = tbody.find('tr').length,
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
