/*global angular, inject, expect, spyOn*/
'use strict';

describe('List module', function() {
	var LIST_ITEM_CAT = {
			id: 'cat',
			name: 'Cat',
			description: 'A fluffy animal that likes to sleep.',
			'action-favourite': {
				iconValue: 'star',
				iconFunction: function() {}
			}
		},
		LIST_ITEM_DOG = {
			id: 'dog',
			name: 'Dog',
			description: 'A playful animal that likes walks',
			'action-favourite': {
				iconValue: 'star-empty',
				iconFunction: function() {}
			}
		},

		fakeScrollElement = {
			scrollTop: 100 // Set the number of pixels scrolled
		},

		fakeEvent = {
			preventDefault: function() {},
			stopPropagation: function() {}
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

	it('should filter the items if the filter value changes', function() {
		var isolateScope;

		scope.filterText = '';
		scope.selectedItem = {};

		compileDirective('om-item-filter="filterText" om-selected-item="selectedItem"');
		isolateScope = directiveElement.isolateScope();

		spyOn(isolateScope, 'filterItems');

		scope.filterText = 'm';
		isolateScope.$apply();

		expect(isolateScope.filterItems).toHaveBeenCalledWith('m');
	});

	describe('scope.isString', function() {
		var isolateScope;

		beforeEach(function() {
			compileDirective();
			isolateScope = directiveElement.isolateScope();
		});

		it('should return true if the type of the passed in object is a string', function() {
			expect(isolateScope.isString('hey')).toBe(true);
		});

		it('should return false if the type of the passed in object is not a string', function() {
			expect(isolateScope.isString(3)).toBe(false);
		});
	});

	describe('scope.isAction', function() {
		var isolateScope;

		beforeEach(function() {
			compileDirective();
			isolateScope = directiveElement.isolateScope();
		});

		it('should return a truthy value if the passed in item is an object and has an iconValue property', function() {
			expect(isolateScope.isAction({iconValue: 'star'})).toBeTruthy();
		});

		it('should return a falsy value if the passed in item is not an object', function() {
			expect(isolateScope.isAction(3)).toBeFalsy();
		});

		it('should return a falsy value if the passed in item is an object and does not have an iconValue property', function() {
			expect(isolateScope.isAction({})).toBeFalsy();
		});
	});

	describe('scope.isNotActionHeader', function() {
		var isolateScope;

		beforeEach(function() {
			compileDirective();
			isolateScope = directiveElement.isolateScope();
		});

		it('should return a truthy value if the passed in item is a string that does not contain "action-"', function() {
			expect(isolateScope.isNotActionHeader('name')).toBeTruthy();
		});

		it('should return a falsy value if the passed in item is not a string', function() {
			expect(isolateScope.isAction(3)).toBeFalsy();
		});

		it('should return a falsy value if the passed in item is a string and contains "action-"', function() {
			expect(isolateScope.isAction('action-favourite')).toBeFalsy();
		});
	});

	describe('scope.isItemFilteredOut', function() {
		var isolateScope;

		beforeEach(function() {
			scope.propsToFilter = ['name', 'description'];
			compileDirective('om-properties-to-filter="propsToFilter"');
			isolateScope = directiveElement.isolateScope();
		});

		it('should return true if the filter text is not included in the item text', function() {
			expect(isolateScope.isItemFilteredOut({name: 'measurement'}, 'z')).toBe(true);
		});

		it('should return false if the filter text is included in the name property of the item text', function() {
			expect(isolateScope.isItemFilteredOut({name: 'measurement'}, 'm')).toBe(false);
		});

		it('should return false if the filter text is included in the description property of the item text', function() {
			expect(isolateScope.isItemFilteredOut({description: 'measurement'}, 'm')).toBe(false);
		});

		it('should return true if the filter text is included in a property other than name and description of the item text', function() {
			expect(isolateScope.isItemFilteredOut({id: 'measurement'}, 'm')).toBe(true);
		});
	});

	describe('scope.filterItems', function() {
		var isolateScope;

		beforeEach(function() {
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			scope.propsToFilter = ['name', 'description'];
			compileDirective('om-properties-to-filter="propsToFilter"');
			isolateScope = directiveElement.isolateScope();
		});

		it('should set whether each item is hidden or not in the list item metadata', function() {
			isolateScope.filterItems('dog');

			expect(isolateScope.listItemMetadata[LIST_ITEM_CAT.id].isHidden).toBe(true);
			expect(isolateScope.listItemMetadata[LIST_ITEM_DOG.id].isHidden).toBe(false);
		});

		it('should build up a list of shown items', function() {
			isolateScope.filterItems('dog');

			expect(isolateScope.shownItems).toContain(LIST_ITEM_DOG);
			expect(isolateScope.shownItems).not.toContain(LIST_ITEM_CAT);
		});

		it('should set the number of items shown', function() {
			isolateScope.filterItems('dog');

			expect(isolateScope.numberOfItemsShown).toBe(1);
			expect(isolateScope.isAnyItemShown).toBe(true);
		});
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
			scope.testData = [LIST_ITEM_CAT];
			scope.selectedItem = {
				id: null
			};
			compileDirective('om-on-click="clickFn()" om-selected-item="selectedItem"');

			var tableHtml = directiveElement.find('table')[0];
			spyOn(tableHtml, 'focus');

			directiveElement.isolateScope().selectItem(1, LIST_ITEM_CAT.id);

			scope.$broadcast('panelClose');
			expect(tableHtml.focus).toHaveBeenCalled();
		});
	});

	describe('scope.toggleFavourites', function() {
		var isolateScope;

		beforeEach(function() {
			scope.testHeaders = ['name', 'description'];
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];

			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.selectedItem = { id: null };
		});

		it('should stop propagation of the event to <tr>', function() {
			spyOn(fakeEvent, 'stopPropagation');

			isolateScope.toggleFavourites(1, 1, fakeEvent, LIST_ITEM_CAT['action-favourite']);
			expect(fakeEvent.stopPropagation).toHaveBeenCalled();
		});

		it('should set the active item to the passed in index', function() {
			isolateScope.toggleFavourites(1, 1, fakeEvent, LIST_ITEM_CAT['action-favourite']);
			expect(isolateScope.activeItemIndex).toBe(1);
		});

		it('call icon function', function() {
			spyOn(LIST_ITEM_CAT['action-favourite'], 'iconFunction');

			directiveElement.isolateScope().toggleFavourites(1, 1, fakeEvent, LIST_ITEM_CAT['action-favourite']);
			expect(LIST_ITEM_CAT['action-favourite'].iconFunction).toHaveBeenCalled();
		});

	});

	describe('scope.scroll', function() {
		var isolateScope;

		beforeEach(function() {
			compileDirective();
			isolateScope = directiveElement.isolateScope();
		});

		it('should set scroll top of the scroll element', function() {
			isolateScope.scroll(fakeScrollElement, 20, 5, 15, 10);
			expect(fakeScrollElement.scrollTop).toEqual(-965);
		});

		it('should set scroll top of the scroll element when current time is less than duration', function() {
			isolateScope.scroll(fakeScrollElement, 20, 5, 15, -20);
			expect(fakeScrollElement.scrollTop).toEqual(15);
		});
	});

	describe('scope.isScrolledIntoView', function() {

		it('should not break if a falsy value is passed in', function() {
			compileDirective();
			expect(directiveElement.isolateScope().isScrolledIntoView(null)).toEqual(false);
		});
	});

	describe('scope.getActiveItemIndex', function() {
		var isolateScope;

		beforeEach(function() {
			compileDirective();
			isolateScope = directiveElement.isolateScope();
			isolateScope.shownItems = [LIST_ITEM_DOG];
			isolateScope.listItemMetadata = {
				cat: {
					index: 0
				},
				dog: {
					index: 1
				}
			};
		});

		it('should return the actual index in the table of the visible item', function() {
			expect(isolateScope.getActiveItemIndex(0)).toBe(1);
		});

		it('should return null if the item is not in the list', function() {
			expect(isolateScope.getActiveItemIndex(3)).toBe(null);
		});
	});

	describe('scope.checkKeyDown', function() {
		var DOWN_KEY = 40,
			UP_KEY = 38,
			ENTER_KEY = 13,
			RANDOM_KEY = 1;

		var isolateScope;

		beforeEach(function() {
			scope.testData = [LIST_ITEM_CAT, LIST_ITEM_DOG];
			compileDirective();
			isolateScope = directiveElement.isolateScope();
		});

		it('should update visible item index if the end of list is not reached when down is pressed', function() {
			spyOn(isolateScope, 'getActiveItemIndex');

			isolateScope.visibleItemIndex = 0;
			fakeEvent.which = DOWN_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.visibleItemIndex).toBe(1);
		});

		it('should update active item if the end of list is not reached when down is pressed', function() {
			spyOn(isolateScope, 'getActiveItemIndex').and.returnValue(1);

			isolateScope.visibleItemIndex = 0;
			fakeEvent.which = DOWN_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.activeItemIndex).toBe(1);
		});

		it('should scroll the list to the active item when the down key is pressed', function() {
			spyOn(isolateScope, 'isScrolledIntoView').and.returnValue(false);
			spyOn(isolateScope, 'scroll');

			fakeEvent.which = DOWN_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.scroll).toHaveBeenCalled();
		});

		it('should should not change the active item if the end of list items is reached', function() {
			spyOn(isolateScope, 'getActiveItemIndex');

			isolateScope.visibleItemIndex = 1;
			isolateScope.activeItemIndex = -1;
			fakeEvent.which = DOWN_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.getActiveItemIndex).not.toHaveBeenCalled();
			expect(isolateScope.activeItemIndex).toBe(-1);
			expect(isolateScope.visibleItemIndex).toBe(1);
		});

		it('should update visible item index if the start of list is not reached when up is pressed', function() {
			spyOn(isolateScope, 'getActiveItemIndex');

			isolateScope.visibleItemIndex = 1;
			fakeEvent.which = UP_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.visibleItemIndex).toBe(0);
		});

		it('should update active item if the start of list is not reached when up is pressed', function() {
			spyOn(isolateScope, 'getActiveItemIndex').and.returnValue(0);

			isolateScope.visibleItemIndex = 1;
			fakeEvent.which = UP_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.activeItemIndex).toBe(0);
		});

		it('should scroll the list to the active item when the up key is pressed', function() {
			spyOn(isolateScope, 'isScrolledIntoView').and.returnValue(false);
			spyOn(isolateScope, 'scroll');

			fakeEvent.which = UP_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.scroll).toHaveBeenCalled();
		});

		it('should should not change the active item if the start of list items is reached', function() {
			spyOn(isolateScope, 'getActiveItemIndex');

			isolateScope.visibleItemIndex = 0;
			isolateScope.activeItemIndex = -1;
			fakeEvent.which = UP_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.getActiveItemIndex).not.toHaveBeenCalled();
			expect(isolateScope.activeItemIndex).toBe(-1);
			expect(isolateScope.visibleItemIndex).toBe(0);
		});

		it('should select an item when enter is pressed', function() {
			spyOn(isolateScope, 'selectItem');

			fakeEvent.which = ENTER_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.selectItem).toHaveBeenCalled();
		});

		it('should not change the index or select an item if a key other than up, down or enter is pressed', function() {
			isolateScope.visibleItemIndex = 0;
			isolateScope.activeItemIndex = 0;

			spyOn(isolateScope, 'selectItem');

			fakeEvent.which = RANDOM_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.selectItem).not.toHaveBeenCalled();
			expect(isolateScope.visibleItemIndex).toBe(0);
			expect(isolateScope.activeItemIndex).toBe(0);
		});
	});
});
