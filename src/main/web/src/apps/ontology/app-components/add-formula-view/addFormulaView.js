/*global angular*/
'use strict';

(function () {
	var app = angular.module('addFormula', ['input', 'textArea', 'variables', 'variableState', 'utilities', 'errorList']);

	app.controller('AddFormulaController', ['$scope', '$window', '$location', 'variablesService', 'variableStateService' ,'serviceUtilities', 'formUtilities',
		function ($scope, $window, $location, variablesService,
				  variableStateService, serviceUtilities, formUtilities) {

			var VARIABLES_PATH = '/variables/', storedData;
			$scope.serverErrors = {};
			storedData = variableStateService.getVariableState();
			variableStateService.reset();

			$scope.model = storedData.variable;
			$scope.serverErrors.general = storedData.errors;

			$scope.saveFormula = function (e, variable) {
				e.preventDefault();

				if ($scope.afForm.$valid) {
					$scope.submitted = true;
					$scope.serverErrors = {};

					variablesService.addFormula(variable.formula).then(function () {
						variableStateService.reset();
						$location.path(VARIABLES_PATH + variable.id);
						variablesService.deleteVariablesFromCache([parseInt(variable.id)]);
					}, function (response) {
						$scope.afForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
						$scope.submitted = false;
					});
				}
			};

			$scope.cancel = function (e) {
				e.preventDefault();
				variableStateService.reset();
				$location.path(VARIABLES_PATH + $scope.model.id);

			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'afForm');
		}
	]);
}());
