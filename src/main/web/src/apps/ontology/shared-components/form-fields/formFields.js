/*global angular*/
'use strict';

(function() {
	var formFieldsModule = angular.module('formFields', ['ngSanitize', 'ui.select']);

	formFieldsModule.factory('editable', function() {
		return function($scope){
			return function() {
				return $scope.adding || ($scope.editing &&
					$scope.model && $scope.model.editableFields &&
					$scope.model.editableFields.indexOf($scope.property) !== -1);
			};
		};
	});

	// Filter to return objects from an array of items by the id in the object, using the passed
	// in id as the filter parameter.
	formFieldsModule.filter('id', function($filter){
		return function(items, id){
			return $filter('filter')(items, {id: id}, true);
		};
	});
})();
