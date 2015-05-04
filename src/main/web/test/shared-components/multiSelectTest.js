/*global angular, expect, inject, spyOn*/
'use strict';

describe('multiselect module', function() {

	var ONE = {name: 'one'},
		TWO = {name: 'two'},
		ONE_TWO = [{name: 'one'}, {name: 'two'}],

		fakeEvent = {
			preventDefault: function() {},
			stopPropagation: function() {}
		},

		scope,
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
		scope.options = [ONE, TWO];

		compileDirective();
	});

	compileDirective = function() {
		var directiveHtml = '<om-multi-select om-name="multiselect" om-property="property" ng-model="model" om-options="options">' +
			'</om-multi-select>';

		inject(function($compile) {
			directiveElement = $compile(directiveHtml)(scope);
			scope.$digest();
			isolateScope = directiveElement.isolateScope();
		});
	};

	it('should set the input to contain the selected item from the suggestions', function() {
		isolateScope.selectedIndex = 1;
		scope.$apply();

		expect(isolateScope.searchText).toEqual('two');
	});

	describe('by default', function() {

		it('should set suggestions to the passed in options', function() {
			// Need to use angular.equals here because of the $$hashKey property that angular adds upon copy
			expect(angular.equals(isolateScope.suggestions, scope.options)).toBe(true);
		});

		it('should set searchText to an empty string', function() {
			expect(isolateScope.searchText).toEqual('');
		});

		it('should set selectedIndex to -1', function() {
			expect(isolateScope.selectedIndex).toEqual(-1);
		});
	});

	describe('multiselect validation', function() {

		function compileForm() {
			inject(function($compile) {
				directiveElement = $compile(
					'<form name="testForm" novalidate>' +
						'<om-multi-select name="omMultiSelect" om-name="multiselect" ' +
							'om-property="property" ng-model="model" om-options="options"></om-multi-select>' +
					'</form>')(scope);
			});

			scope.$digest();
		}

		it('should set the validity to be invalid when there is nothing selected', function() {
			compileForm();

			expect(scope.testForm.omMultiSelect.$error).toEqual({emptyValue: true});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the validity to be valid when there is something selected', function() {
			scope.model.property.push(ONE);
			compileForm();

			expect(scope.testForm.omMultiSelect.$error).toEqual({});
			expect(scope.testForm.$valid).toBe(true);
		});
	});

	describe('$scope.checkKeyDown', function() {

		var DOWN_KEY = 40,
			UP_KEY = 38,
			ENTER_KEY = 13,
			ESCAPE_KEY = 27,
			TAB_KEY = 9,
			RANDOM_KEY = 1;

		it('should call search if the user presses the down arrow with nothing selected', function() {
			spyOn(isolateScope, 'search');

			fakeEvent.keyCode = DOWN_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.search).toHaveBeenCalled();
		});

		it('should increase the selectedIndex if the user presses down and is not at the last item in the list', function() {
			isolateScope.selectedIndex = 0;

			fakeEvent.keyCode = DOWN_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.selectedIndex).toEqual(1);
		});

		it('should not increase the selectedIndex if the user presses down and is at the last item in the list', function() {
			isolateScope.selectedIndex = 1;

			fakeEvent.keyCode = DOWN_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.selectedIndex).toEqual(1);
		});

		it('should decrease the selectedIndex if the user presses up and is not at the first item in the list', function() {
			isolateScope.selectedIndex = 1;

			fakeEvent.keyCode = UP_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.selectedIndex).toEqual(0);
		});

		it('should not decrease the selectedIndex if the user presses up and is at the first item in the list', function() {
			isolateScope.selectedIndex = 0;

			fakeEvent.keyCode = UP_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.selectedIndex).toEqual(0);
		});

		it('should call addToSelectedItems with the selectedIndex if the enter key is pressed', function() {
			spyOn(isolateScope, 'addToSelectedItems');
			isolateScope.selectedIndex = 0;

			fakeEvent.keyCode = ENTER_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.addToSelectedItems).toHaveBeenCalledWith(0);
		});

		it('should hide the suggestions list if the item was added when the enter key is pressed', function() {
			spyOn(isolateScope, 'addToSelectedItems').and.returnValue(true);
			spyOn(isolateScope, 'hideSuggestions');

			fakeEvent.keyCode = ENTER_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.hideSuggestions).toHaveBeenCalled();
		});

		it('should call hideSuggestions if the escape key is pressed', function() {
			spyOn(isolateScope, 'hideSuggestions');

			fakeEvent.keyCode = ESCAPE_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.hideSuggestions).toHaveBeenCalled();
		});

		it('should call hideSuggestions if the tab key is pressed', function() {
			spyOn(isolateScope, 'hideSuggestions');

			fakeEvent.keyCode = TAB_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.hideSuggestions).toHaveBeenCalled();
		});

		it('should not change the selectedIndex if a key other than up, down, enter or escape is pressed', function() {
			isolateScope.selectedIndex = 0;

			fakeEvent.keyCode = RANDOM_KEY;
			isolateScope.checkKeyDown(fakeEvent);

			expect(isolateScope.selectedIndex).toEqual(0);
		});
	});

	describe('$scope.onClick', function() {

		it('should add to selected items', function() {
			spyOn(isolateScope, 'addToSelectedItems');

			isolateScope.onClick(1);
			expect(isolateScope.addToSelectedItems).toHaveBeenCalledWith(1);
		});

		it('should hide the suggestions', function() {
			spyOn(isolateScope, 'hideSuggestions');

			isolateScope.onClick(1);
			expect(isolateScope.hideSuggestions).toHaveBeenCalled();
		});
	});

	describe('$scope.removeItem', function() {

		it('should remove an item from the selected items array at the provided index', function() {
			scope.model[scope.property] = ['one'];
			spyOn(scope.model[scope.property], 'splice').and.callThrough();

			isolateScope.removeItem(fakeEvent, 0);

			expect(scope.model[scope.property].splice).toHaveBeenCalledWith(0, 1);
			expect(scope.model[scope.property]).not.toContain('one');
		});
	});

	describe('$scope.toggleSuggestions', function() {

		it('should show suggestions if there are no suggestions and nothing is selected', function() {
			spyOn(isolateScope, 'showSuggestions');

			isolateScope.selectedIndex = -1;
			isolateScope.suggestions = [];
			isolateScope.toggleSuggestions();

			expect(isolateScope.showSuggestions).toHaveBeenCalled();
		});

		it('should hide the suggestions if they are currently shown', function() {
			spyOn(isolateScope, 'hideSuggestions');

			isolateScope.suggestions = ['one'];
			isolateScope.toggleSuggestions();

			expect(isolateScope.hideSuggestions).toHaveBeenCalled();
		});
	});

	describe('$scope.addToSelectedItems', function() {

		it('should add the item if it hasn\'t already been added to the list of selected items', function() {
			isolateScope.addToSelectedItems(0);
			// Need to use angular.equals here because of the $$hashKey property that angular adds upon copy
			expect(angular.equals(scope.model[scope.property][0], ONE)).toBe(true);
		});

		it('should not add an item if the index is not within the bounds of the suggestions array', function() {
			isolateScope.addToSelectedItems(-1);
			expect(scope.model[scope.property].length).toEqual(0);
		});

	});

	describe('$scope.formatListForDisplay', function() {

		it('should return string separated names of the passed in array of objects', function() {
			expect(isolateScope.formatListForDisplay(ONE_TWO)).toEqual('one, two');
		});

		it('should handle falsy passed in object', function() {
			expect(isolateScope.formatListForDisplay(null)).toEqual('');
		});
	});

	describe('$scope.search', function() {

		it('should reset suggestions to the passed in options', function() {
			isolateScope.suggestions = [];
			isolateScope.search();
			// Need to use angular.equals here because of the $$hashKey property that angular adds upon copy
			expect(angular.equals(isolateScope.suggestions, scope.options)).toBe(true);
		});

		it('should only return suggestions that match the search term', function() {
			isolateScope.searchText = 'on';
			isolateScope.search();

			// Need to use angular.equals here because of the $$hashKey property that angular adds upon copy
			expect(angular.equals(isolateScope.suggestions[0], ONE)).toBe(true);
			expect(isolateScope.suggestions.length).toBe(1);
		});

		it('should only return suggestions that have not already been selected', function() {
			isolateScope.searchText = 'one';

			scope.model[scope.property] = [ONE];
			isolateScope.search();

			expect(isolateScope.suggestions).not.toContain(ONE);
		});

		it('should set the selected index to -1', function() {
			isolateScope.search();
			expect(isolateScope.selectedIndex).toEqual(-1);
		});
	});
});
