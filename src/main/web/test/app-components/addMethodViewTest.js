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

		methodService = {
			saveMethod: function() {}
		},

		variableStateService = {
			updateInProgress: function() {},
			setMethod: function() {}
		},

		q,
		controller,
		location,
		scope,
		window;

	beforeEach(function() {
		module('addMethod');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, $window) {

		spyOn(methodService, 'saveMethod');

		controller = $controller('AddMethodController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			methodService: methodService,
			variableStateService: variableStateService
		});

		q = $q;
		location = $location;
		scope = $rootScope;
		window = $window;
	}));

	describe('$scope.saveMethod', function() {

		it('should call the method service to save the method', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			expect(methodService.saveMethod).toHaveBeenCalledWith(CUT_AND_DRY);
		});

		it('should redirect to /methods after a successful save, if no variable is currently being edited', function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			spyOn(location, 'path');

			scope.saveMethod(fakeEvent, CUT_AND_DRY);

			expect(location.path).toHaveBeenCalledWith('/methods');
		});

		it('should set the method on the variable and redirect to the previous screen if one is currently being edited', function() {

			var deferred;

			variableStateService.setMethod = function() {
				deferred = q.defer();
				return deferred.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'setMethod').and.callThrough();
			spyOn(window.history, 'back');

			scope.saveMethod(fakeEvent, CUT_AND_DRY);
			deferred.resolve();
			scope.$apply();

			expect(variableStateService.setMethod).toHaveBeenCalledWith(CUT_AND_DRY.name);
			expect(window.history.back).toHaveBeenCalled();
		});

		it('should log an error if there is a problem setting the method on the variable being updated', function() {

			var deferred;

			variableStateService.setMethod = function() {
				deferred = q.defer();
				return deferred.promise;
			};

			// Variable edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);

			spyOn(controller, 'genericAndRatherUselessErrorHandler');

			scope.saveMethod(fakeEvent, CUT_AND_DRY);
			deferred.reject();
			scope.$apply();

			expect(controller.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});
	});
});
