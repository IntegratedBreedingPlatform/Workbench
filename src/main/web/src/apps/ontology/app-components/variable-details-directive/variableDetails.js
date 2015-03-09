/*global angular*/
'use strict';

(function() {
	var variableDetailsModule = angular.module('variableDetails', []);

	variableDetailsModule.directive('omVariableDetails', function() {

		return {
			controller: function($scope) {
				$scope.editing = false;

				$scope.editVariable = function() {
					$scope.editing = true;

					// Map the array of editable fields to an object that can be inspected to determine whether fields should be
					// displayed as editable.
					if ($scope.selectedVariable) {
						$scope.editableFields = $scope.selectedVariable.editableFields.reduce(function(editableFields, currentField) {
							editableFields[currentField] = true;
							return editableFields;
						}, {});
					}
				};

				$scope.cancel = function() {
					$scope.editing = false;
					$scope.editableFields = {};
				};
			},
			restrict: 'E',
			templateUrl: 'static/views/ontology/variableDetails.html'
		};
	});

	variableDetailsModule.directive('omTextArea', function() {
		return {
			restrict: 'E',
			scope: {
				omLabel: '@',
				omId: '@',
				omModel: '=',
				omEditable: '='
			},
			templateUrl: 'static/views/ontology/textArea.html'
		};
	});

})();
