/*global angular*/
'use strict';

(function() {
	var rangeModule = angular.module('range', ['formFields']);

	rangeModule.directive('omRange', function(editable) {
		return {
			controller: function($scope) {
				// If the specified property does not exist on the model, add it
				if ($scope.model) {
					$scope.model[$scope.property] = $scope.model[$scope.property] || {};
				}
				$scope.editable = editable($scope);
			},
			restrict: 'E',
			scope: {
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel'
			},
			templateUrl: 'static/views/ontology/range.html'
		};
	});
}());
