/*global angular*/
'use strict';

(function() {
	var app = angular.module('addVariable', ['multiSelect', 'input', 'textArea', 'select', 'variables', 'properties', 'variableTypes',
		'methods', 'scales', 'variableState', 'utilities']);

	app.controller('AddVariableController', ['$scope', '$window', '$location', 'variablesService', 'variableTypesService',
		'propertiesService', 'methodsService', 'scalesService', 'variableStateService', 'serviceUtilities', 'formUtilities',
		'variableFormService',

		function($scope, $window, $location, variablesService, variableTypesService, propertiesService, methodsService, scalesService,
			variableStateService, serviceUtilities, formUtilities, variableFormService) {

			var VARIABLES_PATH = '/variables',
				storedData;

			$scope.serverErrors = {};

			// The select2 input needs to be able to call length on the arrays used for the options before the data is returned.
			$scope.data = {
				properties: [],
				types: []
			};

			// Whether or not we want to display the expected range widget
			$scope.showRangeWidget = false;

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
					$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
				});

				methodsService.getMethods().then(function(methods) {
					$scope.data.methods = methods;
				}, function(response) {
					$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
				});

				scalesService.getScales().then(function(scales) {
					$scope.data.scales = scales;
				}, function(response) {
					$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
				});

				variableTypesService.getTypes().then(function(types) {
					$scope.data.types = types;
				}, function(response) {
					$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
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

					variablesService.addVariable(variable).then(function() {
						variableStateService.reset();
						$location.path(VARIABLES_PATH);
					}, function(response) {
						$scope.avForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
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
						//TODO change to filtering by id when real service is hooked in
						return type.name === 'Treatment Factor';
					});
					$scope.showTreatmentFactorAlert = filtered.length > 0;
				}
			});

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'avForm');
		}
	]);

	app.factory('variableFormService', [function() {

		return {
			formEmpty: function(model) {
				var isUndefined = angular.isUndefined,
					formEmpty;

				// Don't bother checking for expected range, because for there to be an expected range
				// there must be a scale, in which case the form isn't empty.. :)
				formEmpty = !!!model.name &&
					!!!model.description &&
					isUndefined(model.propertySummary) &&
					isUndefined(model.methodSummary) &&
					isUndefined(model.scale) &&
					(isUndefined(model.variableTypes) ||
						(angular.isArray(model.variableTypes) && model.variableTypes.length === 0));

				return formEmpty;
			}
		};
	}]);
}());
