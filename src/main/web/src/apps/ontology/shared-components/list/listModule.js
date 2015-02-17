/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', []);

	listModule.directive('omlist', function() {
		return {
			restrict: 'E',
			scope: {
				colHeaders: '=omcolheaders',
				data: '=omdata',
				parentClickHandler: '&onClick',
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
			templateUrl: '../static/views/ontology/listView.html'
		};
	});

}());
