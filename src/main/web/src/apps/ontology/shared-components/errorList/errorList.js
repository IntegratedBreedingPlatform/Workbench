/*global angular*/
'use strict';

(function() {
	var errorListModule = angular.module('errorList', ['pascalprecht.translate']);

	errorListModule.directive('omErrorList', ['$translate', function($translate) {
		return {
			controller: ['$scope', function($scope) {
				$scope.translatedErrors = [];
				$scope.errors = [];
			}],
			link: function(scope) {
				var i;

				function addToErrors (message) {
					scope.translatedErrors.push(message);
					updateErrorMessages();
				}

				function updateErrorMessages() {
					scope.errorMessages = scope.errors.concat(scope.translatedErrors);
				}

				scope.$watch('errorsToTranslate', function(errorsToTranslate) {
					scope.translatedErrors = [];

					if (errorsToTranslate) {
						for (i = 0; i < errorsToTranslate.length; i++) {
							$translate(errorsToTranslate[i]).then(addToErrors);
						}
					}

					updateErrorMessages();
				});

				scope.$watch('errorsAlreadyTranslated', function(errorsAlreadyTranslated) {
					if (errorsAlreadyTranslated) {
						scope.errors = errorsAlreadyTranslated;
					} else {
						scope.errors = [];
					}

					updateErrorMessages();
				});

			},
			restrict: 'E',
			scope: {
				errorsAlreadyTranslated: '=omErrorsAlreadyTranslated',
				errorsToTranslate: '=omErrorsToTranslate'
			},
			templateUrl: 'static/views/ontology/errorList.html'
		};
	}]);

})();
