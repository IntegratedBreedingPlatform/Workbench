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
			name: 'Percentage',
			description: 'As per title, really',
			dataType: {
				id: 2,
				name: 'Numeric'
			}
		},

		SCORE = {
			id: 2,
			name: 'Score',
			description: 'Score, 1 - 5',
			dataType: {
				id: 1,
				name: 'Categorical'
			}
		},

		FREE_TEXT = {
			id: 2,
			name: 'Text',
			description: 'Some text',
			dataType: {
				id: 3,
				name: 'Character'
			}
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

		it('should hide the range and categories widgets by default', function() {
			expect(scope.showRangeWidget).toBe(false);
			expect(scope.showCategoriesWidget).toBe(false);
		});

		it('should show the range widget and hide the categories widget if the scale type is changed to be Numeric', function() {
			scope.selectedScale = PERCENTAGE;
			scope.$apply();
			expect(scope.showRangeWidget).toBe(true);
			expect(scope.showCategoriesWidget).toBe(false);
		});

		it('should show the categories widget and hide the range widget if the scale type is changed to be Categorical', function() {
			scope.selectedScale = SCORE;
			scope.$apply();
			expect(scope.showRangeWidget).toBe(false);
			expect(scope.showCategoriesWidget).toBe(true);
		});

		it('should hide the categories and range widgets if the scale type is changed to be something other than Categorical or Numeric',
			function() {
				scope.selectedScale = FREE_TEXT;
				scope.$apply();
				expect(scope.showRangeWidget).toBe(false);
				expect(scope.showCategoriesWidget).toBe(false);
			}
		);

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

