/*global angular*/
'use strict';

(function() {
	var categoriesModule = angular.module('categories', ['formFields']);

	categoriesModule.directive('omCategories', function(editable) {
		return {
			restrict: 'E',
			scope: {
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel'
			},
			controller: function($scope) {

				// Categories will be added to a 'categories' property on the specified property. If either the
				// property or the categories are not present, instantiate them
				if ($scope.model) {
					$scope.model[$scope.property] = $scope.model[$scope.property] || {};
					$scope.model[$scope.property].categories = $scope.model[$scope.property].categories || [{}];
				}

				$scope.editable = editable($scope);

				$scope.addCategory = function(e) {
					e.preventDefault();
					$scope.model[$scope.property].categories.push({});
				};

				$scope.removeCategory = function(e, index) {
					e.preventDefault();
					$scope.model[$scope.property].categories.splice(index, 1);
				};
			},
			templateUrl: 'static/views/ontology/categories.html'
		};
	});
}());
