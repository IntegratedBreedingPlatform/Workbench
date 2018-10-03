/*global angular*/
'use strict';

(function () {
	var app = angular.module('addFormula', ['input', 'textArea', 'variables', 'variableState', 'utilities', 'errorList', 'variableSelect','contentEditable']);

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

			$scope.insertTrait = function(variableName) {

				// Elements with contenteditable=false cannot be deleted by using Backspace in Firefox, so as a workaround,
				// we insert the token as input button.
				$scope.insertHtmlAtCaret("<input type='button' disabled=\"disabled\" class=\"token\" value=\"{{" + variableName + "}}\" />");
			};

			$scope.cancel = function (e) {
				e.preventDefault();
				variableStateService.reset();
				$location.path(VARIABLES_PATH + $scope.model.id);

			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'afForm');

            $scope.insertHtmlAtCaret = function(html) {

                document.getElementById('omDefinition').focus();

                var sel, range;
                if (window.getSelection) {
                    // IE9 and non-IE
                    sel = window.getSelection();

                    if (sel.getRangeAt && sel.rangeCount) {

                        range = sel.getRangeAt(0);
                        range.deleteContents();

                        var el = document.createElement("div");
                        el.innerHTML = html;
                        var frag = document.createDocumentFragment(), node, lastNode;
                        while ( (node = el.firstChild) ) {
                            lastNode = frag.appendChild(node);
                        }
                        range.insertNode(frag);

                        // Preserve the selection
                        if (lastNode) {
                            range = range.cloneRange();
                            range.setStartAfter(lastNode);
                            range.collapse(true);
                            sel.removeAllRanges();
                            sel.addRange(range);
                        }
                    }
                } else if (document.selection && document.selection.type != "Control") {
                    // IE < 9
                    document.selection.createRange().pasteHTML(html);
                }
            };
		}
	]);
}());
