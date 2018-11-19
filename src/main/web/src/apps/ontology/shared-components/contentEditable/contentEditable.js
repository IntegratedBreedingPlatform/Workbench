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


                $scope.transformContent = function() {
                    if ($scope.contentValueTransformer) {
                        return $scope.contentValueTransformer({ data: $scope.id });
                    } else {
                        return $(contentEditableDiv).text();
                    }
                };

                $scope.load = function(elm) {
                    var contentEditableDiv = elm.children($scope.name)[0];
                    $(contentEditableDiv).html($scope.onLoad({ data: $scope.model.definition }));
                };

            }],
            link: function(scope, elm, attrs, ctrl) {

                // Bind the content of the content editable div to the model
                elm.children(scope.name).bind('blur', function() {
                    scope.$apply(function() {
                        scope.model[scope.property] = scope.transformContent();
                    });
                });

                // Prevent the users from typing or pasting text if the maximum length is reached.
                elm.children(scope.name).on('keypress paste', function(evt) {

                    var backspaceKeyCode = 8;
                    var deleteKeyCode = 46;

                    if (evt.keyCode === backspaceKeyCode
                        || evt.keyCode === deleteKeyCode) {
                        return true;
                    }

                    if (scope.transformContent().length > scope.maxLength) {
                        evt.preventDefault();
                        return false;
                    }
                });

                scope.load(elm);


            },
            restrict: 'E',
            scope: {
                id: '@omId',
                name: '@omName',
                property: '@omProperty',
                model: '=omModel',
                // Use this syntax for optional one time binding properties
                omRequired: '@',
                omMaxLength: '@',
                onLoad: '&onLoad',
                contentValueTransformer: '&contentValueTransformer'
            },
            templateUrl: 'static/views/ontology/contentEditable.html'
        };
    }]);
})();


