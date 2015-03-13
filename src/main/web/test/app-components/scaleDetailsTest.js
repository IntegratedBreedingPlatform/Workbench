/*global angular, expect, inject, spyOn*/
'use strict';

describe('Scale details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},
		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},
		PERCENTAGE = {
			id: 1,
			name: 'Percentage'
		},
		scalesService = {},
		scope,
		q,
		directiveElement,
		deferredUpdateScale;

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-scale-details></om-scale-details>')(scope);
		});
		scope.$digest();
	}

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));

	beforeEach(module('scaleDetails', function($provide) {
		// Provide mocks for the directive controller
		$provide.value('scalesService', scalesService);
		$provide.value('serviceUtilities', serviceUtilities);
	}));

	beforeEach(inject(function($rootScope, $q) {
		q = $q;
		scope = $rootScope;

		scalesService.updateScale = function() {
			deferredUpdateScale = q.defer();
			return deferredUpdateScale.promise;
		};

		spyOn(scalesService, 'updateScale').and.callThrough();
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');

		compileDirective();
	}));

	describe('by default', function() {

		it('should set editing to false', function() {
			expect(scope.editing).toBe(false);
		});

		it('should set the model to be the selected scale if the selected scale changes', function() {
			scope.selectedScale = PERCENTAGE;
			scope.$apply();
			expect(scope.model).toEqual(PERCENTAGE);
		});

		it('should set the scale id to be the id of the selected item if the selected scale changes', function() {
			scope.selectedItem = PERCENTAGE;
			scope.$apply();
			expect(scope.scaleId).toEqual(PERCENTAGE.id);
		});

		it('should set the scale id to be null if the selected scale changes and has no id', function() {
			scope.selectedItem = {};
			scope.$apply();
			expect(scope.scaleId).toEqual(null);
		});

		it('should set the scale id to be null if the selected scale changes to a falsey value', function() {
			scope.selectedItem = null;
			scope.$apply();
			expect(scope.scaleId).toEqual(null);
		});
	});

	describe('$scope.editScale', function() {

		it('should set editing to be true', function() {
			scope.editing = false;
			scope.editScale(fakeEvent);
			expect(scope.editing).toBe(true);
		});
	});

	describe('$scope.cancel', function() {
		it('should set editing to be false', function() {
			scope.editing = true;
			scope.cancel(fakeEvent);
			expect(scope.editing).toBe(false);
		});

		it('should set the model back to the original unchanged scale', function() {
			scope.model = null;
			scope.selectedScale = PERCENTAGE;
			scope.cancel(fakeEvent);
			expect(scope.model).toEqual(scope.selectedScale);
		});
	});

	describe('$scope.saveChanges', function() {

		beforeEach(function() {
			scope.updateSelectedScale = function(/*model*/) {};
		});

		it('should call the scales service to update the scale', function() {
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);
			expect(scalesService.updateScale).toHaveBeenCalledWith(PERCENTAGE.id, PERCENTAGE);
		});

		it('should handle any errors if the update was not successful', function() {
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);

			deferredUpdateScale.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set editing to false after a successful update', function() {
			scope.editing = true;
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);

			deferredUpdateScale.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
		});

		it('should update scale on the parent scope after a successful update', function() {
			spyOn(scope, 'updateSelectedScale').and.callThrough();

			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);

			deferredUpdateScale.resolve();
			scope.$apply();

			expect(scope.updateSelectedScale).toHaveBeenCalledWith(PERCENTAGE);
		});
	});

});

