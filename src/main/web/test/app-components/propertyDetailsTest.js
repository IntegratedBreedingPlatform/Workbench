/*global angular, expect, inject, spyOn*/
'use strict';

describe('Property details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},
		panelService = {
			hidePanel: function() {}
		},
		serviceUtilities = {
			formatErrorsForDisplay: function() {}
		},
		BLAST = {
			id: 1,
			name: 'Blast',
			metadata: {
				editableFields: ['description', 'classes', 'cropOntologyId']
			}
		},
		SITE_CONDITION = 'Site Condition',
		propertiesService = {},
		formUtilities,
		scope,
		q,
		directiveElement,
		deferredGetClasses,
		deferredUpdateProperty,
		deferredDeleteProperty,
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
		$provide.value('panelService', panelService);
	}));

	beforeEach(inject(function($rootScope, $q, _formUtilities_) {
		q = $q;
		scope = $rootScope;
		formUtilities = _formUtilities_;

		propertiesService.getClasses = function() {
			deferredGetClasses = q.defer();
			return deferredGetClasses.promise;
		};

		propertiesService.updateProperty = function() {
			deferredUpdateProperty = q.defer();
			return deferredUpdateProperty.promise;
		};

		propertiesService.deleteProperty = function() {
			deferredDeleteProperty = q.defer();
			return deferredDeleteProperty.promise;
		};

		spyOn(propertiesService, 'getClasses').and.callThrough();
		spyOn(propertiesService, 'updateProperty').and.callThrough();
		spyOn(propertiesService, 'deleteProperty').and.callThrough();
		spyOn(serviceUtilities, 'formatErrorsForDisplay');
		spyOn(panelService, 'hidePanel');

		compileDirective();
	}));

	describe('by default', function() {

		it('should set editing to false', function() {
			expect(scope.editing).toBe(false);
		});

		it('should set data to have an empty array of classes', function() {
			expect(scope.data).toEqual({classes: []});
		});

		it('should reset errors and remove any leftover confirmation handlers if the selected method changes', function() {
			scope.selectedProperty = BLAST;
			scope.deny = function() {};
			scope.serverErrors = { general: ['error'] };
			scope.clientErrors = { general: ['error'] };

			spyOn(scope, 'deny');

			scope.$apply();

			expect(scope.deny).toHaveBeenCalled();
			expect(scope.serverErrors).toEqual({});
			expect(scope.clientErrors).toEqual({});
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

		it('should show non-editable fields alert if the selected item does not have all fields in editable fields list', function() {
			scope.selectedProperty = BLAST;
			scope.$apply();
			scope.editing = true;
			scope.$apply();
			expect(scope.showNoneditableFieldsAlert).toEqual(true);
		});
	});

	describe('getting classes', function() {
		it('should call the properties service to get all classes', function() {
			expect(propertiesService.getClasses).toHaveBeenCalled();
		});

		it('should handle any errors if the retrieving classes was not successful', function() {
			deferredGetClasses.reject();
			scope.$apply();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
			expect(scope.someListsNotLoaded).toBe(true);
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

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should set editing to false if the user has not made any edits', function() {
			scope.selectedProperty = {
				name: 'property'
			};
			scope.model = angular.copy(scope.selectedProperty);
			scope.editing = true;

			scope.cancel(fakeEvent);

			expect(scope.editing).toBe(false);
		});

		it('should call the confirmation handler if the user has made edits', function() {
			scope.selectedProperty = {
				name: 'property'
			};
			scope.model = {
				name: 'new_property_name'
			};

			scope.cancel(fakeEvent);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should set editing to false and reset the model when the confirmation handler is resolved', function() {
			scope.selectedProperty = {
				name: 'property'
			};
			scope.model = {
				name: 'new_property_name'
			};

			scope.cancel(fakeEvent);
			confirmation.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
			expect(scope.model).toEqual(scope.selectedProperty);
		});
	});

	describe('$scope.saveChanges', function() {

		var timeout;

		beforeEach(inject(function($timeout) {
			timeout = $timeout;

			scope.updateSelectedProperty = function(/*model*/) {};

			// Pretend our form is valid
			scope.pdForm = {
				$valid: true,
				$setUntouched: function() {}
			};
		}));

		it('should call the properties service to update the property', function() {
			scope.saveChanges(fakeEvent, BLAST.id, BLAST);
			expect(propertiesService.updateProperty).toHaveBeenCalledWith(BLAST.id, BLAST);
		});

		it('should not call the properties service if the form is not valid', function() {
			// Set the form to be invalid
			scope.pdForm.$valid = false;

			scope.saveChanges(fakeEvent, BLAST.id, BLAST);

			expect(propertiesService.updateProperty.calls.count()).toEqual(0);
		});

		it('should show the throbber if the form is valid and submitted', function() {
			scope.saveChanges(fakeEvent, BLAST.id, BLAST);
			timeout.flush();
			expect(scope.showThrobber).toBe(true);
		});

		it('should not show the throbber if the form is not in a submitted state', function() {
			scope.saveChanges(fakeEvent, BLAST.id, BLAST);
			scope.submitted = false;
			timeout.flush();
			expect(scope.showThrobber).toBeFalsy();
		});

		it('should handle any errors if the update was not successful', function() {
			scope.saveChanges(fakeEvent, BLAST.id, BLAST);

			deferredUpdateProperty.reject();
			scope.$apply();

			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
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

		it('should handle any errors and set the form to untouched if the update was not successful', function() {
			spyOn(scope.pdForm, '$setUntouched');

			scope.saveChanges(fakeEvent, BLAST.id, BLAST);

			deferredUpdateProperty.reject();
			scope.$apply();

			expect(scope.pdForm.$setUntouched).toHaveBeenCalled();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
		});
	});

	describe('$scope.deleteProperty', function() {

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			scope.updateSelectedProperty = function(/*model*/) {};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should call the confirmation handler', function() {
			scope.deleteProperty(fakeEvent, BLAST.id);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should call the properties service to delete the variable if the confirmation is resolved', function() {
			scope.deleteProperty(fakeEvent, BLAST.id);

			confirmation.resolve();
			scope.$apply();

			expect(propertiesService.deleteProperty).toHaveBeenCalledWith(BLAST.id);
		});

		it('should set an error if the update was not successful', function() {
			scope.clientErrors = {};
			scope.deleteProperty(fakeEvent, BLAST.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteProperty.reject();
			scope.$apply();

			expect(scope.clientErrors.failedToDelete).toBe(true);
		});

		it('should remove property on the parent scope and hide the panel after a successful delete', function() {
			spyOn(scope, 'updateSelectedProperty').and.callThrough();

			scope.deleteProperty(fakeEvent, BLAST.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteProperty.resolve();
			scope.$apply();

			expect(panelService.hidePanel).toHaveBeenCalled();
			expect(scope.updateSelectedProperty).toHaveBeenCalledWith();
		});
	});
});
