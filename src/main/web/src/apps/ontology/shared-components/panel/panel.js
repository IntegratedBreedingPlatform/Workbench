/*global angular*/
'use strict';

(function() {
	var panelModule = angular.module('panel', []),
		DELAY = 400;

	panelModule.directive('omPanel', ['$timeout', 'panelService', function($timeout, panelService) {
		var VISIBLE_CLASS = 'om-pa-panel-visible';

		return {
			controller: function($scope) {
				$scope.closePanel = function(e) {
					e.preventDefault();
					$scope.showThrobber = false;
					panelService.hidePanel();
				};
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
