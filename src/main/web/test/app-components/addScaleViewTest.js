/*global expect, inject, spyOn*/
'use strict';

describe('Add Scale View', function() {
	var fakeEvent = {
			preventDefault: function() {}
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
			name: 'Percentage',
			description: 'As per title',
			dataType: NUMERIC_TYPE,
			validValues: {
				min: 0,
				max: 100
			}
		},

		SOME_LISTS_NOT_LOADED = 'validation.scale.someListsNotLoaded',

		types = [CATEGORICAL_TYPE, NUMERIC_TYPE, CHARACTER_TYPE],

		dataTypesService = {
			getNonSystemDataTypes: function() {}
		},

		variableStateService = {
			updateInProgress: function() {},
			setScale: function() {}
		},

		serviceUtilities = {
			serverErrorHandler: function() {
				return {};
			}
		},

		scalesService,
		formUtilities,
		scaleFormService,

		q,
		controller,
		location,
		scope,
		window,

		deferredGetNonSystemDataTypes,
		deferredAddScale;

	beforeEach(function() {
		module('addScale');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window, _formUtilities_, _scaleFormService_) {

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;
		formUtilities = _formUtilities_;
		scaleFormService = _scaleFormService_;

		dataTypesService.getNonSystemDataTypes = function() {
			deferredGetNonSystemDataTypes = q.defer();
			return deferredGetNonSystemDataTypes.promise;
		};

		scalesService = {
			addScale: function() {
				deferredAddScale = q.defer();
				return deferredAddScale.promise;
			}
		};

		// Pretend our form is valid
		scope.asForm = {
			$valid: true,
			$setUntouched: function() {}
		};

		spyOn(scalesService, 'addScale').and.callThrough();
		spyOn(serviceUtilities, 'serverErrorHandler').and.callThrough();
		spyOn(dataTypesService, 'getNonSystemDataTypes').and.callThrough();

		controller = $controller('AddScaleController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			scalesService: scalesService,
			dataTypesService: dataTypesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});
	}));

	it('should set the non system data types on the scope', function() {
		deferredGetNonSystemDataTypes.resolve(types);
		scope.$apply();
		expect(scope.types).toEqual(types);
	});

	it('should display errors if data types were not retrieved successfully', function() {
		deferredGetNonSystemDataTypes.reject();
		scope.$apply();
		expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
		expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
	});

	it('should hide the range and categories widgets by default', function() {
		expect(scope.showRangeWidget).toBe(false);
		expect(scope.showCategoriesWidget).toBe(false);
	});

	it('should show the range widget and hide the categories widget if the scale type is changed to be Numeric', function() {
		scope.scale.dataType = NUMERIC_TYPE;
		scope.$apply();
		expect(scope.showRangeWidget).toBe(true);
		expect(scope.showCategoriesWidget).toBe(false);
	});

	it('should show the categories widget and hide the range widget if the scale type is changed to be Categorical', function() {
		scope.scale.dataType = CATEGORICAL_TYPE;
		scope.$apply();
		expect(scope.showRangeWidget).toBe(false);
		expect(scope.showCategoriesWidget).toBe(true);
	});

	it('should hide the categories and range widgets if the scale type is changed to be something other than Categorical or Numeric',
		function() {
			scope.scale.dataType = CHARACTER_TYPE;
			scope.$apply();
			expect(scope.showRangeWidget).toBe(false);
			expect(scope.showCategoriesWidget).toBe(false);
		}
	);

	describe('$scope.saveScale', function() {

		it('should call the scale service to save the scale', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);

			scope.saveScale(fakeEvent, PERCENTAGE);

			expect(scalesService.addScale).toHaveBeenCalledWith(PERCENTAGE);
		});

		it('should not call the scale service if the form is not valid', function() {
			// Set the form to be invalid
			scope.asForm.$valid = false;

			scope.saveScale(fakeEvent, PERCENTAGE);

			expect(scalesService.addScale.calls.count()).toEqual(0);
		});

		it('should handle any errors and not redirect if the save was not successful', function() {

			spyOn(location, 'path');

			scope.saveScale(fakeEvent, PERCENTAGE);

			deferredAddScale.reject();
			scope.$apply();

			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
			expect(location.path.calls.count()).toEqual(0);
		});

		it('should redirect to /scales after a successful save, if no variable is currently being edited', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(location, 'path');

			scope.saveScale(fakeEvent, PERCENTAGE);

			deferredAddScale.resolve({id: '1'});
			scope.$apply();

			expect(location.path).toHaveBeenCalledWith('/scales');
		});

		it('should set the scale on the variable and go back to add variable after a successful save, if a variable is being edited',
			function() {

				var deferredSetScale;

				variableStateService.setScale = function() {
					deferredSetScale = q.defer();
					return deferredSetScale.promise;
				};

				// Variable edit is in progress
				spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
				spyOn(variableStateService, 'setScale').and.callThrough();
				spyOn(location, 'path');

				// Successful save
				scope.saveScale(fakeEvent, PERCENTAGE);
				deferredAddScale.resolve({id: '1'});
				scope.$apply();

				// Successfully set the scale
				deferredSetScale.resolve();
				scope.$apply();

				expect(variableStateService.setScale).toHaveBeenCalledWith(PERCENTAGE.id, PERCENTAGE.name);
				expect(location.path).toHaveBeenCalledWith('/add/variable');
			}
		);
	});

	describe('$scope.cancel', function() {

		it('should call the cancel handler', function() {
			scope.asForm = {
				$dirty: true,
				scale: {
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

		it('should set the path to the scales list if there is no variable add in progress', function() {
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(formUtilities, 'cancelAddHandler');

			scope.cancel(fakeEvent);
			expect(formUtilities.cancelAddHandler).toHaveBeenCalledWith(scope, false, '/scales');
		});
	});

	describe('scaleFormService', function() {

		describe('formEmpty', function() {

			it('should return false if the name or description are present', function() {
				var name = {
						name: 'name'
					},
					description = {
						description: 'description'
					};

				expect(scaleFormService.formEmpty(name)).toBe(false);
				expect(scaleFormService.formEmpty(description)).toBe(false);
			});

			it('should return false if the dataType is present', function() {
				var dataType = {
						dataType: 'a type'
					};

				expect(scaleFormService.formEmpty(dataType)).toBe(false);
			});

			it('should return true if no fields are valued', function() {
				var model = {};

				expect(scaleFormService.formEmpty(model)).toBe(true);
			});
		});
	});
});
