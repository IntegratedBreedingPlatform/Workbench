/*global angular*/
'use strict';

(function() {
	var categoriesModule = angular.module('categories', ['formFields']);

	function validateCategories(ctrl, categories) {
		var names = [],
			values = [];

		categories.some(function(category) {
			if (!category.name || !category.description) {
				ctrl.$setValidity('emptyValue', false);
				return true;
			}

			if (names.indexOf(category.name) !== -1) {
				ctrl.$setValidity('nonUniqueName', false);
				return true;
			}

			if (values.indexOf(category.description) !== -1) {
				ctrl.$setValidity('nonUniqueValue', false);
				return true;
			}

			names.push(category.name);
			values.push(category.description);
		});
	}

	categoriesModule.directive('omCategories', function(editable) {
		return {
			require: 'ngModel',
			restrict: 'E',
			scope: {
				categorical: '=omCategorical',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=ngModel'
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

				$scope.removable = function() {
					return $scope.model && $scope.model[$scope.property].categories.length >= 2;
				};

				$scope.removeCategory = function(e, index) {
					e.preventDefault();
					if ($scope.removable()) {
						$scope.model[$scope.property].categories.splice(index, 1);
					}
				};

			},

			link: function(scope, elm, attrs, ctrl) {

				var resetValidity = function() {
					ctrl.$setValidity('emptyValue', true);
					ctrl.$setValidity('nonUniqueName', true);
					ctrl.$setValidity('nonUniqueValue', true);
				};

				scope.$watch('categorical', function(categorical) {
					if (!categorical) {
						resetValidity();
					} else if (scope.model && scope.model[scope.property] && scope.model[scope.property].categories) {
						validateCategories(ctrl, scope.model[scope.property].categories);
					}
				});

				scope.$watch('model[property].categories', function(data) {
					resetValidity();

					if (!scope.categorical || !data) {
						return;
					}

					validateCategories(ctrl, data);
				}, true);
			},

			templateUrl: 'static/views/ontology/categories.html'
		};
	});
}());
