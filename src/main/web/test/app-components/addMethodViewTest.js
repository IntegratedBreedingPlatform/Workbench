/*global expect, inject, spyOn*/
'use strict';

describe('Add Method View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		CUT_AND_DRY = {
			name: 'Cut and Dry',
			description: 'Self explanatory really'
		},

		methodsService,

		variableStateService = {
			updateInProgress: function() {},
			setMethod: function() {}
		},

		serviceUtilities = {
			formatErrorsForDisplay: function() {}
		},

		formUtilities,
		methodFormService,

		q,
		controller,
		location,
		scope,
		window,

		deferredAddMethod;

	beforeEach(function() {
		module('addMethod');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window, _formUtilities_, _methodFormService_) {

		methodsService = {
			addMethod: function() {
				deferredAddMethod = q.defer();
				return deferredAddMethod.promise;
			}
		};

		spyOn(methodsService, 'addMethod').and.callThrough();
		spyOn(serviceUtilities, 'formatErrorsForDisplay');

		controller = $controller('AddMethodController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			methodsService: methodsService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;
		formUtilities = _formUtilities_;
		methodFormService = _methodFormService_;

		// Pretend our form is valid
		scope.amForm = {
			$valid: true,
			$setUntouched: function() {}
		};
	}));

	describe('$scope.saveMethod', function() {

		it('should call the method service to save the method', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			expect(methodsService.addMethod).toHaveBeenCalledWith(CUT_AND_DRY);
		});

		it('should not call the method service if the form is not valid', function() {
			// Set the form to be invalid
			scope.amForm.$valid = false;

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			expect(methodsService.addMethod.calls.count()).toEqual(0);
		});

		it('should handle any errors and not redirect if the save was not successful', function() {

			var response = {
				status: 400,
				errors: [{fieldNames: [], message: 'An error'}]
			};

			spyOn(location, 'path');

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			deferredAddMethod.reject(response);
			scope.$apply();

			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalledWith(response);
			expect(location.path.calls.count()).toEqual(0);
		});

		it('should redirect to /methods after a successful save, if no variable is currently being edited', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(location, 'path');

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			deferredAddMethod.resolve({id: '1'});
			scope.$apply();

			expect(location.path).toHaveBeenCalledWith('/methods');
		});

		it('should set the method on the variable and go back to add variable after a successful save, if a variable is being edited',
			function() {

				var deferred;

				variableStateService.setMethod = function() {
					deferred = q.defer();
					return deferred.promise;
				};

				// Variable edit is in progress
				spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
				spyOn(variableStateService, 'setMethod').and.callThrough();
				spyOn(location, 'path');

				// Successful save
				scope.saveMethod(fakeEvent, CUT_AND_DRY);
				deferredAddMethod.resolve({id: '1'});
				scope.$apply();

				// Successfully set the method
				deferred.resolve();
				scope.$apply();

				expect(variableStateService.setMethod).toHaveBeenCalledWith(CUT_AND_DRY.id, CUT_AND_DRY.name);
				expect(location.path).toHaveBeenCalledWith('/add/variable');
			}
		);

		it('should handle any errors and set the form to untouched if the save was not successful', function() {
			spyOn(scope.amForm, '$setUntouched');

			scope.saveMethod(fakeEvent, CUT_AND_DRY.id, CUT_AND_DRY);

			deferredAddMethod.reject();
			scope.$apply();

			expect(scope.amForm.$setUntouched).toHaveBeenCalled();
			expect(serviceUtilities.formatErrorsForDisplay).toHaveBeenCalled();
		});
	});

	describe('$scope.cancel', function() {

		it('should call the cancel handler', function() {
			scope.amForm = {
				$dirty: true,
				method: {
					name: 'Name'
				}
			};

			spyOn(formUtilities, 'cancelAddHandler');

			scope.cancel(fakeEvent);

			expect(formUtilities.cancelAddHandler).toHaveBeenCalled();
		});

		it('should set the path to add variable if a variable is in the process of being added', function() {
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(formUtilities, 'cancelAddHandler');

			scope.cancel(fakeEvent);
			expect(formUtilities.cancelAddHandler).toHaveBeenCalledWith(scope, false, '/add/variable');
		});

		it('should set the path to the methods list if there is no variable add in progress', function() {
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(formUtilities, 'cancelAddHandler');

			scope.cancel(fakeEvent);
			expect(formUtilities.cancelAddHandler).toHaveBeenCalledWith(scope, false, '/methods');
		});
	});

	describe('methodFormService', function() {

		describe('formEmpty', function() {

			it('should return false if the name or description are present', function() {
				var name = {
						name: 'name'
					},
					description = {
						description: 'description'
					};

				expect(methodFormService.formEmpty(name)).toBe(false);
				expect(methodFormService.formEmpty(description)).toBe(false);
			});

			it('should return true if no fields are valued', function() {
				var model = {};

				expect(methodFormService.formEmpty(model)).toBe(true);
			});
		});
	});
});
