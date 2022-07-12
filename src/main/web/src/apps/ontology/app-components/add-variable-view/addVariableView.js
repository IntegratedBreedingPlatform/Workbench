/*global angular*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['multiSelect', 'input', 'textArea', 'select', 'variables', 'properties', 'variableTypes',
		'methods', 'scales', 'variableState', 'utilities', 'range', 'errorList']);

	app.controller('AddVariableController', ['$scope', '$window', '$location', 'variablesService', 'variableTypesService',
		'propertiesService', 'methodsService', 'scalesService', 'variableStateService', 'serviceUtilities', 'formUtilities',
		'variableFormService',

		function($scope, $window, $location, variablesService, variableTypesService, propertiesService, methodsService, scalesService,
			variableStateService, serviceUtilities, formUtilities, variableFormService) {

			var VARIABLES_PATH = '/variables',
				TREATMENT_FACTOR_ID = 9,
				LISTS_NOT_LOADED_TRANSLATION = 'validation.variable.someListsNotLoaded',
				storedData;

			$scope.serverErrors = {};
			// The select2 input needs to be able to call length on the arrays used for the options before the data is returned.
			$scope.data = {
				properties: [],
				types: []
			};

			// Whether or not we want to display the expected range widget
			$scope.showRangeWidget = false;

			$scope.isAliasDisabled = true;

			// If we were half way through editing, we don't need to fetch everything again - we just need to copy over the stored state
			if (variableStateService.updateInProgress()) {

				storedData = variableStateService.getVariableState();

				$scope.variable = storedData.variable;
				$scope.data = storedData.scopeData;
				$scope.serverErrors.general = storedData.errors;

				// Clear the variable state service now that we are back on the variables screen
				variableStateService.reset();
			} else {

				$scope.variable = {
					variableTypes: []
				};

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
			}

			// Show the expected range widget if the chosen scale has a numeric datatype
			$scope.$watch('variable.scale.dataType.name', function(newValue) {
				$scope.showRangeWidget = newValue === 'Numeric';
			});

			$scope.saveVariable = function(e, variable) {
				e.preventDefault();

				if ($scope.avForm.$valid) {
					$scope.submitted = true;
					// Reset server errors
					$scope.serverErrors = {};

					variablesService.addVariable(variable).then(function() {
						variableStateService.reset();
						$location.path(VARIABLES_PATH);
					}, function(response) {
						$scope.avForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
						$scope.submitted = false;
					});
				}
			};

			$scope.cancel = function(e) {
				e.preventDefault();
				variableStateService.reset();
				formUtilities.cancelAddHandler($scope, !variableFormService.formEmpty($scope.variable), VARIABLES_PATH);
			};

			$scope.addNew = function(e, path) {
				e.preventDefault();

				// Persist the current state of the variable, so we can return to editing once we've finished
				variableStateService.storeVariableState($scope.variable, $scope.data);
				$location.path('/add/' + path);
			};

			$scope.$watchCollection('variable.variableTypes', function(newValue) {
				var filtered;

				if (newValue) {
					filtered = newValue.filter(function(type) {
						return type.id === TREATMENT_FACTOR_ID;
					});
					$scope.showTreatmentFactorAlert = filtered.length > 0;
					$scope.isAliasDisabled = newValue.length === 0 || newValue.some(isVariableTypeNotAllowed);
				}

				if ($scope.isAliasDisabled) {
					$scope.variable.alias = '';
				}
			});

			function isVariableTypeNotAllowed(variableType) {
				return ['1807', '1808', '1802', '1813', '1814', '1815', '1816'].indexOf(variableType.id) === -1;
			}

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'avForm');
		}
	]);

	app.factory('variableFormService', function() {

		return {
			formEmpty: function(model) {
				var isUndefined = angular.isUndefined,
					formEmpty;

				// Don't bother checking for expected range, because for there to be an expected range
				// there must be a scale, in which case the form isn't empty.. :)
				formEmpty = !!!model.name &&
					!!!model.description &&
					isUndefined(model.property) &&
					isUndefined(model.method) &&
					isUndefined(model.scale) &&
					(isUndefined(model.variableTypes) ||
						(angular.isArray(model.variableTypes) && model.variableTypes.length === 0));

				return formEmpty;
			}
		};
	});
}());
