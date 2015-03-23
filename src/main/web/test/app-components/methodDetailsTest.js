/*global angular, expect, inject, spyOn*/
'use strict';

describe('Method details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},
		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},
		panelService = {
			hidePanel: function() {}
		},
		CUT_AND_DRY = {
			id: 1,
			name: 'Cut and Dry'
		},
		methodsService = {},
		scope,
		q,
		directiveElement,
		deferredUpdateMethod,
		deferredDeleteMethod,
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
			directiveElement = $compile('<om-method-details></om-method-details>')(scope);
		});
		scope.$digest();
	}

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));

	beforeEach(module('methodDetails', function($provide) {
		// Provide mocks for the directive controller
		$provide.value('methodsService', methodsService);
		$provide.value('serviceUtilities', serviceUtilities);
		$provide.value('panelService', panelService);
	}));

	beforeEach(inject(function($rootScope, $q) {
		q = $q;
		scope = $rootScope;

		methodsService.updateMethod = function() {
			deferredUpdateMethod = q.defer();
			return deferredUpdateMethod.promise;
		};

		methodsService.deleteMethod = function() {
			deferredDeleteMethod = q.defer();
			return deferredDeleteMethod.promise;
		};

		spyOn(methodsService, 'updateMethod').and.callThrough();
		spyOn(methodsService, 'deleteMethod').and.callThrough();
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');
		spyOn(panelService, 'hidePanel');

		compileDirective();
	}));

	describe('by default', function() {

		it('should set editing to false', function() {
			expect(scope.editing).toBe(false);
		});

		it('should set the model to be the selected method if the selected method changes', function() {
			scope.selectedMethod = CUT_AND_DRY;
			scope.$apply();
			expect(scope.model).toEqual(CUT_AND_DRY);
		});

		it('should set the method id to be the id of the selected item if the selected method changes', function() {
			scope.selectedItem = CUT_AND_DRY;
			scope.$apply();
			expect(scope.methodId).toEqual(CUT_AND_DRY.id);
		});

		it('should set the method id to be null if the selected method changes and has no id', function() {
			scope.selectedItem = {};
			scope.$apply();
			expect(scope.methodId).toEqual(null);
		});

		it('should set the method id to be null if the selected method changes to a falsey value', function() {
			scope.selectedItem = null;
			scope.$apply();
			expect(scope.methodId).toEqual(null);
		});
	});

	describe('$scope.editMethod', function() {

		it('should set editing to be true', function() {
			scope.editing = false;
			scope.editMethod(fakeEvent);
			expect(scope.editing).toBe(true);
		});
	});

	describe('$scope.cancel', function() {
		it('should set editing to be false', function() {
			scope.editing = true;
			scope.cancel(fakeEvent);
			expect(scope.editing).toBe(false);
		});

		it('should set the model back to the original unchanged method', function() {
			scope.model = null;
			scope.selectedMethod = CUT_AND_DRY;
			scope.cancel(fakeEvent);
			expect(scope.model).toEqual(scope.selectedMethod);
		});
	});

	describe('$scope.saveChanges', function() {

		beforeEach(function() {
			scope.updateSelectedMethod = function(/*model*/) {};
		});

		it('should call the methods service to update the method', function() {
			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);
			expect(methodsService.updateMethod).toHaveBeenCalledWith(CUT_AND_DRY.id, CUT_AND_DRY);
		});

		it('should handle any errors if the update was not successful', function() {
			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);

			deferredUpdateMethod.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set editing to false after a successful update', function() {
			scope.editing = true;
			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);

			deferredUpdateMethod.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
		});

		it('should update method on the parent scope after a successful update', function() {
			spyOn(scope, 'updateSelectedMethod').and.callThrough();

			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);

			deferredUpdateMethod.resolve();
			scope.$apply();

			expect(scope.updateSelectedMethod).toHaveBeenCalledWith(CUT_AND_DRY);
		});
	});

	describe('$scope.deleteMethod', function() {

		beforeEach(function() {
			scope.updateSelectedMethod = function(/*model*/) {};
		});

		it('should call the properties service to delete the variable', function() {
			scope.deleteMethod(fakeEvent, CUT_AND_DRY.id);
			expect(methodsService.deleteMethod).toHaveBeenCalledWith(CUT_AND_DRY.id);
		});

		it('should handle any errors if the update was not successful', function() {
			scope.deleteMethod(fakeEvent, CUT_AND_DRY.id);

			deferredDeleteMethod.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should remove variable on the parent scope and hide the panel after a successful delete', function() {
			spyOn(scope, 'updateSelectedMethod').and.callThrough();

			scope.deleteMethod(fakeEvent, CUT_AND_DRY.id);

			deferredDeleteMethod.resolve();
			scope.$apply();

			expect(panelService.hidePanel).toHaveBeenCalled();
			expect(scope.updateSelectedMethod).toHaveBeenCalledWith();
		});
	});

});

