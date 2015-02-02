/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', []);

	listModule.directive('list', function() {
		return {
			restrict: 'E',
			scope: {
				data: '=data'
			},
			templateUrl: '../static/views/ontology/listView.html'
		};
	});
}());
