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
	}));

	function compileDirective() {
		inject(function($compile) {
			element = $compile('<list data="testData"></list>')(scope);
		});

		scope.$digest();
	}

	it('should contain one list item when passed an array with one item', function() {
		scope.testData = [{
			name: 'Cat',
			description: 'A fluffy animal that likes to sleep.'
		}];

		compileDirective();
		expect(element.find('li').length).toEqual(1);
	});

	it('should contain two list items when passed an array with two items', function() {
		scope.testData = [{
			name: 'Cat',
			description: 'A fluffy animal that likes to sleep.'
		}, {
			name: 'Dog',
			description: 'A playful animal that likes walks'
		}];

		compileDirective();
		expect(element.find('li').length).toEqual(2);
	});

	it('should output the name and description of an item', function() {
		var NAME = 'Mouse',
			DESCRIPTION = 'A small animal that likes cheese';

		scope.testData = [{
			name: NAME,
			description: DESCRIPTION
		}];
		compileDirective();
		expect(element.find('li').text().trim()).toEqual(NAME + ' - ' + DESCRIPTION);
	});
});
