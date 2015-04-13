/*global angular*/
'use strict';

(function() {
	var panelModule = angular.module('panel', []);

	panelModule.directive('omPanel', function() {
		var VISIBLE_CLASS = 'om-pa-panel-visible';

		return {
			controller: function($scope, panelService) {
				$scope.panelService = panelService;

				$scope.closePanel = function(e) {
					e.preventDefault();
					$scope.panelService.hidePanel();
				};
			},
			link: function($scope, element) {
				$scope.$watch('panelService.getShownPanel()', function(panelName, prevPanelName, scope) {
					if (panelName === scope.omPanelIdentifier) {
						element.addClass(VISIBLE_CLASS);
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
	});

	panelModule.directive('omPanelSmall', function() {
		return {
			link: function ($scope, element) {
				element.addClass('om-pa-panel-small');
			}
		};
	});

	panelModule.directive('omMaskForPanel', function() {
		var VISIBLE_CLASS = 'om-pa-mask-visible';

		return {
			controller: function($scope, panelService) {
				$scope.panelService = panelService;
			},
			link: function($scope, element) {
				$scope.$watch('panelService.getShownPanel()', function(panelName, prevPanelName, scope) {
					if (panelName === scope.omMaskForPanel) {
						element.addClass(VISIBLE_CLASS);
					} else {
						element.removeClass(VISIBLE_CLASS);
					}
				});
			},
			restrict: 'A',
			scope: {
				omMaskForPanel: '='
			},
			templateUrl: 'static/views/ontology/panelMask.html',
			transclude: true
		};
	});

	panelModule.service('panelService', [function() {
		var shownPanel = null;

		return {
			showPanel: function(panel) {
				shownPanel = panel;
			},
			hidePanel: function() {
				shownPanel = null;
			},
			getShownPanel: function() {
				return shownPanel;
			}
		};
	}]);

}());
