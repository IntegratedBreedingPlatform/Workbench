/*global angular*/
'use strict';

(function() {
	var textAreaModule = angular.module('textArea', ['formFields']);

	textAreaModule.directive('omTextArea', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
				$scope.maxLength = $scope.maxLength || -1;
			},
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
	});
})();
