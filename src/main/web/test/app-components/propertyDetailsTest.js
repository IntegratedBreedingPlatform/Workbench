/*global angular, expect, inject, spyOn*/
'use strict';

describe('Property details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},
		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},
		BLAST = {
			id: 1,
			name: 'Blast'
		},
		SITE_CONDITION = 'Site Condition',
		propertiesService = {},
		scope,
		q,
		directiveElement,
		deferredGetClasses,
		deferredUpdateProperty,
		mockTranslateFilter;

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};
	});

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-property-details></om-property-details>')(scope);
		});
		scope.$digest();
	}

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));

	beforeEach(module('propertyDetails', function($provide) {
		// Provide mocks for the directive controller
		$provide.value('propertiesService', propertiesService);
		$provide.value('serviceUtilities', serviceUtilities);
	}));

	beforeEach(inject(function($rootScope, $q) {
		q = $q;
		scope = $rootScope;

		propertiesService.getClasses = function() {
			deferredGetClasses = q.defer();
			return deferredGetClasses.promise;
		};

		propertiesService.updateProperty = function() {
			deferredUpdateProperty = q.defer();
			return deferredUpdateProperty.promise;
		};

		spyOn(propertiesService, 'getClasses').and.callThrough();
		spyOn(propertiesService, 'updateProperty').and.callThrough();
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');

		compileDirective();
	}));

	describe('by default', function() {

		it('should set editing to false', function() {
			expect(scope.editing).toBe(false);
		});

		it('should set data to have an empty array of classes', function() {
			expect(scope.data).toEqual({classes: []});
		});

		it('should set the model to be the selected property if the selected property changes', function() {
			scope.selectedProperty = BLAST;
			scope.$apply();
			expect(scope.model).toEqual(BLAST);
		});

		it('should set the property id to be the id of the selected item if the selected property changes', function() {
			scope.selectedItem = BLAST;
			scope.$apply();
			expect(scope.propertyId).toEqual(BLAST.id);
		});

		it('should set the property id to be null if the selected property changes and has no id', function() {
			scope.selectedItem = {};
			scope.$apply();
			expect(scope.propertyId).toEqual(null);
		});

		it('should set the property id to be null if the selected property changes to a falsey value', function() {
			scope.selectedItem = null;
			scope.$apply();
			expect(scope.propertyId).toEqual(null);
		});
	});

	describe('getting classes', function() {
		it('should call the properties service to get all classes', function() {
			expect(propertiesService.getClasses).toHaveBeenCalled();
		});

		it('should handle any errors if the retrieving classes was not successful', function() {
			deferredGetClasses.reject();
			scope.$apply();
			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set data.classes to the returned classes after a successful update', function() {
			deferredGetClasses.resolve([SITE_CONDITION]);
			scope.$apply();

			expect(scope.data.classes).toEqual([SITE_CONDITION]);
		});
	});

	describe('$scope.editProperty', function() {

		it('should set editing to be true', function() {
			scope.editing = false;
			scope.editProperty(fakeEvent);
			expect(scope.editing).toBe(true);
		});
	});

	describe('$scope.cancel', function() {
		it('should set editing to be false', function() {
			scope.editing = true;
			scope.cancel(fakeEvent);
			expect(scope.editing).toBe(false);
		});

		it('should set the model back to the original unchanged property', function() {
			scope.model = null;
			scope.selectedProperty = BLAST;
			scope.cancel(fakeEvent);
			expect(scope.model).toEqual(scope.selectedProperty);
		});
	});

	describe('$scope.saveChanges', function() {

		beforeEach(function() {
			scope.updateSelectedProperty = function(/*model*/) {};
		});

		it('should call the properties service to update the property', function() {
			scope.saveChanges(fakeEvent, BLAST.id, BLAST);
			expect(propertiesService.updateProperty).toHaveBeenCalledWith(BLAST.id, BLAST);
		});

		it('should handle any errors if the update was not successful', function() {
			scope.saveChanges(fakeEvent, BLAST.id, BLAST);

			deferredUpdateProperty.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set editing to false after a successful update', function() {
			scope.editing = true;
			scope.saveChanges(fakeEvent, BLAST.id, BLAST);

			deferredUpdateProperty.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
		});

		it('should update property on the parent scope after a successful update', function() {
			spyOn(scope, 'updateSelectedProperty').and.callThrough();

			scope.saveChanges(fakeEvent, BLAST.id, BLAST);

			deferredUpdateProperty.resolve();
			scope.$apply();

			expect(scope.updateSelectedProperty).toHaveBeenCalledWith(BLAST);
		});
	});

});
