/*global angular, inject, expect, spyOn*/
'use strict';

describe('List module', function() {
	var LIST_ITEM_CAT = {
			id: 'cat',
			name: 'Cat',
			description: 'A fluffy animal that likes to sleep.',
			'action-favourite': {
				iconValue: 'iconFavourite'
			}
		},
		LIST_ITEM_DOG = {
			id: 'dog',
			name: 'Dog',
			description: 'A playful animal that likes walks',
			'action-favourite': {
				iconValue: 'icon'
			}
		},

		scope,
		directiveElement;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('list'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	function compileDirective(extraAttrs) {
		var attrs = extraAttrs || '';

		inject(function($compile) {
			directiveElement = $compile('<om-list om-data="testData" om-col-headers="testHeaders" ' + attrs + '></om-list>')(scope);
		});

		scope.$digest();
	}

	it('should contain one row when passed an array with one item', function() {
		scope.testHeaders = ['name', 'description'];
		scope.testData = [LIST_ITEM_CAT];

		compileDirective();
		expect(directiveElement.find('tbody tr').length).toEqual(1);
	});

	it('should contain two list items when passed an array with two items', function() {
		scope.testHeaders = ['name', 'description'];
		scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

		compileDirective();
		expect(directiveElement.find('tbody tr').length).toEqual(2);
	});

	it('should have two column headers when passed an array of two headers', function() {
		scope.testHeaders = ['name', 'description'];
		compileDirective();
		expect(directiveElement.find('th').length).toEqual(2);
	});

	it('should display the correct values in the appropriate columns', function() {

		var firstHeader = 'name',
			secondHeader = 'description';

		scope.testHeaders = [firstHeader, secondHeader];
		scope.testData = [LIST_ITEM_CAT];

		compileDirective();

		expect(directiveElement.find('.om-li-header-cell-test')[0]).toContainText(firstHeader);
		expect(directiveElement.find('.om-li-data-cell-test')[0]).toContainText(LIST_ITEM_CAT[firstHeader]);

		expect(directiveElement.find('.om-li-header-cell-test')[1]).toContainText(secondHeader);
		expect(directiveElement.find('.om-li-data-cell-test')[1]).toContainText(LIST_ITEM_CAT[secondHeader]);
	});


	it('should only display column header if header is not an action header', function() {

		var normalHeader = 'name',
			actionHeader = 'action-favourite';

		scope.testHeaders = [normalHeader, actionHeader];

		compileDirective();

		expect(directiveElement.find('.om-li-header-cell-test').length).toEqual(1);
		expect(directiveElement.find('.om-li-header-cell-test')[0]).toContainText(normalHeader);
	});

	it('should display column values for non action columns and iconValues for action columns', function() {

		var normalHeader = 'name',
			actionHeader = 'action-favourite';

		scope.testHeaders = [normalHeader, actionHeader];
		scope.testData = [LIST_ITEM_CAT];

		compileDirective();

		expect(directiveElement.find('.om-li-data-cell-test')[0]).toContainText(LIST_ITEM_CAT[normalHeader]);
		expect(directiveElement.find('.om-li-data-cell-test')[1]).toContainText(LIST_ITEM_CAT[actionHeader].iconValue);
	});

	describe('scope.clickHandler', function() {

		it('should call the parent click handler and set the selected item id', function() {

			var firstHeader = 'name',
				secondHeader = 'description',

				item = {
					id: null
				},

				count = 0;

			scope.testHeaders = [firstHeader, secondHeader];
			scope.testData = [LIST_ITEM_CAT];

			scope.clickFn = function() {
				count++;
			};
			scope.selectedItem = item;

			compileDirective('om-on-click="clickFn()" om-selected-item="selectedItem"');

			// Call the click handler that would normally be invoked by a click on a list item
			directiveElement.isolateScope().clickHandler(LIST_ITEM_CAT.id);

			expect(count).toEqual(1);
			expect(item.id).toEqual(LIST_ITEM_CAT.id);
		});
	});
});
