/*global angular*/
'use strict';

(function() {
    var contentEditableModule = angular.module('contentEditable', ['formFields', 'ngSanitize']);

    contentEditableModule.directive('omContentEditable', [function() {
        return {
            controller: ['$scope', function($scope) {
                // We cannot assign values to one time binding scope properties that are not defined
                // on the directive instance, so instead we must use a different scope property
                // and just read from the initial property as to whether the value was given or not.
                $scope.required = $scope.omRequired || false;
                $scope.maxLength = $scope.omMaxLength || -1;


            }],
            link: function(scope, elm, attrs, ctrl) {
                elm.children(scope.name).bind('blur', function() {
                    scope.$apply(function() {
                        scope.model[scope.property] = extractTextFromHtml($(elm)[0]);
                    });
                });

                elm.children(scope.name).on('keypress paste', function(evt) {

                    var backspaceKeyCode = 8;
                    var deleteKeyCode = 46;

                    if (evt.keyCode === backspaceKeyCode
                        || evt.keyCode === deleteKeyCode) {
                        return true;
                    }

                    if (elm.text().length > scope.maxLength) {
                        evt.preventDefault();
                        return false;
                    }
                });

                function extractTextFromHtml(elm) {

                    // The variable tokens are inserted as <input type='button' value='{{Name}}'/>, that's why we can't rely on
                    // $(elm).text() to get the text values of all elements. This function will extract the texts as well as the values
                    // from input buttons.
                    var n, a=[], walk=document.createTreeWalker(elm, NodeFilter.SHOW_ALL, null, false);
                    while(n=walk.nextNode()) {
                        if (n.nodeType === Node.TEXT_NODE) {
                            a.push(n.nodeValue);
                        } else if (n.nodeType === Node.ELEMENT_NODE && n.type === 'button') {
                            a.push(n.value);
                        }
                    }
                    return a.join('');
                };
            },
            restrict: 'E',
            scope: {
                name: '@omName',
                property: '@omProperty',
                model: '=omModel',
                // Use this syntax for optional one time binding properties
                omRequired: '@',
                omMaxLength: '@'
            },
            templateUrl: 'static/views/ontology/contentEditable.html'
        };
    }]);
})();


