/*global angular*/
'use strict';

(function() {
	var panelModule = angular.module('panel', []);

	panelModule.directive('omPanel', function(panelService) {
		var VISIBLE_CLASS = 'om-pa-panel-visible';

		return {
			controller: function($scope) {
				$scope.closePanel = function(e) {
					e.preventDefault();
					panelService.hidePanel();
				};
			},
			link: function($scope, element) {
				$scope.$watch(panelService.getCurrentPanel, function(panelName) {
					element.toggleClass(VISIBLE_CLASS, panelName === $scope.omPanelIdentifier);
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
