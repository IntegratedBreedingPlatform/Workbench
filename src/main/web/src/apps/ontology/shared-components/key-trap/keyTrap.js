/*global angular*/
'use strict';

(function() {
	var keyTrap = angular.module('keyTrap', []);

	keyTrap.directive('omKeyTrap', ['$document', function($document) {
		return {
			restrict: 'A',

			link: function(scope) {

				//this function is exposed for testing purposes
				scope.escHandler = function(e) {
					//esc
					if (e.keyCode === 27) {
						scope.$broadcast('escKeydown', e);
					}
				};

				$document.bind('keydown', scope.escHandler);

				scope.$on('$destroy', function() {
					$document.off('keydown', scope.escHandler);
				});
			}
		};
	}]);
}());
