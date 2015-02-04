/*global angular, inject, expect*/
'use strict';

describe('List module', function() {
	var scope,
		element;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('list'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
		scope.testData = [{
			name: 'Cat',
			description: 'A fluffy animal that likes to sleep.'
		}];
	}));

	function compileDirective(customTemplate) {
		var defaultTemplate = '<list data="testData"></list>',
			template = customTemplate ? customTemplate : defaultTemplate;

		inject(function($compile) {
			element = $compile(template)(scope);
		});

		scope.$digest();
	}

	it('should contain one list item when passed an array with one item', function() {
		compileDirective();
		expect(element.find('li').length).toEqual(1);
	});
});
