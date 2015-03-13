/*global angular*/
'use strict';

(function() {
	var categoriesModule = angular.module('categories', []);

	categoriesModule.directive('omCategories', function() {
		return {
			restrict: 'E',
			scope: {
				categories: '=omCategories'
			},
			controller: function($scope) {

				$scope.addCategory = function(e) {
					e.preventDefault();
					$scope.categories.push({});
				};

				$scope.removeCategory = function(e, index) {
					e.preventDefault();
					$scope.categories.splice(index, 1);
				};
			},
			templateUrl: 'static/views/ontology/categories.html'
		};
	});
}());
