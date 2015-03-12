/*global angular*/
'use strict';

(function() {
	var formFieldsModule = angular.module('formFields', []);

	formFieldsModule.factory('editable', function() {
		return function($scope){
			return function() {
				return $scope.editing &&
					$scope.model && $scope.model.editableFields &&
					$scope.model.editableFields.indexOf($scope.property) !== -1;
			};
		};
	});

	formFieldsModule.directive('omTextArea', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
			},
			restrict: 'E',
			scope: {
				id: '@omId',
				label: '@omLabel',
				property: '@omProperty',
				editing: '=omEditing',
				model: '=omModel'
			},
			templateUrl: 'static/views/ontology/textArea.html'
		};
	});

	formFieldsModule.directive('omInput', function(editable) {
		return {
			controller: function ($scope) {
				$scope.editable = editable($scope);
			},
			restrict: 'E',
			scope: {
				id: '@omId',
				label: '@omLabel',
				property: '@omProperty',
				editing: '=omEditing',
				model: '=omModel'
			},
			templateUrl: 'static/views/ontology/input.html'
		};
	});

	formFieldsModule.directive('omSelect', function(editable) {
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
			templateUrl: 'static/views/ontology/select.html'
		};
	});
})();
