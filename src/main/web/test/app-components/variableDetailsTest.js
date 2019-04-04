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
			serverErrorHandler: function() {
				return {};
			}
		},

		PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			metadata: {
				editableFields: ['description', 'cropOntologyId', 'variableTypeIds']
			}
		},

		VARIABLE_TYPES_INC_TREATMENT_FACTOR = [{
			id: 1808,
			name: 'Trait',
			description: 'Characteristics of a germplasm to be recorded during a study.'
		}, {
			id: 9,
			name: 'Treatment Factor',
			description: 'Treatments to be applied to members of a study.'
		}],

		VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR = [{
			id: 1808,
			name: 'Trait',
			description: 'Characteristics of a germplasm to be recorded during a study.'
		}],

		SOME_LISTS_NOT_LOADED = 'validation.variable.someListsNotLoaded',

		propertiesService = {},
		methodsService = {},
		scalesService = {},
		variableTypesService = {},
		variablesService = {},
		variableStateService = {},

		formUtilities,
		scope,
		q,
		directiveElement,
		deferredGetProperties,
		deferredGetMethods,
		deferredGetScalesWithNonSystemDataTypes,
		deferredGetTypes,
		deferredUpdateVariable,
		deferredGetVariable,
		deferredDeleteVariable,
		deferredDeleteVariablesFromCache,
		mockTranslateFilter;

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-variable-details></om-variable-details>')(scope);
		});
		scope.$digest();
	}

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};

		angular.mock.module('templates');

		module('variableDetails', function($provide) {
			// Provide mocks for the directive controller
			$provide.value('propertiesService', propertiesService);
			$provide.value('methodsService', methodsService);
			$provide.value('scalesService', scalesService);
			$provide.value('serviceUtilities', serviceUtilities);
			$provide.value('variablesService', variablesService);
			$provide.value('variableTypesService', variableTypesService);
			$provide.value('panelService', panelService);
			$provide.value('variableStateService', variableStateService);
		});
	});

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

		scalesService.getScalesWithNonSystemDataTypes = function() {
			deferredGetScalesWithNonSystemDataTypes = q.defer();
			return deferredGetScalesWithNonSystemDataTypes.promise;
		};

		variableTypesService.getTypes = function() {
			deferredGetTypes = q.defer();
			return deferredGetTypes.promise;
		};

		variablesService.updateVariable = function() {
			deferredUpdateVariable = q.defer();
			return deferredUpdateVariable.promise;
		};

		variablesService.getVariable = function() {
			deferredGetVariable = q.defer();
			return deferredGetVariable.promise;
		};

		variablesService.deleteVariable = function() {
			deferredDeleteVariable = q.defer();
			return deferredDeleteVariable.promise;
		};

		variablesService.deleteVariablesFromCache = function() {
			deferredDeleteVariablesFromCache = q.defer();
			return deferredDeleteVariablesFromCache.promise;
		};

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScalesWithNonSystemDataTypes').and.callThrough();
		spyOn(variableTypesService, 'getTypes').and.callThrough();
		spyOn(variablesService, 'updateVariable').and.callThrough();
		spyOn(variablesService, 'deleteVariable').and.callThrough();
		spyOn(variablesService, 'deleteVariablesFromCache').and.callThrough();
		spyOn(serviceUtilities, 'serverErrorHandler').and.callThrough();
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

		it('should reset errors and remove any leftover confirmation handlers if the selected method changes', function() {
			scope.selectedVariable = PLANT_VIGOR;
			scope.deny = function() {};
			scope.clientErrors = { general: ['error'] };

			spyOn(scope, 'deny');

			scope.$apply();

			expect(scope.deny).toHaveBeenCalled();
			expect(scope.clientErrors).toEqual({});
		});

		it('should set the model to be the selected variable if the selected variable changes', function() {
			scope.selectedVariable = PLANT_VIGOR;
			scope.selectedVariable.metadata.disableFields = [];
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
			scope.model.metadata = {disableFields: []};

			scope.$apply();

			expect(scope.showTreatmentFactorAlert).toBe(true);
		});

		it('should hide the Treatment Factor warning message if the variable types do not include Treatment Factor', function() {
			scope.model = {};
			scope.model.variableTypes = VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR;
			scope.model.metadata = {disableFields: []};
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

	describe('$scope.editVariable', function() {

		it('should set editing to be true', function() {
			scope.editing = false;
			scope.model = {
				metadata: {
					editableFields: ['alias'],
					disableFields: [],
				},
				variableTypes: [VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR]

			};
			scope.editVariable(fakeEvent);
			expect(scope.editing).toBe(true);
			expect(scope.aliasIsDisable).toBe(true);

		});

		describe('getting variable types', function() {

			beforeEach(function() {
				scope.model = {
					metadata: {
						editableFields: ['alias'],
						disableFields: []

			},
					variableTypes: [VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR]

				};
				scope.editVariable(fakeEvent);
			});

			it('should call the variables service to get all variable types', function() {
				expect(variableTypesService.getTypes).toHaveBeenCalled();
			});

			it('should handle any errors if retrieving the variable types was not successful', function() {
				deferredGetTypes.reject();
				scope.$apply();
				expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
				expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
			});

			it('should set data.types to the returned variable types after a successful update', function() {
				deferredGetTypes.resolve([1, 2]);
				scope.$apply();

				expect(scope.data.types).toEqual([1, 2]);
			});
		});

		describe('getting properties', function() {
			beforeEach(function() {
				scope.model = {
					metadata: {
						editableFields: ['alias'],
						disableFields: []
					},
					variableTypes: [VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR]

				};
				scope.editVariable(fakeEvent);
			});

			it('should call the properties service to get all properties', function() {
				expect(propertiesService.getProperties).toHaveBeenCalled();
			});

			it('should handle any errors if retrieving the properties was not successful', function() {
				deferredGetProperties.reject();
				scope.$apply();
				expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
				expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
			});

			it('should set data.properties to the returned properties after a successful update', function() {
				deferredGetProperties.resolve([PLANT_VIGOR]);
				scope.$apply();

				expect(scope.data.properties).toEqual([PLANT_VIGOR]);
			});
		});

		describe('getting methods', function() {
			beforeEach(function() {
				scope.model = {
					metadata: {
						editableFields: ['alias'],
						disableFields: []
					},
					variableTypes: [VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR]

				};
				scope.editVariable(fakeEvent);
			});

			it('should call the methods service to get all methods', function() {
				expect(methodsService.getMethods).toHaveBeenCalled();
			});

			it('should handle any errors if retrieving the methods was not successful', function() {
				deferredGetMethods.reject();
				scope.$apply();
				expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
				expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
			});

			it('should set data.methods to the returned methods after a successful update', function() {
				deferredGetMethods.resolve([PLANT_VIGOR]);
				scope.$apply();

				expect(scope.data.methods).toEqual([PLANT_VIGOR]);
			});
		});

		describe('getting scales', function() {
			beforeEach(function() {
				scope.model = {
					metadata: {
						editableFields: ['alias'],
						disableFields: []
					},
					variableTypes: [VARIABLE_TYPES_WITHOUT_TREATMENT_FACTOR]

				};
				scope.editVariable(fakeEvent);
			});

			it('should call the scales service to get all scales', function() {
				expect(scalesService.getScalesWithNonSystemDataTypes).toHaveBeenCalled();
			});

			it('should handle any errors if retrieving the scales was not successful', function() {
				deferredGetScalesWithNonSystemDataTypes.reject();
				scope.$apply();
				expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
				expect(scope.serverErrors.someListsNotLoaded).toEqual([SOME_LISTS_NOT_LOADED]);
			});

			it('should set data.scales to the returned scales after a successful update', function() {
				deferredGetScalesWithNonSystemDataTypes.resolve([PLANT_VIGOR]);
				scope.$apply();

				expect(scope.data.scales).toEqual([PLANT_VIGOR]);
			});
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
			scope.addAliasToTableIfPresent = function(/*variables*/) {};
			scope.vdForm.$valid = true;
			scope.selectedVariable = {
				alias: ''
			};
		}));

		it('should call the variables service to update the variable', function() {
			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);
			expect(variablesService.updateVariable).toHaveBeenCalledWith(PLANT_VIGOR.id, PLANT_VIGOR);
		});

		it('should favourite the variable if alias has been set and there was no alias previously', function() {
			scope.model = angular.copy(PLANT_VIGOR);
			scope.model.alias = 'test';
			scope.model.favourite = false;

			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, scope.model);
			expect(scope.model.favourite).toBe(true);
		});

		it('should not favourite the variable if alias is changed and there was an alias previously', function() {
			scope.selectedVariable.alias = 'exisingAlias';
			scope.model = angular.copy(PLANT_VIGOR);
			scope.model.alias = 'test';
			scope.model.favourite = false;

			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, scope.model);
			expect(scope.model.favourite).toBe(false);
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

			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
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
			deferredGetVariable.resolve(PLANT_VIGOR);
			scope.$apply();

			expect(scope.updateSelectedVariable).toHaveBeenCalledWith(PLANT_VIGOR);
		});
	});

	describe('$scope.toggleFavourites', function() {
		beforeEach(function() {
			scope.updateSelectedVariable = function() {};
		});

		it('should toggle the model and the selected variable (true -> false)', function() {
			scope.model = {favourite: true};
			scope.selectedVariable =  {favourite: true};
			scope.toggleFavourites(PLANT_VIGOR.id, scope.model);
			expect(scope.model.favourite).toBe(false);
			expect(scope.selectedVariable.favourite).toBe(false);
		});

		it('should toggle the model and the selected variable (false -> true)', function() {
			scope.model = {favourite: false};
			scope.selectedVariable =  {favourite: false};
			scope.toggleFavourites(PLANT_VIGOR.id, scope.model);
			expect(scope.model.favourite).toBe(true);
			expect(scope.selectedVariable.favourite).toBe(true);
		});

		it('should update the selected variable on the parent scope', function() {
			scope.model = {favourite: false};
			scope.selectedVariable =  {favourite: false};

			spyOn(scope, 'updateSelectedVariable').and.callThrough();
			scope.toggleFavourites(PLANT_VIGOR.id, scope.model);

			deferredUpdateVariable.resolve();
			scope.$apply();

			expect(scope.updateSelectedVariable).toHaveBeenCalledWith(scope.selectedVariable);
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

			expect(serviceUtilities.serverErrorHandler).toHaveBeenCalled();
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
