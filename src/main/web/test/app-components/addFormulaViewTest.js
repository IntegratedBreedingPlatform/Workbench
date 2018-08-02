/*global expect, inject, spyOn*/
'use strict';

describe('Add Formula View', function() {
	var fakeEvent = {
			preventDefault: function () {
			}
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
			}],
			formula: {
				"definition": "{{PLANT_VIGOR}} * 2",
				"target": {
					id: 1
				},
				"description": "",
				"name": "",
				"active": true,
				"formulaId": 0
			}
		},
		variablesService = {},

		variableStateService = {
			updateInProgress: function () {
			},
			getVariableState: function () {
			},
			storeVariableState: function () {
			},
			reset: function () {
			}
		},

		serviceUtilities = {
			serverErrorHandler: function () {
				return {};
			}
		},

		formUtilities,


		deferredAddFormula,

		q,
		location,
		scope,
		controllerFn,

		controller,
		variableFormService;

	function compileController() {
		controller = controllerFn('AddFormulaController', {
			$scope: scope,
			$location: location,
			variablesService: variablesService,
			variableStateService: variableStateService,
			serviceUtilities: serviceUtilities
		});

		scope.$apply();
	}

	beforeEach(function() {
		module('addFormula');
	});

	beforeEach(inject(function($q, $rootScope, $location, $controller, _formUtilities_, _variableFormService_) {
		q = $q;
		location = $location;
		scope = $rootScope;
		controllerFn = $controller;
		formUtilities = _formUtilities_;


		// We want a little more control over when this gets resolved
		variablesService.addFormula = function() {
			deferredAddFormula = q.defer();
			return deferredAddFormula.promise;
		};

		// Pretend our form is valid
		scope.avForm = {
			$valid: true,
			$setUntouched: function() {}
		};

		variableFormService = _variableFormService_;

		spyOn(variableStateService, 'reset');
		spyOn(variableStateService, 'storeVariableState');

		spyOn(variablesService, 'addFormula').and.callThrough();

		spyOn(location, 'path');
		spyOn(serviceUtilities, 'serverErrorHandler').and.callThrough();
	}));

	describe('by default', function() {

		var state = {
			variable: 'variable',
			scopeData: 'data'
		};

		beforeEach(function() {
			// Pretend no edit is in progress
			spyOn(variableStateService, 'getVariableState').and.returnValue(state);
			compileController();
		});

		/*it('should hide the range widget by default', function() {
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
		});*/
	});
});
