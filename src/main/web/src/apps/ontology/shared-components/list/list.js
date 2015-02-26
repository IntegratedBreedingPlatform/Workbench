/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', []);

	listModule.directive('omList', function() {
		return {
			restrict: 'E',
			scope: {
				colHeaders: '=omColHeaders',
				data: '=omData',
				parentClickHandler: '&omOnClick',
				selectedItem: '=omSelectedItem'
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

				$scope.clickHandler = function(itemId) {
					$scope.selectedItem.id = itemId;
					$scope.parentClickHandler();
				};
			},
			templateUrl: '../static/views/ontology/list.html'
		};
	});

}());
