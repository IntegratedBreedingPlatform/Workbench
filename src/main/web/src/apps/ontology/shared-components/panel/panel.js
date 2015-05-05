/*global angular*/
'use strict';

(function() {
	var panelModule = angular.module('panel', []),
		DELAY = 400;

	panelModule.directive('omPanel', ['$timeout', 'panelService', function($timeout, panelService) {
		var VISIBLE_CLASS = 'om-pa-panel-visible';

		return {
			controller: function($scope) {
				//this function is esposed for testing purposes only
				$scope.escHandler = function (msg, e) {
					$scope.$apply(function () {
						if (panelService.getCurrentPanel() === $scope.omPanelIdentifier) {
							$scope.closePanel(e);
						}
					});
				};
				//this function is esposed for testing purposes only
				$scope.removeEscHandler = $scope.$on('escKeydown', $scope.escHandler);

				$scope.closePanel = function(e) {
					e.preventDefault();
					$scope.showThrobber = false;
					panelService.hidePanel();
				};

				$scope.$on('$destroy', function() {
					$scope.removeEscHandler();
				});
			},
			link: function($scope, element) {
				$scope.$watch(panelService.getCurrentPanel, function(panelName, prevPanelName, scope) {
					if (panelName === scope.omPanelIdentifier) {
						element.addClass(VISIBLE_CLASS);

						$timeout(function() {
							if (panelService.getCurrentPanel()) {
								$scope.showThrobber = true;
							}
						}, DELAY);
					} else {
						element.removeClass(VISIBLE_CLASS);
					}
				});
			},
			restrict: 'E',
			scope: {
				omPanelIdentifier: '=',
				title: '=omTitle'
			},
			templateUrl: 'static/views/ontology/panel.html',
			transclude: true
		};
	}]);

	panelModule.directive('omMask', function(panelService) {
		var VISIBLE_CLASS = 'om-mask-visible';

		return {
			link: function($scope, element) {
				$scope.$watch(panelService.getCurrentPanel, function(panelName) {
					element.toggleClass(VISIBLE_CLASS, !!panelName);
				});
			},
			restrict: 'E',
			scope: {}
		};
	});

	panelModule.service('panelService', [function() {
		var currentPanel = null;

		return {
			showPanel: function(panelName) {
				currentPanel = panelName;
			},
			hidePanel: function() {
				currentPanel = null;
			},
			getCurrentPanel: function() {
				return currentPanel;
			}
		};
	}]);

}());
