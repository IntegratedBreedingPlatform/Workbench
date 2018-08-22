/*global angular*/
'use strict';

(function() {
	var textAreaModule = angular.module('textArea', ['formFields']);

	textAreaModule.directive('omTextArea', ['editable', function(editable) {
		return {
			controller: ['$scope', function($scope) {
				$scope.editable = editable($scope);

				// We cannot assign values to one time binding scope properties that are not defined
				// on the directive instance, so instead we must use a different scope property
				// and just read from the initial property as to whether the value was given or not.
				$scope.required = $scope.omRequired || false;
				$scope.maxLength = $scope.omMaxLength || -1;
			}],
			restrict: 'E',
			scope: {
				name: '@omName',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel',
				// Use this syntax for optional one time binding properties
				omRequired: '@',
				omMaxLength: '@'
			},
			templateUrl: 'static/views/ontology/textArea.html'
		};
	}]);
})();
