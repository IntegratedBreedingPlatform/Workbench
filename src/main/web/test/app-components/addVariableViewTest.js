/*global expect, inject, spyOn*/
'use strict';

describe('Add Variable View', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},

		properties = [{
			id: '51475',
			name: 'Leaf length',
			description: 'Measurement of leaf length in centimeter.'
		}, {
			id: '51476',
			name: 'Leaf width',
			description: 'Measurement of leaf width in centimeter.'
		}],

		methods = [{
			id: '51081',
			name: 'Astri counting',
			description: 'Count the number of plants that are affected by black bundle per plot and record data in the FieldBook/FieldLog.'
		}, {
			id: '4130',
			name: 'Average',
			description: 'Average of values'
		}],

		scales = [{
			id: '6067',
			name: 'Day (dd)',
			description: 'Day (dd)'
		}],

		variableTypes = [{
			id: 1,
			name: 'Analysis',
			description: 'Variable to be used only in analysis (for example derived variables).'
		}, {
			id: 6,
			name: 'Environment Detail',
			description: 'Administrative details to be tracked per environment.'
		}],

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
			serverErrorHandler: function() {
				return {};
			}
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

		VARIABLE_TYPES_INC_TREATMENT_FACTOR = [{
			id: 0,
			name: 'Trait',
			description: 'Characteristics of a germplasm to be recorded during a study.'
		}, {
			id: 9,
			name: 'Treatment Factor',
			description: 'Treatments to be applied to members of a trial.'
		}],

		VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR = [{
			id: 0,
			name: 'Trait',
			description: 'Characteristics of a germplasm to be recorded during a study.'
		}],

		SOME_LISTS_NOT_LOADED = 'validation.variable.someListsNotLoaded',

		deferredAddVariable,
		deferredGetProperties,
		deferredGetMethods,
		deferredGetScalesWithNonSystemDataTypes,
		deferredGetVariablesTypes,

		q,
		location,
		scope,
		controllerFn,

		controller,
		variableFormService;

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

		propertiesService.getProperties = function() {
			deferredGetProperties = q.defer();
			return deferredGetProperties.promise;
		};
		methodsService.getMethods = function() {
			deferredGetMethods = q.defer();
			return deferredGetMethods.promise;
		};
		scalesService.getScalesWithNonSystemDataTypes = function() {
			deferredGetScalesWithNonSystemDataTypes = q.defer();
			return deferredGetScalesWithNonSystemDataTypes.promise;
		};
		variableTypesService.getTypes = function() {
			deferredGetVariablesTypes = q.defer();
			return deferredGetVariablesTypes.promise;
		};

		// We want a little more control over when this gets resolved
		variablesService.addVariable = function() {
			deferredAddVariable = q.defer();
			return deferredAddVariable.promise;
		};

		// Pretend our form is valid
		scope.avForm = {
			$valid: true,
			$setUntouched: function() {}
		};

		variableFormService = _variableFormService_;

		spyOn(variableStateService, 'reset');
		spyOn(variableStateService, 'storeVariableState');

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScalesWithNonSystemDataTypes').and.callThrough();

		spyOn(variablesService, 'addVariable').and.callThrough();
		spyOn(variableTypesService, 'getTypes').and.callThrough();

		spyOn(location, 'path');
		spyOn(serviceUtilities, 'serverErrorHandler').and.callThrough();
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
			expect(scalesService.getScalesWithNonSystemDataTypes.calls.count()).toEqual(0);
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
			expect(scalesService.getScalesWithNonSystemDataTypes).toHaveBeenCalled();
			expect(variableTypesService.getTypes).toHaveBeenCalled();
		});

		it('should set the properties on the scope', function() {
			deferredGetProperties.resolve(properties);
			scope.$apply();
			expect(scope.data.properties).toEqual(properties);
		});

		it('should display errors if properties were not retrieved successfully', function() {
			deferredGetProperties.reject();
			scope.$apply();
			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
			expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
		});

		it('should set the methods on the scope', function() {
			deferredGetMethods.resolve(methods);
			scope.$apply();
			expect(scope.data.methods).toEqual(methods);
		});

		it('should display errors if methods were not retrieved successfully', function() {
			deferredGetMethods.reject();
			scope.$apply();
			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
			expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
		});

		it('should set the scales on the scope', function() {
			deferredGetScalesWithNonSystemDataTypes.resolve(scales);
			scope.$apply();
			expect(scope.data.scales).toEqual(scales);
		});

		it('should display errors if scales were not retrieved successfully', function() {
			deferredGetScalesWithNonSystemDataTypes.reject();
			scope.$apply();
			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
			expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
		});

		it('should set the variable types on the scope', function() {
			deferredGetVariablesTypes.resolve(variableTypes);
			scope.$apply();
			expect(scope.data.types).toEqual(variableTypes);
		});

		it('should display errors if variable types were not retrieved successfully', function() {
			deferredGetVariablesTypes.reject();
			scope.$apply();
			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
			expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
		});
	});

	describe('$scope.saveVariable', function() {

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'updateInProgress').and.returnValue(false);
			compileController();
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

			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
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

			spyOn(formUtilities, 'cancelAddHandler');

			scope.cancel(fakeEvent);

			expect(formUtilities.cancelAddHandler).toHaveBeenCalled();
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

			it('should return false if the property, method or scale are present', function() {
				var property = {
						name: 'a property'
					},
					method = {
						name: 'a method'
					},
					scale = {
						name: 'a scale'
					};

				expect(variableFormService.formEmpty(property)).toBe(false);
				expect(variableFormService.formEmpty(method)).toBe(false);
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
