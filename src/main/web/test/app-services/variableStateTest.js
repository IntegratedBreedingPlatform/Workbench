/*global expect, inject, spyOn*/
'use strict';

describe('Variable State Service', function() {
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

		TRANSLATIONS = {
			'variableStateService.couldNotSetProperty': 'an error message',
			'variableStateService.couldNotSetMethod': 'another error message',
			'variableStateService.couldNotSetScale': 'yet another error message'
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

		module(function ($translateProvider, $provide) {

			$translateProvider
				.translations('en', TRANSLATIONS)
				.preferredLanguage('en');

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

			var result;

			variableStateService.storeVariableState(PLANT_VIGOR, SCOPE_DATA);

			result = variableStateService.getVariableState();

			expect(result.variable).toEqual(PLANT_VIGOR);
			expect(result.scopeData).toEqual(SCOPE_DATA);
		});

		it('should default to an empty object for falsy variable or scope data values', function() {

			var result;

			variableStateService.storeVariableState(null, null);

			result = variableStateService.getVariableState();

			expect(result.variable).toEqual({});
			expect(result.scopeData).toEqual({});

			variableStateService.storeVariableState();

			result = variableStateService.getVariableState();

			expect(result.variable).toEqual({});
			expect(result.scopeData).toEqual({});
		});

		it('should set edit in progress to be true', function() {

			variableStateService.storeVariableState(PLANT_VIGOR, SCOPE_DATA);

			expect(variableStateService.updateInProgress()).toBe(true);
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

		it('should empty any stored state and cancel any edit if there is one in progress', function() {

			var result;

			// Store some data
			variableStateService.storeVariableState(PLANT_VIGOR, SCOPE_DATA);

			variableStateService.reset();

			result = variableStateService.getVariableState();

			expect(result.variable).toEqual({});
			expect(result.scopeData).toEqual({});
			expect(result.errors).toEqual([]);
			expect(variableStateService.updateInProgress()).toBe(false);
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

		it('should set an error and return a promise that is rejected if the properties service fails to successfully returns', function() {

			var selectedPropertyId = 1,
				errorMessage = TRANSLATIONS['variableStateService.couldNotSetProperty'],
				result,
				state,
				success;

			result = variableStateService.setProperty(selectedPropertyId);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetProperties.reject();
			scope.$apply();

			state = variableStateService.getVariableState();

			expect(state.errors.length).toEqual(1);
			expect(state.errors[0]).toEqual(errorMessage);
			expect(success).toBe(false);
		});

		it('should set the properties and property summary if the call to the properties service is successful', function() {

			var properties = [{id: 1, name: 'a property'}],
				selectedPropertyId = 1,
				selectedPropertyName = 'a property',
				result,
				state,
				success;

			result = variableStateService.setProperty(selectedPropertyId, selectedPropertyName);

			// Because Angular doesn't let us inspect the state of a promise, call fake success and failure handlers
			// to ensure correct promise resolution
			result.then(function() {success = true;}, function() {success = false;});

			deferredGetProperties.resolve(properties);
			scope.$apply();

			state = variableStateService.getVariableState();

			expect(state.variable.propertySummary.id).toEqual(selectedPropertyId);
			expect(state.variable.propertySummary.name).toEqual(selectedPropertyName);
			expect(state.scopeData.properties).toEqual(properties);
		});
	});

	describe('setMethod', function() {

		it('should return a promise that is resolved if the methods service successfully returns methods', function() {

			var methods = [{id: 1, name: 'a method'}],
				selectedMethodId = 1,
				result,
				success;

			result = variableStateService.setMethod(selectedMethodId);

			result.then(function() {success = true;}, function() {success = false;});

			deferredGetMethods.resolve(methods);
			scope.$apply();

			expect(success).toBe(true);
		});

		it('should set an error and return a promise that is rejected if the methods service fails to successfully returns', function() {

			var selectedMethodId = 1,
				errorMessage = TRANSLATIONS['variableStateService.couldNotSetMethod'],
				result,
				state,
				success;

			result = variableStateService.setMethod(selectedMethodId);

			result.then(function() {success = true;}, function() {success = false;});

			deferredGetMethods.reject();
			scope.$apply();

			state = variableStateService.getVariableState();

			expect(state.errors.length).toEqual(1);
			expect(state.errors[0]).toEqual(errorMessage);
			expect(success).toBe(false);

		});

		it('should set the methods and method summary if the call to the methods service is successful', function() {

			var methods = [{id: 1, name: 'a method'}],
				selectedMethodId = 1,
				selectedMethodName = 'a method',
				result,
				state,
				success;

			result = variableStateService.setMethod(selectedMethodId, selectedMethodName);

			result.then(function() {success = true;}, function() {success = false;});

			deferredGetMethods.resolve(methods);
			scope.$apply();

			state = variableStateService.getVariableState();

			expect(state.variable.methodSummary.id).toEqual(selectedMethodId);
			expect(state.variable.methodSummary.name).toEqual(selectedMethodName);
			expect(state.scopeData.methods).toEqual(methods);
		});
	});

	describe('setScale', function() {

		it('should return a promise that is resolved if the scales service successfully returns scales', function() {

			var scales = [{id: 1, name: 'a scale'}],
				selectedScaleId = 1,
				result,
				success;

			result = variableStateService.setScale(selectedScaleId);

			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.resolve(scales);
			scope.$apply();

			expect(success).toBe(true);

		});

		it('should set an error and return a promise that is rejected if the scales service fails to successfully returns', function() {

			var selectedScaleId = 1,
				errorMessage = TRANSLATIONS['variableStateService.couldNotSetScale'],
				result,
				state,
				success;

			result = variableStateService.setScale(selectedScaleId);

			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.reject();
			scope.$apply();

			state = variableStateService.getVariableState();

			expect(state.errors.length).toEqual(1);
			expect(state.errors[0]).toEqual(errorMessage);
			expect(success).toBe(false);

		});

		it('should set the scales and scale if the call to the scales service is successful', function() {

			var scales = [{id: 1, name: 'a scale'}],
				selectedScaleId = 1,
				result,
				state,
				success;

			result = variableStateService.setScale(selectedScaleId);

			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.resolve(scales);
			scope.$apply();

			state = variableStateService.getVariableState();

			expect(state.variable.scale.id).toEqual(selectedScaleId);
			expect(state.scopeData.scales).toEqual(scales);
		});

		it('should not set the scale if there is no matching id', function() {

			var scales = [{id: 1, name: 'a scale'}],
				selectedScaleId = 3,
				result,
				state,
				success;

			result = variableStateService.setScale(selectedScaleId);

			result.then(function() {success = true;}, function() {success = false;});

			deferredGetScales.resolve(scales);
			scope.$apply();

			state = variableStateService.getVariableState();

			expect(state.variable.scale).toBeUndefined();
		});
	});
});
