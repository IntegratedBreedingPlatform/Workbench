/*global angular*/
'use strict';

(function() {
	var variableDetails = angular.module('variableDetails', ['input', 'textArea', 'select', 'properties', 'methods', 'scales', 'utilities',
		'variables', 'variableTypes', 'panel', 'debounce', 'expandCollapseHeading']),
		DELAY = 400,
		DEBOUNCE_TIME = 500,
		NUM_EDITABLE_FIELDS = 6;

	variableDetails.directive('omVariableDetails', ['variablesService', 'variableTypesService', 'propertiesService', 'methodsService',
		'scalesService', 'serviceUtilities', 'formUtilities', 'panelService', '$timeout', 'debounce',
		function(variablesService, variableTypesService, propertiesService, methodsService, scalesService, serviceUtilities, formUtilities,
			panelService, $timeout, debounce) {

			var TREATMENT_FACTOR_ID = 9;

			// Reset any errors we're showing the user
			function resetErrors($scope) {
				$scope.clientErrors = {};
				$scope.serverErrors = {};
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
					//Whether or not to display the error message that some lists were not loaded
					$scope.someListsNotLoaded = false;

					$scope.$watch('selectedVariable', function(variable) {
						// Should always open in read-only view
						$scope.editing = false;
						resetErrors($scope);

						// If a confirmation handler was in effect, get rid of it
						if ($scope.deny) {
							$scope.deny();
						}
						$scope.model = angular.copy(variable);
						$scope.variableName = $scope.model ? $scope.model.name : '';
						$scope.deletable = variable && variable.metadata && variable.metadata.deletable || false;
					});

					// Show the expected range widget if the chosen scale has a numeric datatype
					$scope.$watch('model.scale.dataType.name', function(newValue) {
						$scope.showRangeWidget = newValue === 'Numeric';
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model &&
							$scope.model.metadata.editableFields.length < NUM_EDITABLE_FIELDS;
					});

					propertiesService.getProperties().then(function(properties) {
						$scope.data.properties = properties;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					methodsService.getMethods().then(function(methods) {
						$scope.data.methods = methods;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					scalesService.getScalesWithNonSystemDataTypes().then(function(scales) {
						$scope.data.scales = scales;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					variableTypesService.getTypes().then(function(types) {
						$scope.data.types = types;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.variableId = selected && selected.id || null;
					}, true);

					function resetSubmissionState() {
						$scope.submitted = false;
						$scope.showThrobber = false;
					}

					$scope.showAlias = function() {
						var aliasHasValue = $scope.model && $scope.model.alias && $scope.model.alias !== '',
							aliasIsEditable = $scope.model && $scope.model.metadata && $scope.model.metadata.editableFields &&
								$scope.model.metadata.editableFields.indexOf('alias') !== -1;

						return $scope.editing && aliasIsEditable || aliasHasValue;
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

							if (model.alias && model.alias !== '') {
								model.favourite = true;
							}
							variablesService.updateVariable(id, model).then(function() {

								// Update variable on parent scope if we succeeded
								$scope.updateSelectedVariable(model);

								$scope.editing = false;
								resetSubmissionState();
								$scope.variableName = model.name;
							}, function(response) {
								$scope.vdForm.$setUntouched();
								$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
								resetSubmissionState();
							});
						}
					};

					// Exposed for testing
					$scope.toggleFavourites = function(id, model) {
						model.favourite = !model.favourite;
						$scope.selectedVariable.favourite = !$scope.selectedVariable.favourite;
						$scope.updateSelectedVariable($scope.selectedVariable);
						variablesService.updateVariable(id, $scope.selectedVariable);
					};

					$scope.debouncedToggleFavourites = debounce($scope.toggleFavourites, DEBOUNCE_TIME, true);

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'vdForm');

					$scope.$watchCollection('model.variableTypes', function(newValue) {
						var filtered;

						if (newValue) {
							filtered = newValue.filter(function(type) {
								return type.id === TREATMENT_FACTOR_ID;
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
