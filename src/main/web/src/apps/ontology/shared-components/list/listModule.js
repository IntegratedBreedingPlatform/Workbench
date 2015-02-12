/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', []);

	listModule.directive('omlist', function() {
		return {
			restrict: 'E',
			scope: {
				colHeaders: '=omcolheaders',
				data: '=omdata'
			},
			templateUrl: '../static/views/ontology/listView.html'
		};
	});

}());
