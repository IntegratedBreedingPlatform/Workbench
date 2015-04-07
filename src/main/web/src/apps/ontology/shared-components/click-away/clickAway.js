/*global angular*/
'use strict';

(function() {
	var clickAway = angular.module('clickAway', []);

	clickAway.directive('omClickAnywhereButHere', function($document) {
		return {
			restrict: 'A',
			scope: {
				callback: '&omCallback',
				enabled: '=omEnabled'
			},
			link: function(scope, element) {

				scope.handler = function(event) {
					if (!element[0].contains(event.target) && scope.enabled) {
						scope.callback(event);
						scope.$apply();
					 }
				};

				$document.on('click', scope.handler);

				scope.$on('$destroy', function() {
					$document.off('click', scope.handler);
				});
			}
		};
	});
}());
