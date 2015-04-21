/*global angular*/
'use strict';

(function() {
	var variableDetails = angular.module('variableDetails', ['formFields', 'input', 'textArea', 'select', 'properties', 'methods',
		'scales', 'utilities', 'variables', 'variableTypes', 'panel']),
		DELAY = 400,
		NUM_EDITABLE_FIELDS = 6;

	variableDetails.directive('omVariableDetails', ['variablesService', 'variableTypesService', 'propertiesService', 'methodsService',
		'scalesService', 'serviceUtilities', 'formUtilities', 'panelService', '$timeout',
		function(variablesService, variableTypesService, propertiesService, methodsService, scalesService, serviceUtilities, formUtilities,
		 panelService, $timeout) {

			// Reset any errors we're showing the user
		 	function resetErrors($scope) {
		 		$scope.clientErrors = {};
		 	}

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
						// Should always open in read-only view
						$scope.editing = false;
						resetErrors($scope);
					});

					// Show the expected range widget if the chosen scale has a numeric datatype
					$scope.$watch('model.scale.dataType.name', function(newValue) {
						$scope.showRangeWidget = newValue === 'Numeric';
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model &&
							$scope.model.editableFields.length < NUM_EDITABLE_FIELDS;
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
					}, true);

					// Hide the alias if the name is still editable
					$scope.hideAlias = function() {
						return $scope.model && $scope.model.editableFields && $scope.model.editableFields.indexOf('name') !== -1;
					};

					$scope.editVariable = function(e) {
						e.preventDefault();
						resetErrors($scope);

						$scope.editing = true;
					};

					$scope.deleteVariable = function(e, id) {
						e.preventDefault();
						resetErrors($scope);

						formUtilities.confirmationHandler($scope, 'confirmDelete').then(function() {
							variablesService.deleteVariable(id).then(function() {
								// Remove variable on parent scope if we succeeded
								panelService.hidePanel();
								$scope.updateSelectedVariable();
							}, function() {
								$scope.clientErrors.failedToDelete = true;
							});
						});
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						resetErrors($scope);

						// The user hasn't changed anything
						if (angular.equals($scope.model, $scope.selectedVariable)) {
							$scope.editing = false;
						} else {
							formUtilities.confirmationHandler($scope, 'confirmCancel').then(function() {
								$scope.model = angular.copy($scope.selectedVariable);
								$scope.editing = false;
							});
						}
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();
						resetErrors($scope);

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

					$scope.$watchCollection('model.variableTypes', function(newValue) {
						var filtered;

						if (newValue) {
							filtered = newValue.filter(function(type) {
				 				//TODO change to filtering by id when real service is hooked in
				 				return type.name === 'Treatment Factor';
				 			});
							$scope.showTreatmentFactorAlert = filtered.length > 0;
						}
					});
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/variableDetails.html'
			};
		}
	]);
})();
