/*global angular, inject, expect, spyOn*/
'use strict';

describe('List module', function() {
	var LIST_ITEM_CAT = {
			id: 'cat',
			name: 'Cat',
			description: 'A fluffy animal that likes to sleep.',
			'action-favourite': {
				iconValue: 'star'
			}
		},
		LIST_ITEM_DOG = {
			id: 'dog',
			name: 'Dog',
			description: 'A playful animal that likes walks',
			'action-favourite': {
				iconValue: 'star-empty'
			}
		},

		fakeScrollElement = {
			scrollTop: 100 // Set the number of pixels scrolled
		},

		scope,
		directiveElement,
		mockTranslateFilter;

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};
	});

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('list'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope;
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

		scope.testHeaders = [firstHeader, secondHeader, ''];
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
		expect(directiveElement.find('.om-li-data-cell-test')[1]).toHaveClass('glyphicon-' + LIST_ITEM_CAT[actionHeader].iconValue);
	});

	describe('scope.selectItem', function() {

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
			directiveElement.isolateScope().selectItem(1, LIST_ITEM_CAT.id);

			expect(count).toEqual(1);
			expect(item.id).toEqual(LIST_ITEM_CAT.id);
		});

		it('should focus on the table after panel is closed', function() {
			var item = { id: null };

			scope.selectedItem = item;
			compileDirective('om-on-click="clickFn()" om-selected-item="selectedItem"');

			directiveElement.isolateScope().selectItem(1, LIST_ITEM_CAT.id);
			var tableHtml = directiveElement.find('table')[0];
			spyOn(tableHtml, 'focus');

			scope.$broadcast('panelClose');
			expect(tableHtml.focus).toHaveBeenCalled();
		});

		it('should show active item in the list on table focus', function() {
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			compileDirective();
			directiveElement.find('table').triggerHandler('focus');
			expect(directiveElement.find('tr.active').length).toBe(1);
		});

		it('should remove active item selection from the list on table blur', function() {
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			compileDirective();
			directiveElement.find('table').triggerHandler('focus');
			directiveElement.find('table').triggerHandler('blur');
			expect(directiveElement.find('tr.active').length).toBe(0);
		});
	});

	describe('scope.scroll', function() {

		it('should set scroll top of the scroll element', function() {
			compileDirective();

			directiveElement.isolateScope().scroll(fakeScrollElement, 20, 5, 15, 10);
			expect(fakeScrollElement.scrollTop).toEqual(-965);
		});

		it('should set scroll top of the scroll element when current time is less than duration', function() {
			compileDirective();

			directiveElement.isolateScope().scroll(fakeScrollElement, 20, 5, 15, -20);
			expect(fakeScrollElement.scrollTop).toEqual(15);
		});
	});

	describe('scope.isScrolledIntoView', function() {

		it('should not break if the falsy value is pased', function() {
			compileDirective();
			expect(directiveElement.isolateScope().isScrolledIntoView(null)).toEqual(false);
		});
	});

	describe('keyDown', function() {
		var DOWN_KEY = 40,
			UP_KEY = 38,
			ENTER_KEY = 13,

			keyboardEvent = {
				which: 0,
				type: 'keydown'
			};

		it('should listen to the Enter keydown event and select an item on Enter', function() {
			var item = {
					id: null
				},
				isolateScope;

			keyboardEvent.which = ENTER_KEY;

			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.selectedItem = item;
			isolateScope.data = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			spyOn(isolateScope, 'selectItem');

			// we pass an event object which then gets appended to the dummy object in the angular triggerHandlerEvent()
			directiveElement.triggerHandler(keyboardEvent);
			expect(isolateScope.selectItem).toHaveBeenCalled();
		});

		it('should listen to the Down keydown event and update active item if the end of list items is not reached', function() {
			var item = {
					id: null
				},
				isolateScope;

			keyboardEvent.which = DOWN_KEY;
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.selectedItem = item;
			isolateScope.data = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			spyOn(isolateScope, 'updateActiveItem');

			// we pass an event object which then gets appended to the dummy object in the angular triggerHandlerEvent()
			directiveElement.triggerHandler(keyboardEvent);
			expect(isolateScope.updateActiveItem).toHaveBeenCalled();
		});

		it('should listen to the Down keydown event and scroll the list to active item', function() {
			var item = {
					id: null
				},
				isolateScope;

			keyboardEvent.which = DOWN_KEY;
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.selectedItem = item;
			isolateScope.data = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			isolateScope.isScrolledIntoView = function() { return false; };
			spyOn(isolateScope, 'scroll');

			// we pass an event object which then gets appended to the dummy object in the angular triggerHandlerEvent()
			directiveElement.triggerHandler(keyboardEvent);
			expect(isolateScope.scroll).toHaveBeenCalled();
		});

		it('should listen to the Down keydown event and should no do anything if the end of list items is reached', function() {
			var item = {
					id: null
				},
				isolateScope;

			keyboardEvent.which = DOWN_KEY;
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.selectedItem = item;
			isolateScope.data = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			isolateScope.updateActiveItem(2);
			isolateScope.isScrolledIntoView = function() { return false; };
			spyOn(isolateScope, 'scroll');
			spyOn(isolateScope, 'updateActiveItem');

			// we pass an event object which then gets appended to the dummy object in the angular triggerHandlerEvent()
			directiveElement.triggerHandler(keyboardEvent);
			expect(isolateScope.updateActiveItem).not.toHaveBeenCalled();
			expect(isolateScope.scroll).not.toHaveBeenCalled();
		});

		it('should listen to the Up keydown event and update active item if the beginning of list items is not reached', function() {
			var item = {
					id: 2
				},
				isolateScope;

			keyboardEvent.which = UP_KEY;
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.selectedItem = item;
			isolateScope.updateActiveItem(1);
			isolateScope.data = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			spyOn(isolateScope, 'updateActiveItem');

			// we pass an event object which then gets appended to the dummy object in the angular triggerHandlerEvent()
			directiveElement.triggerHandler(keyboardEvent);
			expect(isolateScope.updateActiveItem).toHaveBeenCalled();
		});

		it('should listen to the Up keydown event and scroll the list to active item', function() {
			var item = {
					id: null
				},
				isolateScope;

			keyboardEvent.which = UP_KEY;
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.selectedItem = item;
			isolateScope.data = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			isolateScope.isScrolledIntoView = function() { return false; };
			spyOn(isolateScope, 'scroll');

			// we pass an event object which then gets appended to the dummy object in the angular triggerHandlerEvent()
			directiveElement.triggerHandler(keyboardEvent);
			expect(isolateScope.scroll).toHaveBeenCalled();
		});

	});
});
