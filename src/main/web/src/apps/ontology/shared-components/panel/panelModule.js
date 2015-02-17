/*global angular*/
'use strict';

(function() {
	var panelModule = angular.module('panel', []);

	panelModule.directive('ompanel', function() {
		return {
			controller: function($scope) {
				$scope.closePanel = function(e) {
					e.preventDefault();
					$scope.omVisible.show = false;
				};
			},
			link: function($scope, element) {
				$scope.$watch('omVisible.show', function(value) {
					element.toggleClass('om-pa-panel-visible', value);
				});
			},
			restrict: 'E',
			scope: {
				omVisible: '='
			},
			templateUrl: '../static/views/ontology/panelView.html',
			transclude: true
		};
	});
}());
