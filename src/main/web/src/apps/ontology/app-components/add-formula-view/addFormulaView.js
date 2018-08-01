/*global angular*/
'use strict';

(function () {
	var app = angular.module('addFormula', ['multiSelect', 'input', 'textArea', 'select', 'variables', 'properties', 'variableTypes',
		'methods', 'scales', 'variableState', 'utilities', 'range', 'errorList']);

	app.controller('AddFormulaController', ['$scope', '$window', '$location', 'variablesService', 'variableTypesService',
		'propertiesService', 'methodsService', 'scalesService', 'variableStateService', 'serviceUtilities', 'formUtilities',

		function ($scope, $window, $location, variablesService, variableTypesService, propertiesService, methodsService, scalesService,
				  variableStateService, serviceUtilities, formUtilities) {

			var VARIABLES_PATH = '/variables/', storedData;
			$scope.serverErrors = {};
			storedData = variableStateService.getVariableState();

			$scope.variable = storedData.variable;
			$scope.serverErrors.general = storedData.errors;
			$scope.variable.formula = creatingFormula($scope.variable);
			// Clear the variable state service now that we are back on the variables screen
			variableStateService.reset();



			$scope.saveFormula = function (e, formula) {
				e.preventDefault();

				if ($scope.avForm.$valid) {
					$scope.submitted = true;
					$scope.serverErrors = {};

					variablesService.addFormula(formula).then(function () {
						variableStateService.reset();
						$location.path(VARIABLES_PATH+$scope.variable.id);
						variablesService.deleteVariablesFromCache([parseInt($scope.variable.id)]);
					}, function (response) {
						$scope.avForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
						$scope.submitted = false;
					});
				}
			};

			$scope.cancel = function (e) {
				e.preventDefault();
				variableStateService.reset();
				$location.path(VARIABLES_PATH+$scope.variable.id);

			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'avForm');

			function creatingFormula(variable) {
				var formula = {
					"definition": "",
					"targetTermId": variable.id,
					"description": "",
					"name": "",
					"active": true,
					"formulaId": 0
				};
				return formula;
			};
		}
	]);
}());
