/*global angular*/
'use strict';

(function() {
	var variableDetailsModule = angular.module('variableDetails', ['formFields', 'input', 'textArea', 'select', 'properties', 'methods',
		'scales', 'utilities', 'variables', 'variableTypes', 'panel']),
		DELAY = 400;

	variableDetailsModule.directive('omVariableDetails', ['variablesService', 'variableTypesService', 'propertiesService', 'methodsService',
		'scalesService', 'serviceUtilities', 'formUtilities', 'panelService', '$timeout',
		function(variablesService, variableTypesService, propertiesService, methodsService, scalesService, serviceUtilities, formUtilities,
		 panelService, $timeout) {

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.data = {
						properties: [],
						methods: [],
						scales: [],
						types: []
					};

					// Whether or not we want to display the expected range widget
					$scope.showRangeWidget = false;

					$scope.$watch('selectedVariable', function(variable) {
						$scope.model = angular.copy(variable);
						$scope.deletable = variable && variable.deletable || false;
					});

					// Show the expected range widget if the chosen scale has a numeric datatype
					$scope.$watch('model.scale.dataType.name', function(newValue) {
						$scope.showRangeWidget = newValue === 'Numeric';
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

					variableTypesService.getTypes().then(function(types) {
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

					$scope.deleteVariable = function(e, id) {
						e.preventDefault();

						variablesService.deleteVariable(id).then(function() {
							// Remove variable on parent scope if we succeeded
							panelService.hidePanel();
							$scope.updateSelectedVariable();
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						$scope.editing = false;
						$scope.model = angular.copy($scope.selectedVariable);
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();

						if ($scope.vdForm.$valid) {
							$scope.submitted = true;
							$timeout(function() {
								if ($scope.submitted) {
									$scope.showThrobber = true;
								}
							}, DELAY);

							variablesService.updateVariable(id, model).then(function() {

								// Update variable on parent scope if we succeeded
								$scope.updateSelectedVariable(model);

								$scope.editing = false;
								$scope.submitted = false;
								$scope.showThrobber = false;
							}, serviceUtilities.genericAndRatherUselessErrorHandler);
						}
					};

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'vdForm');
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/variableDetails.html'
			};
		}
	]);
})();
