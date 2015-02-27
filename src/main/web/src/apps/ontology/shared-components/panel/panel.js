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
					$scope.panelService.visible = {show: null};
				};
			},
			link: function($scope, element) {
				$scope.$watch('panelService.visible', function(value, oldValue, scope) {
					if (value.show === scope.omPanelIdentifier) {
						element.addClass(VISIBLE_CLASS);
					} else {
						element.removeClass(VISIBLE_CLASS);
					}
				});
			},
			restrict: 'E',
			scope: {
				omPanelIdentifier: '='
			},
			templateUrl: 'static/views/ontology/panel.html',
			transclude: true
		};
	});

	panelModule.service('panelService', [function() {
		var visible = {
			show: null
		};

		return {
			visible: visible
		};
	}]);
}());
