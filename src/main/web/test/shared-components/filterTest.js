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
		deferredGetDataTypes,

		panelService = {
			showPanel: function() {},
			getCurrentPanel: function() {}
		},

		serviceUtilities = {
			formatErrorsForDisplay: function() { return {}; }
		},

		variableTypes = [{
			id: 1,
			name: 'Analysis',
			description: 'Variable to be used only in analysis (for example derived variables).'
		},
		{
			id: 6,
			name: 'Environment Detail',
			description: 'Administrative details to be tracked per environment.'
		}],

		CATEGORICAL_TYPE = {
			id: 1,
			name: 'Categorical'
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

		scaleTypesService.getDataTypes = function() {
			deferredGetDataTypes = q.defer();
			return deferredGetDataTypes.promise;
		};

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
			deferredGetDataTypes.resolve(CATEGORICAL_TYPE);
			scope.$apply();
			expect(isolateScope.data.scaleTypes).toEqual(dataTypesList);
		});

		it('should display errors if scale data types were not retrieved successfully', function() {
			deferredGetDataTypes.reject();
			scope.$apply();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
			expect(isolateScope.someListsNotLoaded).toBe(true);
		});

		describe('scope.addNewFilter', function() {

			it('should open the panel', function() {
				compileDirective();
				isolateScope.addNewFilter();
				expect(panelService.showPanel).toHaveBeenCalledWith(isolateScope.smallPanelName);
			});
		});
	});
});
