/*global angular, expect, inject, spyOn*/
'use strict';

describe('Variable details directive', function() {
	var fakeEvent = {
			preventDefault: function() {}
		},
		serviceUtilities = {
			genericAndRatherUselessErrorHandler: function() {}
		},
		PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor'
		},
		propertiesService = {},
		methodsService = {},
		scalesService = {},
		variablesService = {},
		scope,
		q,
		directiveElement,
		deferredGetProperties,
		deferredGetMethods,
		deferredGetScales,
		deferredUpdateVariable;

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
	}));

	beforeEach(inject(function($rootScope, $q) {
		q = $q;
		scope = $rootScope;

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

		variablesService.updateVariable = function() {
			deferredUpdateVariable = q.defer();
			return deferredUpdateVariable.promise;
		};

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScales').and.callThrough();
		spyOn(variablesService, 'updateVariable').and.callThrough();
		spyOn(serviceUtilities, 'genericAndRatherUselessErrorHandler');

		compileDirective();
	}));

	describe('by default', function() {

		it('should set editing to false', function() {
			expect(scope.editing).toBe(false);
		});

		it('should set data to have an empty array of properties, methods and scales', function() {
			expect(scope.data).toEqual({
				properties: [],
				methods: [],
				scales: []
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
	});

	describe('getting properties', function() {
		it('should call the properties service to get all properties', function() {
			expect(propertiesService.getProperties).toHaveBeenCalled();
		});

		it('should handle any errors if the retrieving properties was not successful', function() {
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

		it('should handle any errors if the retrieving methods was not successful', function() {
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

		it('should handle any errors if the retrieving scales was not successful', function() {
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
		it('should set editing to be false', function() {
			scope.editing = true;
			scope.cancel(fakeEvent);
			expect(scope.editing).toBe(false);
		});

		it('should set the model back to the original unchanged variable', function() {
			scope.model = null;
			scope.selectedVariable = {
				name: 'variable'
			};
			scope.cancel(fakeEvent);
			expect(scope.model).toEqual(scope.selectedVariable);
		});
	});

	describe('$scope.saveChanges', function() {

		beforeEach(function() {
			scope.updateSelectedVariable = function(/*model*/) {};
		});

		it('should call the variables service to update the variable', function() {
			scope.saveChanges(fakeEvent, PLANT_VIGOR.id, PLANT_VIGOR);
			expect(variablesService.updateVariable).toHaveBeenCalledWith(PLANT_VIGOR.id, PLANT_VIGOR);
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

});
