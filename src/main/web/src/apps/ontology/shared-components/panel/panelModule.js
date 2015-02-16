/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('panel', []);

	listModule.directive('ompanel', function() {
		return {
			link: function(scope, element) {
				element.toggleClass('om-pa-open', scope.open);
			},
			restrict: 'E',
			scope: {
				open: '=omPanelOpen'
			},
			templateUrl: '../static/views/ontology/panelView.html',
			transclude: true
		};
	});

}());
