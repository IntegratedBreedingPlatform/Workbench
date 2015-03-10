/*global angular, expect, inject*/
'use strict';

describe('Variable details directive', function() {
	var VARIABLE = {
			cropOntologyId: 'CO_123',
			editableFields: [
				'description'
			],
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
		inject(function($compile) {
			directiveElement = $compile('<om-variable-details></om-variable-details>')(scope);
		});
		scope.$digest();
	}

	// FIXME This test is useless, we should get rid of it and test the actual JS
	it('should show the values of the passed in variable', function() {
		scope.selectedVariable = VARIABLE;
		compileDirective();

		expect(directiveElement).toContainText('CO_123');
		expect(directiveElement).toContainText('A little vigorous');
		expect(directiveElement).toContainText('Plant Vigour');
	});

});
