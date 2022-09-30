/*global angular*/
'use strict';

(function() {
	var variableDetails = angular.module('variableDetails', ['input', 'textArea', 'select', 'properties', 'methods', 'scales', 'utilities',
		'variables', 'variableTypes', 'panel', 'debounce', 'expandCollapseHeading', 'multiSelect', 'range', 'errorList']),
		DELAY = 400,
		DEBOUNCE_TIME = 500,
		NUM_EDITABLE_FIELDS = 6;

	variableDetails.directive('omVariableDetails', ['variablesService', 'variableTypesService', 'propertiesService', 'methodsService',
		'scalesService', 'serviceUtilities', 'formUtilities', 'panelService', '$timeout', 'debounce', 'variableStateService',
		function (variablesService, variableTypesService, propertiesService, methodsService, scalesService, serviceUtilities, formUtilities,
				  panelService, $timeout, debounce, variableStateService) {

			var TREATMENT_FACTOR_ID = 9,
				LISTS_NOT_LOADED_TRANSLATION = 'validation.variable.someListsNotLoaded';

			// Reset any errors we're showing the user
			function resetErrors($scope) {
				$scope.clientErrors = {};
				$scope.serverErrors = {};
			}

			return {
				controller: ['$scope', function($scope) {
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
						// Should always open in read-only view
						$scope.editing = false;
						resetErrors($scope);

						// If a confirmation handler was in effect, get rid of it
						if ($scope.deny) {
							$scope.deny();
						}
						$scope.model = angular.copy(variable);
						$scope.variableName = $scope.model ? $scope.model.name : '';
						$scope.deletable = Boolean(variable && variable.metadata && variable.metadata.deletable);
						$scope.isSystemVariable = Boolean(variable && variable.metadata && variable.metadata.usage && variable.metadata.usage.systemTerm);
						$scope.formulaInUsed = Boolean(!$scope.deletable && $scope.model && $scope.model.formula && $scope.model.formula.formulaId);
						$scope.showAlias = $scope.model && $scope.model.variableTypes && $scope.model.variableTypes.filter(isVariableTypeAllowed).length > 0;

						if ($scope.model && $scope.model.metadata) {
							$scope.model.metadata.disableFields = [];
						}
					});

					function isVariableTypeNotAllowed(variableType) {
						return ['1807', '1808', '1802', '1813', '1814', '1815', '1816'].indexOf(variableType.id) === -1;
					}
					function isVariableTypeAllowed(variableType) {
						return ['1807', '1808', '1802', '1813', '1814', '1815', '1816'].indexOf(variableType.id) > -1;
					}

					// Show the expected range widget if the chosen scale has a numeric datatype
					$scope.$watch('model.scale.dataType.name', function(newValue) {
						$scope.showRangeWidget = newValue === 'Numeric';
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model &&
							$scope.model.metadata.editableFields.length < NUM_EDITABLE_FIELDS;
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.variableId = selected && selected.id || null;
					}, true);

					function resetSubmissionState() {
						$scope.submitted = false;
						$scope.showThrobber = false;
					}

					$scope.traitHasFormula = function () {
						return !!($scope.model && $scope.model.formula);
					};

					$scope.addNewFormula = function (e) {
						resetErrors($scope);
						$scope.model.formula = creatingFormula();
						variableStateService.storeVariableState($scope.model, null);
						$scope.addNew(e,'formula');
					};

					$scope.editFormula = function (e) {
						resetErrors($scope);
						variableStateService.storeVariableState($scope.model, null);
						$scope.addNew(e,'formula');
					};

					function creatingFormula() {
						var formula = {
							"definition": "",
							"target":  {
								id: $scope.model.id
							},
							"description": "",
							"name": "",
							"active": true,
							"formulaId": 0
						};
						return formula;
					}
					$scope.editVariable = function(e) {
						e.preventDefault();
						resetErrors($scope);

						propertiesService.getProperties().then(function(properties) {
							$scope.data.properties = properties;
						}, function(response) {
							$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							$scope.serverErrors.someListsNotLoaded = [LISTS_NOT_LOADED_TRANSLATION];
						});

						methodsService.getMethods().then(function(methods) {
							$scope.data.methods = methods;
						}, function(response) {
							$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							$scope.serverErrors.someListsNotLoaded = [LISTS_NOT_LOADED_TRANSLATION];
						});

						scalesService.getScalesWithNonSystemDataTypes().then(function(scales) {
							$scope.data.scales = scales;
						}, function(response) {
							$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							$scope.serverErrors.someListsNotLoaded = [LISTS_NOT_LOADED_TRANSLATION];
						});

						var excludeRestrictedTypes = true;
						variableTypesService.getTypes(excludeRestrictedTypes).then(function(types) {
							$scope.data.types = types;
						}, function(response) {
							$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							$scope.serverErrors.someListsNotLoaded = [LISTS_NOT_LOADED_TRANSLATION];
						});

						$scope.editing = true;
						$scope.isAliasDisabled = $scope.model && $scope.model.variableTypes && $scope.model.variableTypes.length === 0
							|| $scope.model.variableTypes.some(isVariableTypeNotAllowed);
					};

					$scope.deleteFormula = function (e, variableId) {
						e.preventDefault();
						resetErrors($scope);
						var formulaId = $scope.model.formula.formulaId;
						formUtilities.confirmationHandler($scope, 'confirmDeleteFormula').then(function () {
							variablesService.deleteFormula(formulaId).then(function () {
								$scope.model.formula = null;
								variablesService.deleteVariablesFromCache([parseInt(variableId)]);

							}, function (response) {
								var error = {};
								serviceUtilities.serverErrorHandler(error, response);
								$scope.clientErrors.deleteFormulaErrorMessage = error.general;
								$scope.clientErrors.failedToDeleteFormula = true;
							});
						});
					};

					$scope.deleteVariable = function(e, id) {
						e.preventDefault();
						resetErrors($scope);
						formUtilities.confirmationHandler($scope, 'confirmDelete').then(function() {
							variablesService.deleteVariable(id).then(function() {
								// Remove variable on parent scope if we succeeded
								panelService.hidePanel();
								$scope.updateSelectedVariable();

								variablesService.deleteVariablesFromCache([ parseInt(id) ]);

							}, function(response) {
								var error = {};
								serviceUtilities.serverErrorHandler(error, response);
								$scope.clientErrors.deleteVariableErrorMessage = error.general;
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
						var hadNoAlias;

						e.preventDefault();
						resetErrors($scope);

						if ($scope.vdForm.$valid) {
							$scope.submitted = true;
							// Reset server errors
							$scope.serverErrors = {};

							$timeout(function() {
								if ($scope.submitted) {
									$scope.showThrobber = true;
								}
							}, DELAY);

							// If the variable has been given an alias from not previously having one, we set that
							// variable as a favourite for the user.
							hadNoAlias = !$scope.selectedVariable.alias;
							if (hadNoAlias && model.alias && model.alias !== '') {
								model.favourite = true;
							}

							variablesService.updateVariable(id, model).then(function () {
								return variablesService.getVariable(id);
							}).then(function(model) {

								// Update variable on parent scope if we succeeded
								$scope.updateSelectedVariable(model);
								// evaluate the model (stdVariable) to see if we need to add
								// an alias column to the parent page - list variables
								$scope.addAliasToTableIfPresent([model]);

								$scope.editing = false;
								resetSubmissionState();
								$scope.variableName = model.name;

								variablesService.deleteVariablesFromCache([ parseInt(id) ]);

							}, function(response) {
								$scope.vdForm.$setUntouched();
								$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
								resetSubmissionState();
							});
						}
					};

					$scope.toggleObsolete = function(id, model) {
						model.obsolete = !model.obsolete;
						$scope.selectedVariable.obsolete = !$scope.selectedVariable.obsolete;
						$scope.updateSelectedVariable($scope.selectedVariable);
						variablesService.updateVariable(id, $scope.selectedVariable).then(function () {
						}, function (response) {
							model.obsolete = !model.obsolete;
							$scope.selectedVariable.obsolete = !$scope.selectedVariable.obsolete;
							$scope.updateSelectedVariable($scope.selectedVariable);
							$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							resetSubmissionState();
						});
					};

					// Exposed for testing
					$scope.toggleFavourites = function(id, model) {
						model.favourite = !model.favourite;
						$scope.selectedVariable.favourite = !$scope.selectedVariable.favourite;
						$scope.updateSelectedVariable($scope.selectedVariable);
						variablesService.updateVariable(id, $scope.selectedVariable).then(function () {
						}, function (response) {
							model.favourite = !model.favourite;
							$scope.selectedVariable.favourite = !$scope.selectedVariable.favourite;
							$scope.updateSelectedVariable($scope.selectedVariable);
							$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							resetSubmissionState();
						});
					};

					$scope.debouncedToggleFavourites = debounce($scope.toggleFavourites, DEBOUNCE_TIME, true);

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'vdForm');

					$scope.$watchCollection('model.variableTypes', function(newValue) {
						var filtered;

						if (newValue) {
							filtered = newValue.filter(function (type) {
								return type.id === TREATMENT_FACTOR_ID;
							});
							$scope.showTreatmentFactorAlert = filtered.length > 0;
							$scope.isAliasDisabled = newValue.some(isVariableTypeNotAllowed);
							$scope.showAlias = newValue.filter(isVariableTypeAllowed).length > 0;
						}

						if ($scope.isAliasDisabled) {
							if($scope.model && $scope.model.metadata && $scope.model.metadata.disableFields){
								$scope.model.alias = '';
								$scope.model.metadata.disableFields.push('alias');
							}

						} else if ($scope.model && $scope.model.metadata) {
							$scope.model.metadata.disableFields = [];
						}
					});
				}],
				restrict: 'E',
				templateUrl: 'static/views/ontology/variableDetails.html'
			};
		}
	]);
})();
