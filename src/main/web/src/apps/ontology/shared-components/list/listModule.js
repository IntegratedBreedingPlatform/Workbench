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
			controller: function($scope) {
				$scope.isString = function(object) {
					return typeof object === 'string';
				};

				$scope.isAction = function(object) {
					return typeof object === 'object' && object.iconValue;
				};

				$scope.isNotActionHeader = function(object) {
					return object && typeof object === 'string' && object.indexOf('action-') !== 0;
				};
			},
			templateUrl: '../static/views/ontology/listView.html'
		};
	});

}());
