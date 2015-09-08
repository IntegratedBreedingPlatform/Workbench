/*global angular*/
'use strict';

(function() {
	var expandCollapseHeadingModule = angular.module('expandCollapseHeading', []);

	expandCollapseHeadingModule.directive('omExpandCollapseHeading', function() {
		return {
			restrict: 'E',
			scope: {
				isOpen: '=omIsOpen',
				text: '@omText'
			},
			templateUrl: 'static/views/ontology/expandCollapseHeading.html'
		};
	});

})();
