/*global angular*/
'use strict';

(function() {
	var textAreaModule = angular.module('textArea', ['formFields']);

	textAreaModule.directive('omTextArea', ['editable', function(editable) {
		return {
			controller: ['$scope', function($scope) {
				$scope.editable = editable($scope);
				$scope.maxLength = $scope.maxLength || -1;
			}],
			restrict: 'E',
			scope: {
				name: '@omName',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel',
				maxLength: '@omMaxLength'
			},
			templateUrl: 'static/views/ontology/textArea.html'
		};
	}]);
})();
