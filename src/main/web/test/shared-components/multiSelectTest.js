/*global angular, expect, inject, spyOn*/
'use strict';

describe('multiselect module', function() {

	var scope,
		compileDirective,
		isolateScope,
		directiveElement,
		mockTranslateFilter,

		stringDataService = {
			addToSelectedItems: function(/*scope*/) {
				return function() {};
			},
			formatForDisplay: function(value) {
				return value;
			},
			search: function(/*scope*/) {
				return function() {};
			}
		},
		objectDataService = {
			addToSelectedItems: function(/*scope*/) {
				return function() {};
			},
			formatForDisplay: function(value) {
				return value.name;
			},
			search: function(/*scope*/) {
				return function() {};
			}
		};

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
	});

	beforeEach(module('multiSelect', function($provide) {
		$provide.value('stringDataService', stringDataService);
		$provide.value('objectDataService', objectDataService);
	}));

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

	it('should not set the service if the type of the options cannot be determined', function() {
		scope.options = [];
		compileDirective();
		expect(isolateScope.service).toBeUndefined();
	});

	it('should set the service to the stringDataService when used with strings', function() {
		scope.options = ['one', 'two'];
		compileDirective();
		expect(isolateScope.service).toEqual(stringDataService);
	});

	it('should set the service to the objectDataService when used with objects', function() {
		scope.options = [{
			name: 'one'
		}, {
			name: 'two'
		}];
		compileDirective();
		expect(isolateScope.service).toEqual(objectDataService);
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

	describe('$scope.onClick', function() {

		it('should add to selected items', function() {
			compileDirective();

			spyOn(isolateScope, 'addToSelectedItems').and.callThrough();

			isolateScope.onClick(1);
			expect(isolateScope.addToSelectedItems).toHaveBeenCalledWith(1);
		});

		it('should reset the selected index back to -1', function() {
			compileDirective();
			isolateScope.onClick();
			expect(isolateScope.selectedIndex).toEqual(-1);
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

	describe('$scope.toggleSuggestions', function() {

		it('should call search to populate the suggestions if there are no suggestions and nothing is selected', function() {
			compileDirective();

			spyOn(isolateScope, 'search').and.callThrough();

			isolateScope.selectedIndex = -1;
			isolateScope.suggestions = [];
			isolateScope.toggleSuggestions();

			expect(isolateScope.search).toHaveBeenCalled();
		});

		it('should clear the suggestions to hide them if they are currently shown', function() {
			compileDirective();

			isolateScope.suggestions = ['one'];
			isolateScope.toggleSuggestions();

			expect(isolateScope.suggestions.length).toEqual(0);
		});
	});

});

describe('stringDataService', function() {
		var DEFAULT_OPTIONS = ['one', 'two'],

			scope,
			stringDataService,
			addToSelectedItems,
			formatForDisplay,
			search;

	beforeEach(function() {
		module('multiSelect');
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	beforeEach(function() {
		inject(function(_stringDataService_) {
			stringDataService = _stringDataService_;
		});

		addToSelectedItems = stringDataService.addToSelectedItems(scope);
		search = stringDataService.search(scope);

		formatForDisplay = stringDataService.formatForDisplay;
	});

	beforeEach(function() {
		scope.model = {
			property: []
		};
		scope.property = 'property';
	});

	describe('addToSelectedItems', function() {
		beforeEach(function() {
			scope.suggestions = DEFAULT_OPTIONS;
		});

		it('should allow the user to add text they have entered as a tag without explicitly selecting it', function() {
			scope.tags = true;
			scope.searchText = 'hi';
			addToSelectedItems(-1);

			expect(scope.model[scope.property]).toContain('hi');
		});

		it('should not allow the user to add text they have entered as a tag if the multiselect does not allow tags', function() {
			scope.searchText = 'hi';
			addToSelectedItems(-1);

			expect(scope.model[scope.property]).not.toContain('hi');
		});

		it('should add the item if it hasn\'t already been added to the list of selected items', function() {
			addToSelectedItems(0);
			expect(scope.model[scope.property]).toContain('one');
		});

		it('should not add the item again if it has already been added to the list of selected items', function() {
			scope.model[scope.property] = ['one'];
			addToSelectedItems(0);

			expect(scope.model[scope.property][0]).toEqual('one');
			expect(scope.model[scope.property].length).toEqual(1);
		});

		it('should not add an item if the index is not within the bounds of the suggestions array', function() {
			addToSelectedItems(-1);
			expect(scope.model[scope.property].length).toEqual(0);
		});

		it('should set the suggestions back to an empty array after adding an item', function() {
			addToSelectedItems(0);
			expect(scope.suggestions).toEqual([]);
		});
	});

	describe('formatForDisplay', function() {

		it('should return the passed in string with no additional formatting', function() {
			expect(formatForDisplay('hi')).toEqual('hi');
		});
	});

	describe('search', function() {

		beforeEach(function() {
			scope.options = DEFAULT_OPTIONS;
			scope.searchText = '';
		});

		it('should reset suggestions to the passed in options', function() {
			scope.suggestions = [];
			search();
			expect(scope.suggestions).toEqual(scope.options);
		});

		it('should add the search term text to the suggestions if the multiselect allows tags', function() {
			scope.tags = true;
			scope.searchText = 'cat';

			search();
			expect(scope.suggestions[0]).toEqual('cat');
		});

		it('should not add the search term text to the suggestions if the multiselect does not allow tags', function() {
			scope.searchText = 'cat';
			search();
			expect(scope.suggestions).not.toContain('cat');
		});

		it('should not add the search term text to the suggestions if the model for the input is not valued', function() {
			scope.tags = true;
			search();
			expect(scope.suggestions).not.toContain('');
		});

		it('should only return suggestions that match the search term', function() {
			scope.searchText = 'on';
			search();

			expect(scope.suggestions[0]).toEqual('one');
			expect(scope.suggestions).not.toContain('two');
		});

		it('should only return suggestions that have not already been selected', function() {
			scope.searchText = 'one';

			scope.model[scope.property] = ['one'];
			search();

			expect(scope.suggestions).not.toContain('one');
		});

		it('should set the selected index to -1', function() {
			search();
			expect(scope.selectedIndex).toEqual(-1);
		});
	});
});

describe('objectDataService', function() {
		var ONE = {name: 'one'},
			TWO = {name: 'two'},
			DEFAULT_OPTIONS = [ONE, TWO],

			scope,
			objectDataService,
			addToSelectedItems,
			formatForDisplay,
			search;

	beforeEach(function() {
		module('multiSelect');
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	beforeEach(function() {
		inject(function(_objectDataService_) {
			objectDataService = _objectDataService_;
		});

		addToSelectedItems = objectDataService.addToSelectedItems(scope);
		search = objectDataService.search(scope);

		formatForDisplay = objectDataService.formatForDisplay;
	});

	beforeEach(function() {
		scope.model = {
			property: []
		};
		scope.property = 'property';
	});

	describe('addToSelectedItems', function() {
		beforeEach(function() {
			scope.suggestions = DEFAULT_OPTIONS;
		});

		it('should add the item if it hasn\'t already been added to the list of selected items', function() {
			addToSelectedItems(0);
			expect(scope.model[scope.property]).toContain(ONE);
		});

		it('should not add the item again if it has already been added to the list of selected items', function() {
			scope.model[scope.property] = [ONE];
			addToSelectedItems(0);

			expect(scope.model[scope.property][0]).toEqual(ONE);
			expect(scope.model[scope.property].length).toEqual(1);
		});

		it('should not add an item if the index is not within the bounds of the suggestions array', function() {
			addToSelectedItems(-1);
			expect(scope.model[scope.property].length).toEqual(0);
		});

		it('should set the suggestions back to an empty array after adding an item', function() {
			addToSelectedItems(0);
			expect(scope.suggestions).toEqual([]);
		});
	});

	describe('formatForDisplay', function() {

		it('should return the name of the passed in object', function() {
			expect(formatForDisplay(ONE)).toEqual('one');
		});
	});

	describe('search', function() {

		beforeEach(function() {
			scope.options = DEFAULT_OPTIONS;
			scope.searchText = '';
		});

		it('should reset suggestions to the passed in options', function() {
			scope.suggestions = [];
			search();
			expect(scope.suggestions).toEqual(scope.options);
		});

		it('should only return suggestions that match the search term', function() {
			scope.searchText = 'on';
			search();

			expect(scope.suggestions[0]).toEqual(ONE);
			expect(scope.suggestions).not.toContain(TWO);
		});

		it('should only return suggestions that have not already been selected', function() {
			scope.searchText = 'one';

			scope.model[scope.property] = [ONE];
			search();

			expect(scope.suggestions).not.toContain(ONE);
		});

		it('should set the selected index to -1', function() {
			search();
			expect(scope.selectedIndex).toEqual(-1);
		});
	});
});
