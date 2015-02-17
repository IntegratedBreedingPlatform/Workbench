/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('panel', []);

	listModule.directive('ompanel', function() {
		return {
			link: function(scope, element) {
				element.toggleClass('om-pa-panel-visible', scope.visible);
			},
			restrict: 'E',
			scope: {
				visible: '=omPanelVisible'
			},
			templateUrl: '../static/views/ontology/panelView.html',
			transclude: true
		};
	});

}());
