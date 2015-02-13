/*global angular, inject, expect*/
'use strict';

describe('List module', function() {
	var LIST_ITEM_CAT = {
			name: 'Cat',
			description: 'A fluffy animal that likes to sleep.'
		},
		LIST_ITEM_DOG = {
			name: 'Dog',
			description: 'A playful animal that likes walks'
		},

		scope,
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
			element = $compile('<omlist omdata="testData" omcolheaders="testHeaders"></omlist>')(scope);
		});

		scope.$digest();
	}

	it('should contain one row when passed an array with one item', function() {
		scope.testHeaders = ['name', 'description'];
		scope.testData = [LIST_ITEM_CAT];

		compileDirective();
		expect(element.find('tbody').find('tr').length).toEqual(1);
	});

	it('should contain two list items when passed an array with two items', function() {
		scope.testHeaders = ['name', 'description'];
		scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

		compileDirective();
		expect(element.find('tbody').find('tr').length).toEqual(2);
	});

	it('should have two column headers when passed an array of two headers', function() {
		scope.testHeaders = ['name', 'description'];
		compileDirective();
		expect(element.find('th').length).toEqual(2);
	});

	it('should display the correct values in the appropriate columns', function() {

		var firstHeader = 'name',
			secondHeader = 'description';

		scope.testHeaders = [firstHeader, secondHeader];
		scope.testData = [LIST_ITEM_CAT];

		compileDirective();

		expect(element.find('.om-li-header-cell-test')[0]).toContainText(firstHeader);
		expect(element.find('.om-li-data-cell-test')[0]).toContainText(LIST_ITEM_CAT[firstHeader]);

		expect(element.find('.om-li-header-cell-test')[1]).toContainText(secondHeader);
		expect(element.find('.om-li-data-cell-test')[1]).toContainText(LIST_ITEM_CAT[secondHeader]);
	});

});
