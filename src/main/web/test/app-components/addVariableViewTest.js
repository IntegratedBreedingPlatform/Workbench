/*global expect, inject, spyOn*/
'use strict';

describe('Add Variable View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		variableService = {
			saveVariable: function() {}
		},

		variablesService = {},
		propertiesService = {},
		methodsService = {},
		scalesService = {},

		variableStateService = {
			updateInProgress: function() {},
			getVariableState: function() {},
			storeVariableState: function() {},
			reset: function() {}
		},

		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},

		PERCENTAGE = {
			name: 'Percentage',
			dataType: {
				id: 2,
				name: 'Numeric'
			}
		},

		CATEGORICAL = {
			name: 'Categorical Scale',
			dataType: {
				id: 1,
				name: 'Categorical'
			}
		},

		deferred = [],

		q,
		location,
		scope,
		controllerFn,

		controller;

	function fakePromise() {
		return function() {
			var defer = q.defer();
			deferred.push(defer);
			return defer.promise;
		};
	}

	function compileController() {
		controller = controllerFn('AddVariableController', {
			$scope: scope,
			$location: location,
			variableService: variableService,
			variablesService: variablesService,
			propertiesService: propertiesService,
			methodsService: methodsService,
			scalesService: scalesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		deferred.forEach(function(d) {
			d.resolve();
		});

		scope.$apply();
	}

	beforeEach(function() {
		module('addVariable');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller) {
		q = $q;
		location = $location;
		scope = $rootScope;
		controllerFn = $controller;

		propertiesService.getProperties = fakePromise();
		methodsService.getMethods = fakePromise();
		scalesService.getScales = fakePromise();
		variablesService.getTypes = fakePromise();

		spyOn(variableService, 'saveVariable');

		spyOn(variableStateService, 'reset');
		spyOn(variableStateService, 'storeVariableState');

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScales').and.callThrough();
		spyOn(variablesService, 'getTypes').and.callThrough();

		spyOn(location, 'path');
	}));

	describe('by default', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should hide the range widget by deafult', function() {
			expect(scope.showRangeWidget).toBe(false);
		});

		it('should show the range widget if the variable changes to have a Numeric data type', function() {
			scope.variable.scale = PERCENTAGE;
			scope.$apply();

			expect(scope.showRangeWidget).toBe(true);
		});

		it('should hide the range widget if the variable changes to have a non Numeric data type', function() {
			scope.variable.scale = CATEGORICAL;
			scope.$apply();

			expect(scope.showRangeWidget).toBe(false);
		});
	});

	describe('when a variable update is in progress', function() {

		var state = {
			variable: 'variable',
			scopeData: 'data'
		};

		beforeEach(function() {
			// Pretend an edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(true);
			spyOn(variableStateService, 'getVariableState').and.returnValue(state);

			compileController();
		});

		it('should set the variable and data properties on the $scope', function() {
			expect(scope.variable).toEqual(state.variable);
			expect(scope.scopeData).toEqual(state.data);
		});

		it('should not get property, method, scale or variable type data from services', function() {
			expect(propertiesService.getProperties.calls.count()).toEqual(0);
			expect(methodsService.getMethods.calls.count()).toEqual(0);
			expect(scalesService.getScales.calls.count()).toEqual(0);
			expect(variablesService.getTypes.calls.count()).toEqual(0);
		});
	});

	describe('when a variable update is not in progress', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should get property, method, scale or variable type data from services', function() {
			expect(propertiesService.getProperties).toHaveBeenCalled();
			expect(methodsService.getMethods).toHaveBeenCalled();
			expect(scalesService.getScales).toHaveBeenCalled();
			expect(variablesService.getTypes).toHaveBeenCalled();
		});
	});

	describe('$scope.saveVariable', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should save the variable to the variableService', function() {
			var variable = {
				name: 'My variable'
			};
			scope.saveVariable(fakeEvent, variable);
			expect(variableService.saveVariable).toHaveBeenCalledWith(variable);
		});

		it('should reset the state of any stored variable', function() {
			scope.saveVariable(fakeEvent, {});
			expect(variableStateService.reset).toHaveBeenCalled();
		});

		it('should redirect to /variables after a successful save', function() {
			scope.saveVariable(fakeEvent, {});
			expect(location.path).toHaveBeenCalledWith('/variables');
		});
	});

	describe('$scope.addNew', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should store the variable state and data from the scope', function() {
			var variable = {
				name: 'variable'
			},
			data = {
				someData: {}
			};

			scope.variable = variable;
			scope.data = data;

			scope.addNew(fakeEvent, '');
			expect(variableStateService.storeVariableState).toHaveBeenCalledWith(variable, data);
		});

		it('should redirect to /variables after a successful save', function() {
			var path = 'path';

			scope.addNew(fakeEvent, path);
			expect(location.path).toHaveBeenCalledWith('/add/' + path);
		});
	});
});
