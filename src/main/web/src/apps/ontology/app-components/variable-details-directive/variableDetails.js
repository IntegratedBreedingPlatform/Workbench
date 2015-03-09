/*global angular*/
'use strict';

(function() {
	var variableDetailsModule = angular.module('variableDetails', []);

	variableDetailsModule.directive('omVariableDetails', function() {

		return {
			restrict: 'E',
			templateUrl: 'static/views/ontology/variableDetails.html'
		};
	});

})();
