/*global angular, expect, inject, spyOn*/
'use strict';

describe('Variable details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},
		panelService = {
			hidePanel: function() {}
		},
		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},

		PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			editableFields: [ 'description','cropOntologyId', 'variableTypeIds' ]
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
		propertiesService = {},
		methodsService = {},
		scalesService = {},
		variableTypesService = {},
		variablesService = {},
		formUtilities,
		scope,
		q,
		directiveElement,
		deferredGetProperties,
		deferredGetMethods,
		deferredGetScales,
		deferredGetTypes,
		deferredUpdateVariable,
		deferredDeleteVariable,
		mockTranslateFilter;

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};
	});

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-variable-details></om-variable-details>')(scope);
		});
		scope.$digest();
	}

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));

	beforeEach(module('variableDetails', function($provide) {
		// Provide mocks for the directive controller
		$provide.value('propertiesService', propertiesService);
		$provide.value('methodsService', methodsService);
		$provide.value('scalesService', scalesService);
		$provide.value('serviceUtilities', serviceUtilities);
		$provide.value('variablesService', variablesService);
		$provide.value('variableTypesService', variableTypesService);
		$provide.value('panelService', panelService);
	}));

	beforeEach(inject(function($rootScope, $q, _formUtilities_) {
		q = $q;
		scope = $rootScope;
		formUtilities = _formUtilities_;

		propertiesService.getProperties = function() {
			deferredGetProperties = q.defer();
			return deferredGetProperties.promise;
		};

		methodsService.getMethods = function() {
			deferredGetMethods = q.defer();
			return deferredGetMethods.promise;
		};

		scalesService.getScales = function() {
			deferredGetScales = q.defer();
			return deferredGetScales.promise;
		};

		variableTypesService.getTypes = function() {
			deferredGetTypes = q.defer();
			return deferredGetTypes.promise;
		};

		variablesService.updateVariable = function() {
			deferredUpdateVariable = q.defer();
			return deferredUpdateVariable.promise;
		};

		variablesService.deleteVariable = function() {
			deferredDeleteVariable = q.defer();
			return deferredDeleteVariable.promise;
		};

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScales').and.callThrough();
		spyOn(variableTypesService, 'getTypes').and.callThrough();
		spyOn(variablesService, 'updateVariable').and.callThrough();
		spyOn(variablesService, 'deleteVariable').and.callThrough();
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');
		spyOn(panelService, 'hidePanel');

		compileDirective();
	}));

	describe('by default', function() {

		it('should set editing to false', function() {
			expect(scope.editing).toBe(false);
		});

		it('should set data to have an empty array of properties, methods, scales and types', function() {
			expect(scope.data).toEqual({
				properties: [],
				methods: [],
				scales: [],
				types: []
			});
		});

		it('should set the model to be the selected variable if the selected variable changes', function() {
			scope.selectedVariable = PLANT_VIGOR;
			scope.$apply();
			expect(scope.model).toEqual(PLANT_VIGOR);
		});

		it('should set the variable id to be the id of the selected item if the selected variable changes', function() {
			scope.selectedItem = PLANT_VIGOR;
			scope.$apply();
			expect(scope.variableId).toEqual(PLANT_VIGOR.id);
		});

		it('should set the variable id to be null if the selected variable changes and has no id', function() {
			scope.selectedItem = {};
			scope.$apply();
			expect(scope.variableId).toEqual(null);
		});

		it('should set the variable id to be null if the selected variable changes to a falsey value', function() {
			scope.selectedItem = null;
			scope.$apply();
			expect(scope.variableId).toEqual(null);
		});

		it('should hide the Treatment Factor warning message by default', function() {
			expect(scope.showTreatmentFactorAlert).toBeFalsy();
		});

		it('should show the Treatment Factor warning message if the variable types include Treatment Factor', function() {
			scope.model = {};
			scope.model.variableTypes = VARIABLE_TYPES_INC_TREATMENT_FACTOR;
			scope.$apply();

			expect(scope.showTreatmentFactorAlert).toBe(true);
		});

		it('should hide the Treatment Factor warning message if the variable types do not include Treatment Factor', function() {
			scope.model = {};
			scope.model.variableTypes = VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR;
			scope.$apply();

			expect(scope.showTreatmentFactorAlert).toBe(false);
		});

		it('should show non-editable fields alert if the selected item does not have all fields in editable fields list', function() {
			scope.selectedVariable = PLANT_VIGOR;
			scope.$apply();
			scope.editing = true;
			scope.$apply();
			expect(scope.showNoneditableFieldsAlert).toEqual(true);
		});
	});

	describe('getting properties', function() {
		it('should call the properties service to get all properties', function() {
			expect(propertiesService.getProperties).toHaveBeenCalled();
		});

		it('should handle any errors if retrieving the properties was not successful', function() {
			deferredGetProperties.reject();
			scope.$apply();
			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set data.properties to the returned properties after a successful update', function() {
			deferredGetProperties.resolve([PLANT_VIGOR]);
			scope.$apply();

			expect(scope.data.properties).toEqual([PLANT_VIGOR]);
		});
	});

	describe('getting methods', function() {
		it('should call the methods service to get all methods', function() {
			expect(methodsService.getMethods).toHaveBeenCalled();
		});

		it('should handle any errors if retrieving the methods was not successful', function() {
			deferredGetMethods.reject();
			scope.$apply();
			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set data.methods to the returned methods after a successful update', function() {
			deferredGetMethods.resolve([PLANT_VIGOR]);
			scope.$apply();

			expect(scope.data.methods).toEqual([PLANT_VIGOR]);
		});
	});

	describe('getting scales', function() {
		it('should call the scales service to get all scales', function() {
			expect(scalesService.getScales).toHaveBeenCalled();
		});

		it('should handle any errors if retrieving the scales was not successful', function() {
			deferredGetScales.reject();
			scope.$apply();
			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set data.scales to the returned scales after a successful update', function() {
			deferredGetScales.resolve([PLANT_VIGOR]);
			scope.$apply();

			expect(scope.data.scales).toEqual([PLANT_VIGOR]);
		});
	});

	describe('getting variable types', function() {
		it('should call the variables service to get all variable types', function() {
			expect(variableTypesService.getTypes).toHaveBeenCalled();
		});

		it('should handle any errors if retrieving the variable types was not successful', function() {
			deferredGetTypes.reject();
			scope.$apply();
			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set data.types to the returned variable types after a successful update', function() {
			deferredGetTypes.resolve([1, 2]);
			scope.$apply();

			expect(scope.data.types).toEqual([1, 2]);
		});
	});

	describe('$scope.hideAlias', function() {

		it('should return true when the name is editable', function() {
			scope.model = {
				editableFields: ['name']
			};
			expect(scope.hideAlias()).toBe(true);
		});

		it('should return false when the name is not editable', function() {
			scope.model = {
				editableFields: ['description']
			};
			expect(scope.hideAlias()).toBe(false);
		});

		it('should return false when there is no model', function() {
			scope.model = null;
			expect(scope.hideAlias()).toBeFalsy();
		});

		it('should return false when the model has no editable fields', function() {
			scope.model = {};
			expect(scope.hideAlias()).toBeFalsy();
		});

	});

	describe('$scope.editVariable', function() {

		it('should set editing to be true', function() {
			scope.editing = false;
			scope.editVariable(fakeEvent);
			expect(scope.editing).toBe(true);
		});
	});

	describe('$scope.cancel', function() {

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should set editing to false if the user has not made any edits', function() {
			scope.selectedVariable = {
				name: 'variable'
			};
			scope.model = angular.copy(scope.selectedVariable);
			scope.editing = true;

			scope.cancel(fakeEvent);

			expect(scope.editing).toBe(false);
		});

		it('should call the confirmation handler if the user has made edits', function() {
			scope.selectedVariable = {
				name: 'variable'
			};
			scope.model = {
				name: 'new_variable_name'
			};

			scope.cancel(fakeEvent);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should set editing to false and reset the model when the confirmation handler is resolved', function() {
			scope.selectedVariable = {
				name: 'variable'
			};
			scope.model = {
				name: 'new_variable_name'
			};

			scope.cancel(fakeEvent);
			confirmation.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
			expect(scope.model).toEqual(scope.selectedVariable);
		});
	});

	describe('$scope.saveChanges', function() {

		var timeout;

		beforeEach(inject(function($timeout) {
			timeout = $timeout;
			scope.updateSelectedVariable = function(/*model*/) {};
			scope.vdForm.$valid = true;
		}));

		it('should call the variables service to update the variable', function() {
			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);
			expect(variablesService.updateVariable).toHaveBeenCalledWith(PLANT_VIGOR.id, PLANT_VIGOR);
		});

		it('should not call the variables service if the form is not valid', function() {
			// Set the form to be invalid
			scope.vdForm.$valid = false;
			scope.saveChanges(fakeEvent, PLANT_VIGOR);

			expect(variablesService.updateVariable.calls.count()).toEqual(0);
		});

		it('should show the throbber if the form is valid and submitted', function() {
			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);
			timeout.flush();
			expect(scope.showThrobber).toBe(true);
		});

		it('should not show the throbber if the form is not in a submitted state', function() {
			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);
			scope.submitted = false;
			timeout.flush();
			expect(scope.showThrobber).toBeFalsy();
		});

		it('should handle any errors if the update was not successful', function() {
			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);

			deferredUpdateVariable.reject();
			scope.$apply();

			expect(serviceUtilities.genericAndRatherUselessErrorHandler).toHaveBeenCalled();
		});

		it('should set editing to false after a successful update', function() {
			scope.editing = true;
			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);

			deferredUpdateVariable.resolve();
			scope.$apply();

			expect(scope.editing).toBe(false);
		});

		it('should update variable on the parent scope after a successful update', function() {
			spyOn(scope, 'updateSelectedVariable').and.callThrough();

			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);

			deferredUpdateVariable.resolve();
			scope.$apply();

			expect(scope.updateSelectedVariable).toHaveBeenCalledWith(PLANT_VIGOR);
		});
	});

	describe('$scope.deleteVariable', function() {

		var confirmation;

		beforeEach(function() {
			formUtilities.confirmationHandler = function() {
				confirmation = q.defer();
				return confirmation.promise;
			};

			scope.updateSelectedVariable = function(/*model*/) {};

			spyOn(formUtilities, 'confirmationHandler').and.callThrough();
		});

		it('should call the confirmation handler', function() {
			scope.deleteVariable(fakeEvent, PLANT_VIGOR.id);

			expect(formUtilities.confirmationHandler).toHaveBeenCalled();
			expect(formUtilities.confirmationHandler.calls.mostRecent().args[0]).toEqual(scope);
		});

		it('should call the variables service to delete the variable if the confirmation is resolved', function() {
			scope.deleteVariable(fakeEvent, PLANT_VIGOR.id);

			confirmation.resolve();
			scope.$apply();

			expect(variablesService.deleteVariable).toHaveBeenCalledWith(PLANT_VIGOR.id);
		});

		it('should set an error if the update was not successful', function() {
			scope.clientErrors = {};
			scope.deleteVariable(fakeEvent, PLANT_VIGOR.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteVariable.reject();
			scope.$apply();

			expect(scope.clientErrors.failedToDelete).toBe(true);
		});

		it('should remove variable on the parent scope and hide the panel after a successful delete', function() {
			spyOn(scope, 'updateSelectedVariable').and.callThrough();

			scope.deleteVariable(fakeEvent, PLANT_VIGOR.id);

			confirmation.resolve();
			scope.$apply();

			deferredDeleteVariable.resolve();
			scope.$apply();

			expect(panelService.hidePanel).toHaveBeenCalled();
			expect(scope.updateSelectedVariable).toHaveBeenCalledWith();
		});
	});
});
