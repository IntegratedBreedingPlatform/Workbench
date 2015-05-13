/*global angular*/
'use strict';

(function() {
	var inputModule = angular.module('search', []);

	/*
	The search directive allows the user to input search text which will be stored in the passed
	in model.

	@param scope.model puts text from the text box in this model so that it can then be used for filtering
	*/
	inputModule.directive('omSearch', function() {
		return {
			restrict: 'E',
			scope: {
				model: '=omModel'
			},
			templateUrl: 'static/views/ontology/search.html'
		};
	});

})();
