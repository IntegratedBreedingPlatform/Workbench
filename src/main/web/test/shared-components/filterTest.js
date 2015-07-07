/*global angular, expect, inject, spyOn*/
'use strict';

describe('Filter Module', function() {
	var scope,
		compileDirective,
		isolateScope,
		directiveElement,
		mockTranslateFilter,
		variableTypesService = {},
		scaleTypesService = {},
		q,
		deferredGetTypes,
		deferredGetNonSystemDataTypes,

		panelService = {
			showPanel: function() {},
			getCurrentPanel: function() {}
		},

		serviceUtilities = {
			formatErrorsForDisplay: function() { return {}; }
		},

		fakeEvent = {
			preventDefault: function() {},
			stopPropagation: function() {}
		},

		variableTypes = [{
			id: 1,
			name: 'Analysis',
			description: 'Variable to be used only in analysis (for example derived variables).'
		}, {
			id: 6,
			name: 'Environment Detail',
			description: 'Administrative details to be tracked per environment.'
		}],

		CATEGORICAL_TYPE = {
			id: 1,
			name: 'Categorical'
		},
		TODAY;

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
		module('filter');
	});

	beforeEach(module('filter', function($provide) {
		// Provide mocks for the directive controller
		$provide.value('variableTypesService', variableTypesService);
		$provide.value('panelService', panelService);
		$provide.value('serviceUtilities', serviceUtilities);
		$provide.value('dataTypesService', scaleTypesService);
	}));

	beforeEach(inject(function($rootScope, $q) {
		q = $q;
		scope = $rootScope;

		variableTypesService.getTypes = function() {
			deferredGetTypes = q.defer();
			return deferredGetTypes.promise;
		};

		scaleTypesService.getNonSystemDataTypes = function() {
			deferredGetNonSystemDataTypes = q.defer();
			return deferredGetNonSystemDataTypes.promise;
		};

		TODAY = new Date();
		TODAY.setHours(0, 0, 0, 0);

		spyOn(panelService, 'showPanel').and.callThrough();
		spyOn(serviceUtilities, 'formatErrorsForDisplay').and.callThrough();
	}));

	describe('omFilter', function() {

		compileDirective = function() {
			inject(function($compile) {
				directiveElement = $compile('<om-filter om-filter-options="filterOptions"></om-filter>')(scope);

				scope.$digest();

				isolateScope = directiveElement.isolateScope();
			});
		};

		beforeEach(function() {
			compileDirective();
			isolateScope.filterOptions = {};
		});

		it('should set defaults for missing attributes', function() {
			expect(isolateScope.smallPanelName).toBe('filters');
		});

		it('should set the variable types on the scope', function() {
			deferredGetTypes.resolve(variableTypes);
			scope.$apply();
			expect(isolateScope.data.types).toEqual(variableTypes);
		});

		it('should display errors if variable types were not retrieved successfully', function() {
			deferredGetTypes.reject();
			scope.$apply();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
			expect(isolateScope.someListsNotLoaded).toBe(true);
		});

		it('should set the scale data types on the scope', function() {
			//List with the value for 'ALL'
			var dataTypesList = [{id: 0, name:'...'}].concat(CATEGORICAL_TYPE);
			deferredGetNonSystemDataTypes.resolve(CATEGORICAL_TYPE);
			scope.$apply();
			expect(isolateScope.data.scaleTypes).toEqual(dataTypesList);
		});

		it('should display errors if scale data types were not retrieved successfully', function() {
			deferredGetNonSystemDataTypes.reject();
			scope.$apply();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
			expect(isolateScope.someListsNotLoaded).toBe(true);
		});

		describe('scope.addNewFilter', function() {
			it('should open the panel', function() {
				isolateScope.addNewFilter();
				expect(panelService.showPanel).toHaveBeenCalledWith(isolateScope.smallPanelName);
			});
		});

		describe('scope.today', function() {
			it('should set filter options dates to today\'s date', function() {
				isolateScope.today();
				isolateScope.filterOptions.dateCreatedFrom.setHours(0, 0, 0, 0);
				isolateScope.filterOptions.dateCreatedTo.setHours(0, 0, 0, 0);
				expect(isolateScope.filterOptions.dateCreatedFrom).toEqual(TODAY);
				expect(isolateScope.filterOptions.dateCreatedTo).toEqual(TODAY);
			});
		});

		describe('scope.clear', function() {
			it('should nullify filter options dates', function() {
				isolateScope.clear();
				expect(isolateScope.filterOptions.dateCreatedFrom).toBeNull();
				expect(isolateScope.filterOptions.dateCreatedTo).toBeNull();
			});
		});

		describe('scope.open1', function() {
			it('should set 1st calendar is opened property to true', function() {
				isolateScope.open1(fakeEvent);
				expect(isolateScope.data.calendarOpened1).toBe(true);
			});
		});

		describe('scope.open2', function() {
			it('should set 2st calendar is opened property to true', function() {
				isolateScope.open2(fakeEvent);
				expect(isolateScope.data.calendarOpened2).toBe(true);
			});
		});

		describe('$scope.isFilterActive', function() {

			it('should return true if variable types filter option is set', function() {
				isolateScope.filterOptions.variableTypes = variableTypes;
				expect(isolateScope.isFilterActive()).toBe(true);
			});

			it('should return true if scale data types filter option is set', function() {
				isolateScope.filterOptions.scaleType = CATEGORICAL_TYPE;
				expect(isolateScope.isFilterActive()).toBe(true);
			});

			it('should return true if date created from filter option is set', function() {
				isolateScope.filterOptions.dateCreatedFrom = new Date();
				expect(isolateScope.isFilterActive()).toBe(true);
			});

			it('should return true if date created to filter option is set', function() {
				isolateScope.filterOptions.dateCreatedTo = new Date();
				expect(isolateScope.isFilterActive()).toBe(true);
			});
		});
	});
});
