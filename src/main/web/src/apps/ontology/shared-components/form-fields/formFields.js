/*global angular*/
'use strict';

(function() {
	var formFieldsModule = angular.module('formFields', ['ngSanitize', 'ui.select']);

	formFieldsModule.factory('editable', function() {
		return function($scope) {
			return function() {
				return $scope.adding || ($scope.editing &&
					$scope.model && $scope.model.metadata && $scope.model.metadata.editableFields &&
					$scope.model.metadata.editableFields.indexOf($scope.property) !== -1);
			};
		};
	});

})();
