/*global angular*/
'use strict';

(function() {
	var formFieldsModule = angular.module('formFields', []);

	formFieldsModule.directive('omTextArea', function() {
		return {
			restrict: 'E',
			scope: {
				omLabel: '@',
				omId: '@',
				omModel: '=',
				omEditable: '='
			},
			templateUrl: 'static/views/ontology/textArea.html'
		};
	});

	formFieldsModule.directive('omInput', function() {
		return {
			restrict: 'E',
			scope: {
				omLabel: '@',
				omId: '@',
				omModel: '=',
				omEditable: '='
			},
			templateUrl: 'static/views/ontology/input.html'
		};
	});

})();

