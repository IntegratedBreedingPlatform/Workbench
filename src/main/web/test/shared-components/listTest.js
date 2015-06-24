/*global angular, inject, expect, spyOn*/
'use strict';

describe('List module', function() {
	var scope,
		isolateScope,
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

	describe('omList directive', function() {
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

		fakeEvent = {
			preventDefault: function() {},
			stopPropagation: function() {}
		},
		directiveElement;

		function compileDirective(extraAttrs) {
			var attrs = extraAttrs || '';

			inject(function($compile) {
				directiveElement = $compile('<om-list om-data="testData" om-col-headers="testHeaders" ' + attrs + '></om-list>')(scope);
			});

			scope.$digest();
		}

		beforeEach(function() {
			/*compileDirective();
			isolateScope = directiveElement.isolateScope();*/
		});

		it('should set the maximum rows per page to 50 if the list can be paginated', function() {
			compileDirective('om-pagination="true"');
			isolateScope = directiveElement.isolateScope();

			expect(isolateScope.rowsPerPage).toBe(50);
		});

		it('should set the rows per page to -1 if the list cannot be paginated', function() {
			compileDirective('om-pagination="false"');
			isolateScope = directiveElement.isolateScope();

			expect(isolateScope.rowsPerPage).toBe(-1);
		});

		it('should change the active item id if the selectedItemService refers to this list', function() {
			var selectedItemService;

			inject(function(_selectedItemService_) {
				selectedItemService = _selectedItemService_;
			});

			compileDirective('om-list-name="list"');
			isolateScope = directiveElement.isolateScope();

			selectedItemService.setSelectedItem('1', 'list');

			scope.$apply();

			expect(isolateScope.activeItemId).toEqual('1');
		});

		it('should not change the active item id if the selectedItemService does not refer to this list', function() {
			var selectedItemService;

			inject(function(_selectedItemService_) {
				selectedItemService = _selectedItemService_;
			});

			compileDirective('om-list-name="list"');
			isolateScope = directiveElement.isolateScope();

			selectedItemService.setSelectedItem('1', 'anotherList');

			scope.$apply();

			expect(isolateScope.activeItemId).toEqual(null);
		});

		describe('scope.isString', function() {

			it('should return true if the type of the passed in object is a string', function() {
				expect(isolateScope.isString('hey')).toBe(true);
			});

			it('should return false if the type of the passed in object is not a string', function() {
				expect(isolateScope.isString(3)).toBe(false);
			});
		});

		describe('scope.isAction', function() {

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

		describe('scope.filterByProperties', function() {

			beforeEach(function() {
				compileDirective();
				isolateScope = directiveElement.isolateScope();
			});

			it('should return true if there is no provided filter text', function() {
				expect(isolateScope.filterByProperties()).toBe(true);
			});

			it('should return true if the search text is contained within any of the filtered properties of the item', function() {
				var item = {
					name: 'cat',
					description: 'animal'
				};
				isolateScope.propertiesToFilter = ['name', 'description'];
				isolateScope.itemFilter = 'm';

				expect(isolateScope.filterByProperties(item)).toBe(true);
			});

			it('should return true if the value matches the search text but is a different case', function() {
				var item = {
					name: 'cat'
				};
				isolateScope.propertiesToFilter = ['name'];
				isolateScope.itemFilter = 'CAT';

				expect(isolateScope.filterByProperties(item)).toBe(true);
			});

			it('should return false if the search text is not contained within any of the filtered properties of the item', function() {
				var item = {
					name: 'cat',
					description: 'animal'
				};
				isolateScope.propertiesToFilter = ['name', 'description'];
				isolateScope.itemFilter = 'dog';

				expect(isolateScope.filterByProperties(item)).toBe(false);
			});
		});

		describe('scope.filterByOptions', function() {

			beforeEach(function() {
				compileDirective();
				isolateScope = directiveElement.isolateScope();
			});

			it('should return function returning true if there is no provided options filter function', function() {
				expect(isolateScope.filterByOptions()()).toBe(true);
			});

			it('should return options filter function if it is provided', function() {
				scope.optionsFilter = function() {
					return 'test';
				};
				compileDirective('om-options-filter="optionsFilter"');
				isolateScope = directiveElement.isolateScope();
				expect(isolateScope.filterByOptions()).toBe('test');
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
		});

		describe('scope.toggleFavourites', function() {

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

			it('call icon function', function() {
				spyOn(LIST_ITEM_CAT['action-favourite'], 'iconFunction');

				directiveElement.isolateScope().toggleFavourites(1, 1, fakeEvent, LIST_ITEM_CAT['action-favourite']);
				expect(LIST_ITEM_CAT['action-favourite'].iconFunction).toHaveBeenCalled();
			});

		});
	});

	describe('selectedItemService', function() {
		var selectedItemService;

		beforeEach(inject(function(_selectedItemService_) {
			selectedItemService = _selectedItemService_;
		}));

		describe('getSelectedItem', function() {

			it('should return an object containing the selected item id and list that the selected item is in', function() {
				var item = selectedItemService.getSelectedItem();
				expect(item).toEqual({id: null, list: null});
			});
		});

		describe('setSelectedItem', function() {

			it('should store the passed in item id and list', function() {
				var item;
				selectedItemService.setSelectedItem('1', 'list');
				item = selectedItemService.getSelectedItem();
				expect(item).toEqual({id: '1', list: 'list'});
			});
		});
	});

});
