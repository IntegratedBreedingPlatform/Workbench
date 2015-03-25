/*global expect, inject, spyOn*/
'use strict';

describe('VariableState Service', function() {
	var PLANT_VIGOR = {
			id: 2,
			name: 'Plant Vigor',
			description: 'A little vigourous',
			property: 1,
			method: 1,
			scale: 1,
			variableType: [
				1
			]
		},

		SCOPE_DATA = {
			properties: [{id: 1, name: 'a property'}],
			methods: [{id: 1, name: 'a method'}],
			scales: [{id: 1, name: 'a scale'}]
		},

		variableStateService,

		propertiesService,
		methodsService,
		scalesService,

		deferredGetProperties,
		deferredGetMethods,
		deferredGetScales,

		scope,
		q;

	beforeEach(function() {
		module('variableState');
	});

	beforeEach(function () {

		propertiesService = {
			getProperties: function() {
				deferredGetProperties = q.defer();
				return deferredGetProperties.promise;
			}
		};

		methodsService = {
			getMethods: function() {
				deferredGetMethods = q.defer();
				return deferredGetMethods.promise;
			}
		};

		scalesService = {
			getScales: function() {
				deferredGetScales = q.defer();
				return deferredGetScales.promise;
			}
		};

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(scalesService, 'getScales').and.callThrough();

		module(function ($provide) {
			$provide.value('propertiesService', propertiesService);
			$provide.value('methodsService', methodsService);
			$provide.value('scalesService', scalesService);
		});
	});

	beforeEach(function() {
		inject(function(_variableStateService_, $q, $rootScope) {
			variableStateService = _variableStateService_;
			q = $q;
			scope = $rootScope;
		});
	});

	describe('storeVariableState', function() {

		it('should store the specified variable and scope data', function() {

			variableStateService.storeVariableState(PLANT_VIGOR, SCOPE_DATA);

			expect(variableStateService.variable).toEqual(PLANT_VIGOR);
			expect(variableStateService.scopeData).toEqual(SCOPE_DATA);
		});

		it('should default to an empty object for falsy variable or scope data values', function() {

			variableStateService.storeVariableState(null, null);

			expect(variableStateService.variable).toEqual({});
			expect(variableStateService.scopeData).toEqual({});

			variableStateService.storeVariableState();

			expect(variableStateService.variable).toEqual({});
			expect(variableStateService.scopeData).toEqual({});
		});

		it('should set edit in progress to be true', function() {

			variableStateService.storeVariableState(PLANT_VIGOR, SCOPE_DATA);

			expect(variableStateService.editInProgress).toBe(true);
		});
	});

	describe('getVariableState', function() {

		it('should return the stored variable and scope data', function() {

			// Nothing had been stored so it should be empty
			expect(variableStateService.getVariableState()).toEqual({
				variable: {},
				scopeData: {}
			});

			// Store some data
			variableStateService.storeVariableState(PLANT_VIGOR, SCOPE_DATA);

			// It should now be what we stored
			expect(variableStateService.getVariableState()).toEqual({
				variable: PLANT_VIGOR,
				scopeData: SCOPE_DATA
			});
		});
	});

	describe('updateInProgress', function() {

		it('should return whether or not an update is in progress', function() {

			// By default no edit is in progress
			expect(variableStateService.updateInProgress()).toBe(false);

			// Pretend to store some data - this should set an edit in progress
			variableStateService.storeVariableState({}, {});

			// There should now be an edit in progress
			expect(variableStateService.updateInProgress()).toBe(true);

		});
	});

	describe('reset', function() {

		it('should empty any stored data and cancel any edit if there is one in progress', function() {

			// Store some data
			variableStateService.storeVariableState(PLANT_VIGOR, SCOPE_DATA);

			variableStateService.reset();

			expect(variableStateService.variable).toEqual({});
			expect(variableStateService.scopeData).toEqual({});
			expect(variableStateService.editInProgress).toBe(false);
		});
	});

	describe('setProperty', function() {

		it('should return a promise that is resolved if the properties service successfully returns properties', function() {

			var properties = [{id: 1, name: 'a property'}],
				selectedPropertyId = 1,
				result,
				success;

			result = variableStateService.setProperty(selectedPropertyId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetProperties.resolve(properties);
			scope.$apply();

			expect(success).toBe(true);

		});

		it('should return a promise that is rejected if the properties service fails to successfully returns properties', function() {

			var selectedPropertyId = 1,
				result,
				success;

			result = variableStateService.setProperty(selectedPropertyId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetProperties.reject();
			scope.$apply();

			expect(success).toBe(false);

		});

		it('should set the properties and property summary if the call to the properties service is successful', function() {

			var properties = [{id: 1, name: 'a property'}],
				selectedPropertyId = 1,
				selectedPropertyName = 'a property',
				result,
				success;

			result = variableStateService.setProperty(selectedPropertyId, selectedPropertyName);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetProperties.resolve(properties);
			scope.$apply();

			expect(variableStateService.variable.propertySummary.id).toEqual(selectedPropertyId);
			expect(variableStateService.variable.propertySummary.name).toEqual(selectedPropertyName);
			expect(variableStateService.scopeData.properties).toEqual(properties);
		});
	});

	describe('setMethod', function() {

		it('should return a promise that is resolved if the methods service successfully returns methods', function() {

			var methods = [{id: 1, name: 'a method'}],
				selectedMethodId = 1,
				result,
				success;

			result = variableStateService.setMethod(selectedMethodId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetMethods.resolve(methods);
			scope.$apply();

			expect(success).toBe(true);

		});

		it('should return a promise that is rejected if the methods service fails to successfully returns methods', function() {

			var selectedMethodId = 1,
				result,
				success;

			result = variableStateService.setMethod(selectedMethodId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetMethods.reject();
			scope.$apply();

			expect(success).toBe(false);

		});

		it('should set the methods and method summary if the call to the methods service is successful', function() {

			var methods = [{id: 1, name: 'a method'}],
				selectedMethodId = 1,
				selectedMethodName = 'a method',
				result,
				success;

			result = variableStateService.setMethod(selectedMethodId, selectedMethodName);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetMethods.resolve(methods);
			scope.$apply();

			expect(variableStateService.variable.methodSummary.id).toEqual(selectedMethodId);
			expect(variableStateService.variable.methodSummary.name).toEqual(selectedMethodName);
			expect(variableStateService.scopeData.methods).toEqual(methods);
		});
	});

	describe('setScale', function() {

		it('should return a promise that is resolved if the scales service successfully returns scales', function() {

			var scales = [{id: 1, name: 'a scale'}],
				selectedScaleId = 1,
				result,
				success;

			result = variableStateService.setScale(selectedScaleId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.resolve(scales);
			scope.$apply();

			expect(success).toBe(true);

		});

		it('should return a promise that is rejected if the scales service fails to successfully returns scales', function() {

			var selectedScaleId = 1,
				result,
				success;

			result = variableStateService.setScale(selectedScaleId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.reject();
			scope.$apply();

			expect(success).toBe(false);

		});

		it('should set the scales and scale if the call to the scales service is successful', function() {

			var scales = [{id: 1, name: 'a scale'}],
				selectedScaleId = 1,
				result,
				success;

			result = variableStateService.setScale(selectedScaleId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.resolve(scales);
			scope.$apply();

			expect(variableStateService.variable.scale.id).toEqual(selectedScaleId);
			expect(variableStateService.scopeData.scales).toEqual(scales);
		});

		it('should not set the scale if there is no matching id', function() {

			var scales = [{id: 1, name: 'a scale'}],
				selectedScaleId = 3,
				result,
				success;

			result = variableStateService.setScale(selectedScaleId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.resolve(scales);
			scope.$apply();

			expect(variableStateService.variable.scale).toBeUndefined();
		});
	});
});
