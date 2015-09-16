/*global angular, expect, inject, spyOn*/
'use strict';

describe('Scale details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		serviceUtilities = {
			formatErrorsForDisplay: function() {},
			filterOutSystemDataTypes: function() {
				return types;
			}
		},

		panelService = {
			hidePanel: function() {}
		},

		dataTypesService = {
			getNonSystemDataTypes: function() {}
		},

		CATEGORICAL_TYPE = {
			id: 1,
			name: 'Categorical',
			systemDataType: false
		},

		NUMERIC_TYPE = {
			id: 2,
			name: 'Numeric',
			systemDataType: false
		},

		CHARACTER_TYPE = {
			id: 3,
			name: 'Character',
			systemDataType: false
		},

		PERCENTAGE = {
			id: 1,
			name: 'Percentage',
			description: 'As per title, really',
			dataType: NUMERIC_TYPE,
			metadata: {
				editableFields: ['description']
			}
		},

		SCORE = {
			id: 2,
			name: 'Score',
			description: 'Score, 1 - 5',
			dataType: CATEGORICAL_TYPE,
			validValues: {
				categories: []
			}
		},

		FREE_TEXT = {
			id: 2,
			name: 'Text',
			description: 'Some text',
			dataType: CHARACTER_TYPE
		},

		types = [angular.copy(CATEGORICAL_TYPE), angular.copy(NUMERIC_TYPE), angular.copy(CHARACTER_TYPE)],

		scalesService = {},
		formUtilities,
		scope,
		q,
		directiveElement,
		deferredUpdateScale,
		deferredDeleteScale,
		deferredGetNonSystemDataTypes,
		mockTranslateFilter;

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-scale-details></om-scale-details>')(scope);
		});
		scope.$digest();
	}

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};

		angular.mock.module('templates');

		module('scaleDetails', function($provide) {
			// Provide mocks for the directive controller
			$provide.value('dataTypesService', dataTypesService);
			$provide.value('scalesService', scalesService);
			$provide.value('serviceUtilities', serviceUtilities);
			$provide.value('panelService', panelService);
		});
	});

	beforeEach(inject(function($rootScope, $q, _formUtilities_) {
		q = $q;
		scope = $rootScope;
		formUtilities = _formUtilities_;

		scalesService.updateScale = function() {
			deferredUpdateScale = q.defer();
			return deferredUpdateScale.promise;
		};

		scalesService.deleteScale = function() {
			deferredDeleteScale = q.defer();
			return deferredDeleteScale.promise;
		};

		dataTypesService.getNonSystemDataTypes = function() {
			deferredGetNonSystemDataTypes = q.defer();
			return deferredGetNonSystemDataTypes.promise;
		};

		spyOn(scalesService, 'updateScale').and.callThrough();
		spyOn(scalesService, 'deleteScale').and.callThrough();
		spyOn(dataTypesService, 'getNonSystemDataTypes').and.callThrough();
		spyOn(serviceUtilities, 'formatErrorsForDisplay');
		spyOn(panelService, 'hidePanel');

		compileDirective();
	}));

	describe('by default', function() {

		it('should set editing to false', function() {
			expect(scope.editing).toBe(false);
		});

		it('should hide the range and categories widgets', function() {
			expect(scope.showRangeWidget).toBe(false);
			expect(scope.showCategoriesWidget).toBe(false);
		});

		it('should should add one empty category', function() {
			scope.selectedScale = SCORE;
			scope.$apply();
			expect(scope.model.validValues.categories.length).toBe(1);
		});

		it('should reset errors and remove any leftover confirmation handlers if the selected method changes', function() {
			scope.selectedScale = PERCENTAGE;
			scope.deny = function() {};
			scope.clientErrors = { general: ['error'] };

			spyOn(scope, 'deny');

			scope.$apply();

			expect(scope.deny).toHaveBeenCalled();
			expect(scope.clientErrors).toEqual({});
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

		it('should initialise categories if there aren\'t any yet and the scale type is changed to be Categorical', function() {
			scope.selectedScale = SCORE;
			scope.$apply();
			// Use angular.equals to ignore the $$hashKey property
			expect(angular.equals(scope.model.validValues.categories, [{}])).toBe(true);
		});

		it('should not initialise categories if there are already categories and the scale type is changed to be Categorical', function() {
			scope.selectedScale = SCORE;
			scope.selectedScale.validValues.categories.push({name: '1', description: 'low'});
			scope.$apply();
			// Use angular.equals to ignore the $$hashKey property
			expect(angular.equals(scope.model.validValues.categories, [{name: '1', description: 'low'}])).toBe(true);
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

		it('should show non-editable fields alert if the selected item does not have all fields in editable fields list', function() {
			scope.selectedScale = PERCENTAGE;
			scope.$apply();
			scope.editing = true;
			scope.$apply();
			expect(scope.showNoneditableFieldsAlert).toEqual(true);
		});
	});

	describe('$scope.editScale', function() {

		it('should set the non system data types on the scope', function() {
			scope.editScale(fakeEvent);

			deferredGetNonSystemDataTypes.resolve(types);
			scope.$apply();

			expect(scope.types).toEqual(types);
		});

		it('should set editing to be true', function() {
			scope.editing = false;
			scope.editScale(fakeEvent);

			expect(scope.editing).toBe(true);
		});

		it('should handle any errors if the retrieving data types was not successful', function() {
			scope.editScale(fakeEvent);

			deferredGetNonSystemDataTypes.reject();
			scope.$apply();

			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
			expect(scope.someListsNotLoaded).toBe(true);
		});
	});

	describe('$scope.cancel', function() {

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should set editing to false if the user has not made any edits', function() {
			scope.selectedScale = {
				name: 'scale'
			};
			scope.model = angular.copy(scope.selectedScale);
			scope.editing = true;

			scope.cancel(fakeEvent);

			expect(scope.editing).toBe(false);
		});

		it('should call the confirmation handler if the user has made edits', function() {
			scope.selectedScale = {
				name: 'scale'
			};
			scope.model = {
				name: 'new_scale_name'
			};

			scope.cancel(fakeEvent);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should set editing to false and reset the model when the confirmation handler is resolved', function() {
			scope.selectedScale = {
				name: 'scale'
			};
			scope.model = {
				name: 'new_scale_name'
			};

			scope.cancel(fakeEvent);
			confirmation.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
			expect(scope.model).toEqual(scope.selectedScale);
		});
	});

	describe('$scope.saveChanges', function() {

		var timeout;

		beforeEach(inject(function($timeout) {
			timeout = $timeout;
			scope.updateSelectedScale = function(/*model*/) {};
			scope.sdForm.$valid = true;
		}));

		it('should call the scales service to update the scale', function() {
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);
			expect(scalesService.updateScale).toHaveBeenCalledWith(PERCENTAGE.id, PERCENTAGE);
		});

		it('should not call the scales service if the form is not valid', function() {
			// Set the form to be invalid
			scope.sdForm.$valid = false;
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);

			expect(scalesService.updateScale.calls.count()).toEqual(0);
		});

		it('should show the throbber if the form is valid and submitted', function() {
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);
			timeout.flush();
			expect(scope.showThrobber).toBe(true);
		});

		it('should not show the throbber if the form is not in a submitted state', function() {
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);
			scope.submitted = false;
			timeout.flush();
			expect(scope.showThrobber).toBeFalsy();
		});

		it('should handle any errors if the update was not successful', function() {
			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);

			deferredUpdateScale.reject();
			scope.$apply();

			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
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

		it('should handle any errors and set the form to untouched if the update was not successful', function() {
			spyOn(scope.sdForm, '$setUntouched');

			scope.saveChanges(fakeEvent, PERCENTAGE.id, PERCENTAGE);

			deferredUpdateScale.reject();
			scope.$apply();

			expect(scope.sdForm.$setUntouched).toHaveBeenCalled();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
		});
	});

	describe('$scope.deleteScale', function() {

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			scope.updateSelectedScale = function(/*model*/) {};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should call the confirmation handler', function() {
			scope.deleteScale(fakeEvent, PERCENTAGE.id);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should call the scales service to delete the scale if the confirmation is resolved', function() {
			scope.deleteScale(fakeEvent, PERCENTAGE.id);

			confirmation.resolve();
			scope.$apply();

			expect(scalesService.deleteScale).toHaveBeenCalledWith(PERCENTAGE.id);
		});

		it('should set an error if the update was not successful', function() {
			scope.clientErrors = {};
			scope.deleteScale(fakeEvent, PERCENTAGE.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteScale.reject();
			scope.$apply();

			expect(scope.clientErrors.failedToDelete).toBe(true);
		});

		it('should remove scale on the parent scope and hide the panel after a successful delete', function() {
			spyOn(scope, 'updateSelectedScale').and.callThrough();

			scope.deleteScale(fakeEvent, PERCENTAGE.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteScale.resolve();
			scope.$apply();

			expect(panelService.hidePanel).toHaveBeenCalled();
			expect(scope.updateSelectedScale).toHaveBeenCalledWith();
		});
	});
});
