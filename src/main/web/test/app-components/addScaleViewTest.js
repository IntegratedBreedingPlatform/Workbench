/*global expect, inject, spyOn*/
'use strict';

describe('Add Scale View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		CATEGORICAL_TYPE = {
			id: 1,
			name: 'Categorical'
		},

		NUMERIC_TYPE = {
			id: 2,
			name: 'Numeric'
		},

		CHARACTER_TYPE = {
			id: 3,
			name: 'Character'
		},

		types = [CATEGORICAL_TYPE, NUMERIC_TYPE, CHARACTER_TYPE],

		PERCENTAGE = {
			name: 'Percentage',
			description: 'As per title',
			dataType: NUMERIC_TYPE,
			validValues: {
				min: 0,
				max: 100
			}
		},

		scalesService,

		dataTypesService = {
			getDataTypes: function() {}
		},

		variableStateService = {
			updateInProgress: function() {},
			setScale: function() {}
		},

		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},

		q,
		controller,
		location,
		scope,
		window,

		deferredGetDataTypes,
		deferredAddScale;

	beforeEach(function() {
		module('addScale');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window) {

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;

		dataTypesService.getDataTypes = function() {
			deferredGetDataTypes = q.defer();
			return deferredGetDataTypes.promise;
		};

		scalesService = {
			addScale: function() {
				deferredAddScale = q.defer();
				return deferredAddScale.promise;
			}
		};

		// Pretend our form is valid
		scope.asForm = {
			$valid: true
		};

		spyOn(scalesService, 'addScale').and.callThrough();
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');
		spyOn(dataTypesService, 'getDataTypes').and.callThrough();

		controller = $controller('AddScaleController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			scalesService: scalesService,
			dataTypesService: dataTypesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		deferredGetDataTypes.resolve(types);
		scope.$apply();
	}));

	it('should set the data types on the scope', function() {
		expect(scope.types).toEqual(types);
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

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
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

		it('should set the scale on the variable and go back after a successful save, if a variable is being edited', function() {

			var deferredSetScale;

			variableStateService.setScale = function() {
				deferredSetScale = q.defer();
				return deferredSetScale.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'setScale').and.callThrough();
			spyOn(window.history, 'back');

			// Successful save
			scope.saveScale(fakeEvent, PERCENTAGE);
			deferredAddScale.resolve({id: '1'});
			scope.$apply();

			// Successfully set the scale
			deferredSetScale.resolve();
			scope.$apply();

			expect(variableStateService.setScale).toHaveBeenCalledWith(PERCENTAGE.id, PERCENTAGE.name);
			expect(window.history.back).toHaveBeenCalled();
		});
	});
});
