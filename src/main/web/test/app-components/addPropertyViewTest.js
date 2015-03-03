/*global expect, inject, spyOn*/
'use strict';

describe('Add Property View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		classes = ['class', 'anotherClass'],

		BLAST = {
			name: 'Blast',
			description: 'I\'ts a blast',
			classes: classes
		},

		propertyService = {
			saveProperty: function() {}
		},

		propertiesService = {
			getClasses: function() {}
		},

		variableStateService = {
			updateInProgress: function() {},
			setProperty: function() {}
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
		module('addProperty');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window) {

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;

		propertiesService.getClasses = function() {
			deferred = q.defer();
			return deferred.promise;
		};

		controller = $controller('AddPropertyController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			propertyService: propertyService,
			propertiesService: propertiesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		spyOn(propertyService, 'saveProperty');
		spyOn(propertiesService, 'getClasses').and.callThrough();

		deferred.resolve(classes);
		scope.$apply();
	}));

	it('should set the classes on the scope', function() {
		expect(scope.classes).toEqual(classes);
	});

	describe('$scope.saveProperty', function() {

		it('should call the property service to save the property', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);

			scope.saveProperty(fakeEvent, BLAST);

			expect(propertyService.saveProperty).toHaveBeenCalledWith(BLAST);
		});

		it('should redirect to /properties after a successful save, if no variable is currently being edited', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(location, 'path');

			scope.saveProperty(fakeEvent, BLAST);

			expect(location.path).toHaveBeenCalledWith('/properties');
		});

		it('should set the property on the variable and redirect to the previous screen if one is currently being edited', function() {

			var deferred;

			variableStateService.setProperty = function() {
				deferred = q.defer();
				return deferred.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'setProperty').and.callThrough();
			spyOn(window.history, 'back');

			scope.saveProperty(fakeEvent, BLAST);
			deferred.resolve();
			scope.$apply();

			expect(variableStateService.setProperty).toHaveBeenCalledWith(BLAST.name);
			expect(window.history.back).toHaveBeenCalled();
		});

		it('should log an error if there is a problem setting the property on the variable being updated', function() {

			var deferred;

			variableStateService.setProperty = function() {
				deferred = q.defer();
				return deferred.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);

			spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');

			scope.saveProperty(fakeEvent, BLAST);
			deferred.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});
	});
});
