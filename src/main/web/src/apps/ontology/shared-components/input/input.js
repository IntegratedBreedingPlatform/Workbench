/*global angular*/
'use strict';

(function() {
	var inputModule = angular.module('input', ['formFields']);

	inputModule.directive('omInput', ['editable', function(editable) {
		return {
			controller: ['$scope', function($scope) {
				$scope.editable = editable($scope);
				$scope.isDisabled = function(){
					return $scope.inputDisabled;
				};
				// We cannot assign values to one time binding scope properties that are not defined
				// on the directive instance, so instead we must use a different scope property
				// and just read from the initial property as to whether the value was given or not.
				$scope.required = $scope.omRequired || false;
				$scope.maxLength = $scope.omMaxLength || -1;
				$scope.inputDisabled = $scope.inputDisabled || false;
				$scope.regex = $scope.pattern ? new RegExp($scope.pattern) : /[\s\S]*/;
			}],
			restrict: 'E',
			scope: {
				name: '@omName',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel',
				pattern: '@omPattern',
				// Use this syntax for optional one time binding properties
				omRequired: '@',
				omMaxLength: '@',
				inputDisabled: '=omInputDisabled'
			},
			templateUrl: 'static/views/ontology/input.html'
		};
	}]);

})();
