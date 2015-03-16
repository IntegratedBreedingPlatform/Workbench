/*global angular*/
'use strict';

(function() {
	var variableDetailsModule = angular.module('variableDetails', ['ngSanitize', 'ui.select', 'formFields', 'properties', 'methods',
		'scales', 'utilities', 'variables']);

	variableDetailsModule.directive('omVariableDetails', ['variablesService', 'propertiesService', 'methodsService', 'scalesService',
		'serviceUtilities',
		function(variablesService, propertiesService, methodsService, scalesService, serviceUtilities) {

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.data = {
						properties: [],
						methods: [],
						scales: [],
						types: []
					};

					$scope.$watch('selectedVariable', function(variable) {
						$scope.model = angular.copy(variable);
					});

					propertiesService.getProperties().then(function(properties) {
						$scope.data.properties = properties;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);

					methodsService.getMethods().then(function(methods) {
						$scope.data.methods = methods;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);

					scalesService.getScales().then(function(scales) {
						$scope.data.scales = scales;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);

					variablesService.getTypes().then(function(types) {
						$scope.data.types = types;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);

					$scope.$watch('selectedItem', function(selected) {
						$scope.variableId = selected && selected.id || null;
					});

					// Hide the alias if the name is still editable
					$scope.hideAlias = function() {
						return $scope.model && $scope.model.editableFields && $scope.model.editableFields.indexOf('name') !== -1;
					};

					$scope.editVariable = function(e) {
						e.preventDefault();
						$scope.editing = true;
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						$scope.editing = false;
						$scope.model = angular.copy($scope.selectedVariable);
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();

						variablesService.updateVariable(id, model).then(function() {

							// Update variable on parent scope if we succeeded
							$scope.updateSelectedVariable(model);

							$scope.editing = false;
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					};
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/variableDetails.html'
			};
		}
	]);
})();
