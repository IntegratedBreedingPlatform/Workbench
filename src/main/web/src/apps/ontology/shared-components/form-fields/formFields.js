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

	formFieldsModule.directive('omTextArea', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
				$scope.maxLength = $scope.maxLength || -1;
			},
			restrict: 'E',
			scope: {
				id: '@omId',
				label: '@omLabel',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=omModel',
				maxLength: '@omMaxLength'
			},
			templateUrl: 'static/views/ontology/textArea.html'
		};
	});

	formFieldsModule.directive('omInput', function(editable) {
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

	formFieldsModule.directive('omSelect', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
				$scope.required = $scope.required || false;
			},
			link: function(scope, elm, attrs, ctrl) {
				scope.$watch('model[property]', function (data) {
					ctrl.$setValidity('required', true);

					if (scope.required && !data) {
						ctrl.$setValidity('required', false);
					}
				});
			},
			require: 'ngModel',
			restrict: 'E',
			scope: {
				// omOptions must be an array of objects with (at least) name and id properties.
				options: '=omOptions',
				id: '@omId',
				label: '@omLabel',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=ngModel',
				required: '@omRequired'
			},
			templateUrl: 'static/views/ontology/select.html'
		};
	});

	formFieldsModule.directive('omMultiUiSelect', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
			},
			restrict: 'E',
			scope: {
				// omOptions must be an array of objects with (at least) name and id properties.
				options: '=omOptions',
				id: '@omId',
				label: '@omLabel',
				property: '@omProperty',
				editing: '=omEditing',
				model: '=omModel'
			},
			templateUrl: 'static/views/ontology/multiUiSelect.html'
		};
	});

	formFieldsModule.directive('omTagUiSelect', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
			},
			restrict: 'E',
			scope: {
				// omOptions must be an array of strings.
				options: '=omOptions',
				id: '@omId',
				label: '@omLabel',
				property: '@omProperty',
				editing: '=omEditing',
				model: '=omModel'
			},
			templateUrl: 'static/views/ontology/tagUiSelect.html'
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
