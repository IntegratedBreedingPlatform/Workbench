/*global expect, inject, spyOn*/
'use strict';

describe('Add Variable View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			description: 'A little vigourous',
			propertyId: 1,
			methodId: 1,
			scaleId: 1,
			variableTypes: [{
				id: 1,
				name: 'Analysis',
				description: ''
			}]
		},

		variableTypesService = {},
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

		formUtilities,

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

		VARIABLE_TYPES_INC_TREATMENT_FACTOR = [
		{
			id: 0,
			name: 'Trait',
			description: 'Characteristics of a germplasm to be recorded during a study.'
		},
		{
			id: 1,
			name: 'Treatment Factor',
			description: 'Treatments to be applied to members of a trial.'
		}
		],

		VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR = [
		{
			id: 0,
			name: 'Trait',
			description: 'Characteristics of a germplasm to be recorded during a study.'
		}
		],

		deferred = [],
		deferredAddVariable,

		q,
		location,
		scope,
		controllerFn,

		controller,
		variableFormService;

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
			variableTypesService: variableTypesService,
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

	beforeEach(inject(function($q, $rootScope, $location, $controller, _formUtilities_, _variableFormService_) {
		q = $q;
		location = $location;
		scope = $rootScope;
		controllerFn = $controller;
		formUtilities = _formUtilities_;

		propertiesService.getProperties = fakePromise();
		methodsService.getMethods = fakePromise();
		scalesService.getScales = fakePromise();
		variableTypesService.getTypes = fakePromise();

		// We want a little more control over when this gets resolved
		variablesService.addVariable = function() {
			deferredAddVariable = q.defer();
			return deferredAddVariable.promise;
		};

		variableFormService = _variableFormService_;

		spyOn(variableStateService, 'reset');
		spyOn(variableStateService, 'storeVariableState');

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScales').and.callThrough();

		spyOn(variablesService, 'addVariable').and.callThrough();
		spyOn(variableTypesService, 'getTypes').and.callThrough();

		spyOn(location, 'path');
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');
	}));

	describe('by default', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
		});

		it('should hide the range widget by default', function() {
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

		it('should hide the Treatment Factor warning message by default', function() {
			expect(scope.showTreatmentFactorAlert).toBeFalsy();
		});

		it('should show the Treatment Factor warning message if the variable types include Treatment Factor', function() {
			scope.variable.variableTypes = VARIABLE_TYPES_INC_TREATMENT_FACTOR;
			scope.$apply();

			expect(scope.showTreatmentFactorAlert).toBe(true);
		});

		it('should hide the Treatment Factor warning message if the variable types do not include Treatment Factor', function() {
			scope.variable.variableTypes = VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR;
			scope.$apply();

			expect(scope.showTreatmentFactorAlert).toBe(false);
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
			expect(variableTypesService.getTypes.calls.count()).toEqual(0);
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
			expect(variableTypesService.getTypes).toHaveBeenCalled();
		});
	});

	describe('$scope.saveVariable', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();

			// Set the form to be valid
			scope.avForm = {
				$valid: true
			};
		});

		it('should call the variables service to save the variable', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);
			expect(variablesService.addVariable).toHaveBeenCalledWith(PLANT_VIGOR);
		});

		it('should not call the variables service if the form is not valid', function() {
			// Set the form to be invalid
			scope.avForm.$valid = false;
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			expect(variablesService.addVariable.calls.count()).toEqual(0);
		});

		it('should handle any errors and not redirect if the save was not successful', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			deferredAddVariable.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
			expect(location.path.calls.count()).toEqual(0);
		});

		it('should reset the state of any stored variable after a successful save', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			deferredAddVariable.resolve();
			scope.$apply();

			expect(variableStateService.reset).toHaveBeenCalled();
		});

		it('should redirect to /variables after a successful save', function() {
			scope.saveVariable(fakeEvent, PLANT_VIGOR);

			deferredAddVariable.resolve();
			scope.$apply();

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

		it('should redirect to /variables after a successful state save', function() {
			var path = 'path';

			scope.addNew(fakeEvent, path);
			expect(location.path).toHaveBeenCalledWith('/add/' + path);
		});
	});

	describe('$scope.cancel', function() {

		beforeEach(function() {
			compileController();
		});

		it('should call the cancel handler', function() {
			scope.avForm = {
				$dirty: true,
				variable: {
					name: 'Name'
				}
			};

			spyOn(formUtilities, 'cancelHandler');

			scope.cancel(fakeEvent);

			expect(formUtilities.cancelHandler).toHaveBeenCalled();
		});
	});

	describe('variableFormService', function() {

		describe('formEmpty', function() {

			it('should return false if the name or description are present', function() {
				var name = {
						name: 'name'
					},
					description = {
						description: 'description'
					};

				expect(variableFormService.formEmpty(name)).toBe(false);
				expect(variableFormService.formEmpty(description)).toBe(false);
			});

			it('should return false if the propertySummary, methodSummary or scale are present', function() {
				var propertySummary = {
						name: 'a property'
					},
					methodSummary = {
						name: 'a method'
					},
					scale = {
						name: 'a scale'
					};

				expect(variableFormService.formEmpty(propertySummary)).toBe(false);
				expect(variableFormService.formEmpty(methodSummary)).toBe(false);
				expect(variableFormService.formEmpty(scale)).toBe(false);
			});

			it('should return false if at least one variable type is present', function() {
				var model = {
						variableTypes: ['type']
					};

				expect(variableFormService.formEmpty(model)).toBe(false);
			});

			it('should return true if no fields are valued', function() {
				var model = {};

				expect(variableFormService.formEmpty(model)).toBe(true);
			});
		});
	});
});
