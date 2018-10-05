/*global angular*/
'use strict';

(function () {
	var app = angular.module('addFormula', ['input', 'textArea', 'variables', 'variableState', 'utilities', 'errorList',
        'variableSelect','contentEditable']);

	app.controller('AddFormulaController', ['$scope', '$window', '$location', 'variablesService', 'variableStateService'
        ,'serviceUtilities', 'formUtilities',
		function ($scope, $window, $location, variablesService,
				  variableStateService, serviceUtilities, formUtilities) {

			var VARIABLES_PATH = '/variables/', storedData;
            // matches any word (with alphanumeric characters, underscore , space and carriage return) enclosed with {{ and }}
			var VARIABLE_TOKEN_EXPRESSION = /\{\{[\w\r\s_]*\}\}/g;

			$scope.formulaDefinitionMaxLength = 255;
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

            $scope.insertTrait = function(variableName) {

                var contentEditableDivLength = $scope.extractTextFromHtml('contentEditableDiv').length;
                var variableToken = '{{' + variableName + '}}';
                // Insert the trait token if the formula field is within maxlength.
                if (contentEditableDivLength + variableToken.length < $scope.formulaDefinitionMaxLength) {
                    // Elements with contenteditable=false cannot be deleted by using Backspace in Firefox, so as a workaround,
                    // we insert the token as input button.
                    $scope
                        .insertHtmlAtCaret('<input type=\"button\" disabled=\"disabled\" class=\"token\" ' +
                            'value=\"' + variableToken + '\" />');
                }
            };

            $scope.insertHtmlAtCaret = function(html) {

                document.getElementById('contentEditableDiv').focus();

                var sel, range;
                if (window.getSelection) {
                    // IE9 and non-IE
                    sel = window.getSelection();

                    if (sel.getRangeAt && sel.rangeCount) {

                        range = sel.getRangeAt(0);
                        range.deleteContents();

                        var el = document.createElement('div');
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
                } else if (document.selection && document.selection.type !== 'Control') {
                    // IE < 9
                    document.selection.createRange().pasteHTML(html);
                }
            };

            $scope.extractTextFromHtml = function(elementId) {

                var contentEditableDiv = document.getElementById(elementId);
                // The variable tokens are inserted as <input type='button' value='{{Name}}'/>, that's why we can't rely on
                // $(elm).text() to get the text values of all elements. This function will extract the texts as well as the values
                // from input buttons.
                var n, a=[], walk=document.createTreeWalker(contentEditableDiv, NodeFilter.SHOW_ALL, null, false);
                while(n=walk.nextNode()) {
                    if (n.nodeType === Node.TEXT_NODE) {
                        a.push(n.nodeValue);
                    } else if (n.nodeType === Node.ELEMENT_NODE && n.type === 'button') {
                        a.push(n.value);
                    }
                }
                return a.join('');
            };

            $scope.convertTextToHtmlStringWithTokens = function(formulaDefinition) {

                // Convert the formula definition to a format that can be displayed in contenteditable field.
                // This will convert the variable tokens ({{variable}}) into <input type=button class=token value={{variable}}>
                // when the component loads.
                if (formulaDefinition) {
                    var matches = formulaDefinition.match(VARIABLE_TOKEN_EXPRESSION);

                    if (matches) {
                        $.each(matches, function(index, match) {
                            formulaDefinition =
                                formulaDefinition.replace(match, '<input type="button" class="token" disabled="disabled" value="'
                                    + match + '"/>');
                        });
                    }

                    return formulaDefinition;

                } else {
                    return formulaDefinition;
                }
            };
		}
	]);
}());
