/*global angular, expect, inject, spyOn*/
'use strict';

describe('Method details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},
		serviceUtilities = {
			formatErrorsForDisplay: function() {}
		},
		panelService = {
			hidePanel: function() {}
		},
		CUT_AND_DRY = {
			id: 1,
			name: 'Cut and Dry',
			editableFields: [ 'description' ]
		},
		methodsService = {},
		formUtilities,
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

	beforeEach(inject(function($rootScope, $q, _formUtilities_) {
		q = $q;
		scope = $rootScope;
		formUtilities = _formUtilities_;

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
		spyOn(serviceUtilities, 'formatErrorsForDisplay');
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

		it('should show non-editable fields alert if the selected item does not have all fields in editable fields list', function() {
			scope.selectedMethod = CUT_AND_DRY;
			scope.$apply();
			scope.editing = true;
			scope.$apply();
			expect(scope.showNoneditableFieldsAlert).toEqual(true);
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

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should set editing to false if the user has not made any edits', function() {
			scope.selectedMethod = {
				name: 'method'
			};
			scope.model = angular.copy(scope.selectedMethod);
			scope.editing = true;

			scope.cancel(fakeEvent);

			expect(scope.editing).toBe(false);
		});

		it('should call the confirmation handler if the user has made edits', function() {
			scope.selectedMethod = {
				name: 'method'
			};
			scope.model = {
				name: 'new_method_name'
			};

			scope.cancel(fakeEvent);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should set editing to false and reset the model when the confirmation handler is resolved', function() {
			scope.selectedMethod = {
				name: 'method'
			};
			scope.model = {
				name: 'new_method_name'
			};

			scope.cancel(fakeEvent);
			confirmation.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
			expect(scope.model).toEqual(scope.selectedMethod);
		});
	});

	describe('$scope.saveChanges', function() {

		var timeout;

		beforeEach(inject(function($timeout) {
			timeout = $timeout;

			scope.updateSelectedMethod = function(/*model*/) {};

			// Pretend our form is valid
			scope.mdForm = {
				$valid: true,
				$setUntouched: function() {}
			};
		}));

		it('should call the methods service to update the method', function() {
			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);
			expect(methodsService.updateMethod).toHaveBeenCalledWith(CUT_AND_DRY.id, CUT_AND_DRY);
		});

		it('should not call the methods service if the form is not valid', function() {
			// Set the form to be invalid
			scope.mdForm.$valid = false;

			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);

			expect(methodsService.updateMethod.calls.count()).toEqual(0);
		});

		it('should show the throbber if the form is valid and submitted', function() {
			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);
			timeout.flush();
			expect(scope.showThrobber).toBe(true);
		});

		it('should not show the throbber if the form is not in a submitted state', function() {
			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);
			scope.submitted = false;
			timeout.flush();
			expect(scope.showThrobber).toBeFalsy();
		});

		it('should handle any errors and set the form to untouched if the update was not successful', function() {
			spyOn(scope.mdForm, '$setUntouched');

			scope.saveChanges(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);

			deferredUpdateMethod.reject();
			scope.$apply();

			expect(scope.mdForm.$setUntouched).toHaveBeenCalled();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
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

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			scope.updateSelectedMethod = function(/*model*/) {};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should call the confirmation handler', function() {
			scope.deleteMethod(fakeEvent, CUT_AND_DRY.id);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should call the methods service to delete the method if the confirmation is resolved', function() {
			scope.deleteMethod(fakeEvent, CUT_AND_DRY.id);

			confirmation.resolve();
			scope.$apply();

			expect(methodsService.deleteMethod).toHaveBeenCalledWith(CUT_AND_DRY.id);
		});

		it('should set an error if the update was not successful', function() {
			scope.clientErrors = {};
			scope.deleteMethod(fakeEvent, CUT_AND_DRY.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteMethod.reject();
			scope.$apply();

			expect(scope.clientErrors.failedToDelete).toBe(true);
		});

		it('should remove method on the parent scope and hide the panel after a successful delete', function() {
			spyOn(scope, 'updateSelectedMethod').and.callThrough();

			scope.deleteMethod(fakeEvent, CUT_AND_DRY.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteMethod.resolve();
			scope.$apply();

			expect(panelService.hidePanel).toHaveBeenCalled();
			expect(scope.updateSelectedMethod).toHaveBeenCalledWith();
		});
	});
});

