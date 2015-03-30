/*global angular, expect, inject, spyOn*/
'use strict';

describe('multiselect module', function() {

	var scope,
		compileDirective,
		isolateScope,
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

	beforeEach(function() {
		module('templates');
		module('multiSelect');
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	beforeEach(function() {
		scope.model = {
			property: []
		};
		scope.property = 'property';
		scope.options = ['one', 'two'];
	});

	compileDirective = function(extraAttrs) {
		var attrs = extraAttrs || '',
			directiveHtml = '<om-multi-select om-id="multiselect" om-label="Multiselect" ' +
			'om-property="property" om-model="model" om-options="options" ' + attrs + '></om-multi-select>';

		inject(function($compile) {
			directiveElement = $compile(directiveHtml)(scope);

			scope.$digest();

			isolateScope = directiveElement.isolateScope();
		});
	};

	it('should set the input to contain the selected item from the suggestions', function() {
		compileDirective();

		expect(isolateScope.searchText).toEqual('');

		isolateScope.suggestions = ['one', 'two'];
		isolateScope.selectedIndex = 1;
		scope.$apply();

		expect(isolateScope.searchText).toEqual('two');
	});

	describe('by default', function() {

		it('should set suggestions to the passed in options', function() {
			compileDirective();

			expect(isolateScope.suggestions).toEqual(scope.options);
		});

		it('should set searchText to an empty string', function() {
			compileDirective();
			expect(isolateScope.searchText).toEqual('');
		});

		it('should set selectedIndex to -1', function() {
			compileDirective();
			expect(isolateScope.selectedIndex).toEqual(-1);
		});
	});

	describe('$scope.search', function() {

		it('should reset suggestions to the passed in options', function() {
			compileDirective();

			isolateScope.suggestions = [];
			isolateScope.search();

			expect(isolateScope.suggestions).toEqual(scope.options);
		});

		it('should add the search term text to the suggestions if the multiselect allows tags', function() {
			compileDirective('om-tags="true"');
			isolateScope.searchText = 'cat';

			isolateScope.search();

			expect(isolateScope.suggestions[0]).toEqual('cat');
		});

		it('should not add the search term text to the suggestions if the multiselect does not allow tags', function() {
			compileDirective();
			isolateScope.searchText = 'cat';

			isolateScope.search();

			expect(isolateScope.suggestions).not.toContain('cat');
		});

		it('should not add the search term text to the suggestions if the model for the input is not valued', function() {
			compileDirective('om-tags="true"');

			isolateScope.search();

			expect(isolateScope.suggestions).not.toContain('');
		});

		it('should only return suggestions that match the search term', function() {
			compileDirective();
			isolateScope.searchText = 'on';

			isolateScope.search();

			expect(isolateScope.suggestions[0]).toEqual('one');
			expect(isolateScope.suggestions).not.toContain('two');
		});

		it('should only return suggestions that have not already been selected', function() {
			compileDirective();
			isolateScope.searchText = 'one';

			scope.model[scope.property] = ['one'];
			isolateScope.search();

			expect(isolateScope.suggestions).not.toContain('one');
		});

		it('should set the selected index to -1', function() {
			compileDirective();

			isolateScope.search();

			expect(isolateScope.selectedIndex).toEqual(-1);
		});
	});

	describe('$scope.checkKeyDown', function() {
		var DOWN_KEY = 40,
			UP_KEY = 38,
			ENTER_KEY = 13,
			RANDOM_KEY = 1,
			keyEvent = {
				preventDefault: function() {}
			};

		it('should call search if the user presses the down arrow with nothing selected', function() {
			compileDirective();

			spyOn(isolateScope, 'search').and.callThrough();

			keyEvent.keyCode = DOWN_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.search).toHaveBeenCalled();
		});

		it('should increase the selectedIndex if the user presses down and is not at the last item in the list', function() {
			compileDirective();

			isolateScope.selectedIndex = 0;
			keyEvent.keyCode = DOWN_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.selectedIndex).toEqual(1);
		});

		it('should not increase the selectedIndex if the user presses down and is at the last item in the list', function() {
			compileDirective();

			isolateScope.selectedIndex = 1;
			keyEvent.keyCode = DOWN_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.selectedIndex).toEqual(1);
		});

		it('should decrease the selectedIndex if the user presses up and is not at the first item in the list', function() {
			compileDirective();

			isolateScope.selectedIndex = 1;
			keyEvent.keyCode = UP_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.selectedIndex).toEqual(0);
		});

		it('should not decrease the selectedIndex if the user presses up and is at the first item in the list', function() {
			compileDirective();

			isolateScope.selectedIndex = 0;
			keyEvent.keyCode = UP_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.selectedIndex).toEqual(0);
		});

		it('should call addToSelectedItems with the selectedIndex if the enter key is pressed', function() {
			compileDirective();
			spyOn(isolateScope, 'addToSelectedItems').and.callThrough();

			isolateScope.selectedIndex = 0;
			keyEvent.keyCode = ENTER_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.addToSelectedItems).toHaveBeenCalledWith(0);
		});

		it('should reset the selected index back to -1', function() {
			compileDirective();

			isolateScope.selectedIndex = 0;
			keyEvent.keyCode = ENTER_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.selectedIndex).toEqual(-1);
		});

		it('should not change the selectedIndex if a key other than up, down or enter is pressed', function() {
			compileDirective();

			isolateScope.selectedIndex = 0;
			keyEvent.keyCode = RANDOM_KEY;
			isolateScope.checkKeyDown(keyEvent);

			expect(isolateScope.selectedIndex).toEqual(0);
		});
	});

	describe('$scope.addToSelectedItems', function() {

		it('should allow the user to add text they have entered as a tag without explicitly selecting it', function() {
			compileDirective('om-tags="true"');

			isolateScope.searchText = 'hi';
			isolateScope.addToSelectedItems(-1);

			expect(scope.model[scope.property]).toContain('hi');
		});

		it('should not allow the user to add text they have entered as a tag if the multiselect does not allow tags', function() {
			compileDirective();

			isolateScope.searchText = 'hi';
			isolateScope.addToSelectedItems(-1);

			expect(scope.model[scope.property]).not.toContain('hi');
		});

		it('should add the item if it hasn\'t already been added to the list of selected items', function() {
			compileDirective();

			isolateScope.addToSelectedItems(0);

			expect(scope.model[scope.property]).toContain('one');
		});

		it('should not add the item again if it has already been added to the list of selected items', function() {
			compileDirective();

			scope.model[scope.property] = ['one'];
			isolateScope.addToSelectedItems(0);

			expect(scope.model[scope.property][0]).toEqual('one');
			expect(scope.model[scope.property].length).toEqual(1);
		});

		it('should not add an item if the index is not within the bounds of the suggestions array', function() {
			compileDirective();

			isolateScope.addToSelectedItems(-1);

			expect(scope.model[scope.property].length).toEqual(0);
		});

		it('should set the suggestions back to an empty array after adding an item', function() {
			compileDirective();

			isolateScope.addToSelectedItems(0);

			expect(isolateScope.suggestions).toEqual([]);
		});
	});

	describe('$scope.removeItem', function() {
		it('should remove an item from the selected items array at the provided index', function() {
			compileDirective();

			scope.model[scope.property] = ['one'];
			spyOn(scope.model[scope.property], 'splice').and.callThrough();

			isolateScope.removeItem(0);

			expect(scope.model[scope.property].splice).toHaveBeenCalledWith(0, 1);
			expect(scope.model[scope.property]).not.toContain('one');
		});
	});
});
