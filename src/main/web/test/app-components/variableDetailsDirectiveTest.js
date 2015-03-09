/*global angular, expect, inject*/
'use strict';

describe('Variable details directive', function() {
	var VARIABLE = {
			cropOntologyId: 'CO_123',
			description: 'A little vigorous',
			name: 'Plant Vigour'
		},
		scope,
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
		scope.variable = VARIABLE;
		console.log(scope.variable);

		inject(function($compile) {
			directiveElement = $compile('<om-variable-details om-variable="variable"></om-variable-details>')(scope);
		});
		scope.$digest();
	}

	it('should show the keys and values of the passed in variable', function() {
		compileDirective();
		console.log(directiveElement);
		expect(directiveElement).toContainText('cropOntologyId : CO_123');
		expect(directiveElement).toContainText('description : A little vigorous');
		expect(directiveElement).toContainText('name : Plant Vigour');
	});

});
