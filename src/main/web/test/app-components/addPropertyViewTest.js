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

		propertiesService,

		variableStateService = {
			updateInProgress: function() {},
			setProperty: function() {}
		},

		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},

		q,
		deferredAddProperty,
		controller,
		location,
		scope,
		window;

	beforeEach(function() {
		module('addProperty');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window) {

		var deferredGetClasses;

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;

		propertiesService = {
			getClasses: function() {
				deferredGetClasses = q.defer();
				return deferredGetClasses.promise;
			},
			addProperty: function() {
				deferredAddProperty = q.defer();
				return deferredAddProperty.promise;
			}
		};

		spyOn(propertiesService, 'addProperty').and.callThrough();
		spyOn(propertiesService, 'getClasses').and.callThrough();
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');

		controller = $controller('AddPropertyController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			propertiesService: propertiesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		deferredGetClasses.resolve(classes);
		scope.$apply();
	}));

	it('should set the classes on the scope', function() {
		expect(scope.classes).toEqual(classes);
	});

	describe('$scope.saveProperty', function() {

		it('should call the properties service to save the property', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);

			scope.saveProperty(fakeEvent, BLAST);

			expect(propertiesService.addProperty).toHaveBeenCalledWith(BLAST);
		});

		it('should handle any errors and not redirect if the save was not successful', function() {

			spyOn(location, 'path');

			scope.saveProperty(fakeEvent, BLAST);

			deferredAddProperty.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
			expect(location.path.calls.count()).toEqual(0);
		});

		it('should redirect to /properties after a successful save, if no variable is currently being edited', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(location, 'path');

			scope.saveProperty(fakeEvent, BLAST);
			deferredAddProperty.resolve({id: 45});
			scope.$apply();

			expect(location.path).toHaveBeenCalledWith('/properties');
		});

		it('should set the property on the variable and redirect to the previous screen if one is currently being edited', function() {

			var deferredSetProperty;

			variableStateService.setProperty = function() {
				deferredSetProperty = q.defer();
				return deferredSetProperty.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'setProperty').and.callThrough();
			spyOn(window.history, 'back');

			// Successful save
			scope.saveProperty(fakeEvent, BLAST);
			deferredAddProperty.resolve({id: 45});
			scope.$apply();

			// Successfully set the property
			deferredSetProperty.resolve();
			scope.$apply();

			expect(variableStateService.setProperty).toHaveBeenCalledWith(BLAST.id, BLAST.name);
			expect(window.history.back).toHaveBeenCalled();
		});
	});
});
