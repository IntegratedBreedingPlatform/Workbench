/*global angular*/
'use strict';

(function() {
	var inputModule = angular.module('input', ['formFields']);

	inputModule.directive('omInput', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
				$scope.required = $scope.required || false;
				$scope.maxLength = $scope.maxLength || -1;
				$scope.regex = $scope.pattern ? new RegExp($scope.pattern) : /[\s\S]*/;
			},
			restrict: 'E',
			scope: {
				id: '@omId',
				label: '@omLabel',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel',
				required: '@omRequired',
				maxLength: '@omMaxLength',
				pattern: '@omPattern',
			},
			templateUrl: 'static/views/ontology/input.html'
		};
	});

})();
