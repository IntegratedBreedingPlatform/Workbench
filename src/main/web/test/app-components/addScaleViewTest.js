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

		scaleService = {
			saveScale: function() {}
		},

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
		deferred,
		controller,
		location,
		scope,
		window;

	beforeEach(function() {
		module('addScale');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window) {

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;

		dataTypesService.getDataTypes = function() {
			deferred = q.defer();
			return deferred.promise;
		};

		controller = $controller('AddScaleController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			scaleService: scaleService,
			dataTypesService: dataTypesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		spyOn(scaleService, 'saveScale');
		spyOn(dataTypesService, 'getDataTypes').and.callThrough();

		deferred.resolve(types);
		scope.$apply();
	}));

	it('should set the data types on the scope', function() {
		expect(scope.types).toEqual(types);
	});

	it('should hide the range and categories widgets by default', function() {
		expect(scope.showRangeWidget).toBe(false);
		expect(scope.showCategoriesWidget).toBe(false);
	});

	it('should initialise the scale model with a categories property set to an array with one empty object', function() {
		var scale = scope.scale,
			categories;

		expect(typeof scale).toEqual('object');

		categories = scale.categories;

		expect(categories).not.toBeUndefined();
		expect(Array.isArray(categories)).toBe(true);
		expect(categories[0]).toEqual({});
	});

	it('should show the range widget and hide the categories widget if the scale type is changed to be Numeric', function() {
		scope.scale.type = NUMERIC_TYPE;
		scope.$apply();
		expect(scope.showRangeWidget).toBe(true);
		expect(scope.showCategoriesWidget).toBe(false);
	});

	it('should show the categories widget and hide the range widget if the scale type is changed to be Categorical', function() {
		scope.scale.type = CATEGORICAL_TYPE;
		scope.$apply();
		expect(scope.showRangeWidget).toBe(false);
		expect(scope.showCategoriesWidget).toBe(true);
	});

	it('should hide the categories and range widgets if the scale type is changed to be something other than Categorical or Numeric',
		function() {
		scope.scale.type = CHARACTER_TYPE;
		scope.$apply();
		expect(scope.showRangeWidget).toBe(false);
		expect(scope.showCategoriesWidget).toBe(false);
	});

	describe('$scope.addCategory', function() {

		it('should add an empty category to the categories array on the scale object', function() {

			expect(scope.scale.categories.length).toEqual(1);
			expect(scope.scale.categories[0]).toEqual({});

			scope.addCategory();

			expect(scope.scale.categories.length).toEqual(2);
			expect(scope.scale.categories[1]).toEqual({});
		});
	});

	describe('$scope.saveScale', function() {

		it('should call the scale service to save the scale', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);

			scope.saveScale(fakeEvent, PERCENTAGE);

			expect(scaleService.saveScale).toHaveBeenCalledWith(PERCENTAGE);
		});

		it('should redirect to /scales after a successful save, if no variable is currently being edited', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(location, 'path');

			scope.saveScale(fakeEvent, PERCENTAGE);

			expect(location.path).toHaveBeenCalledWith('/scales');
		});

		it('should set the scale on the variable and redirect to the previous screen if one is currently being edited', function() {

			var deferred;

			variableStateService.setScale = function() {
				deferred = q.defer();
				return deferred.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'setScale').and.callThrough();
			spyOn(window.history, 'back');

			scope.saveScale(fakeEvent, PERCENTAGE);
			deferred.resolve();
			scope.$apply();

			expect(variableStateService.setScale).toHaveBeenCalledWith(PERCENTAGE.name);
			expect(window.history.back).toHaveBeenCalled();
		});

		it('should log an error if there is a problem setting the scale on the variable being updated', function() {

			var deferred;

			variableStateService.setScale = function() {
				deferred = q.defer();
				return deferred.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);

			spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');

			scope.saveScale(fakeEvent, PERCENTAGE);
			deferred.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});
	});
});
