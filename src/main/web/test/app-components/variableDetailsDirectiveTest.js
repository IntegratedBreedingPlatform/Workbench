/*global angular, inject*/
'use strict';

describe('Variable details directive', function() {
	var scope,
		directiveElement;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('variableDetails'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope;
	}));

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-variable-details></om-variable-details>')(scope);
		});
		scope.$digest();
	}

	//TODO: Implement tests!!!

});
