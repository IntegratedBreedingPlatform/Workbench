/*global angular, expect, inject, spyOn*/
'use strict';

describe('Variables Service', function() {
	var PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			description: 'A little vigourous',
			property: {
				id: 1,
				name: 'Plant Vigor'
			},
			method: {
				id: 1,
				name: 'Look at the plant'
			},
			scale: {
				id: 1,
				name: 'Score',
				description: 'As per title',
				dataType: {
					id: 2,
					name: 'Numeric'
				},
				validValues: {
					min: 0,
					max: 10
				}
			},
			variableTypes: [{
				id: 1,
				name: 'Analysis',
				description: ''
			}]
		},

		CONVERTED_PLANT_VIGOR = {
			name: PLANT_VIGOR.name,
			description: PLANT_VIGOR.description,
			property: PLANT_VIGOR.property,
			method: PLANT_VIGOR.method,
			scale: PLANT_VIGOR.scale,
			variableTypes: PLANT_VIGOR.variableTypes
		},

		variablesService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('variables');
	});

	beforeEach(function() {

		serviceUtilities = {
			restSuccessHandler: function() {},
			restFailureHandler: function() {}
		};

		spyOn(serviceUtilities, 'restSuccessHandler');
		spyOn(serviceUtilities, 'restFailureHandler');

		module(function($provide) {
			$provide.value('serviceUtilities', serviceUtilities);
		});
	});

	beforeEach(function() {
		inject(function(_variablesService_, $httpBackend) {
			variablesService = _variablesService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	describe('getVariables', function() {

		it('should GET /variables', function() {

			httpBackend.expectGET(/\/variables\?programId=$/).respond();

			variablesService.getVariables();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['variables go here'];

			httpBackend.expectGET(/\/variables\?programId=$/).respond(response);

			variablesService.getVariables();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/variables\?programId=$/).respond(500, error);

			variablesService.getVariables();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getFavouriteVariables', function() {

		it('should GET /variables, setting favourite=true', function() {

			httpBackend.expectGET(/\/variables\?favourite=true\&programId=$/).respond();

			variablesService.getFavouriteVariables();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['variables go here'];

			httpBackend.expectGET(/\/variables\?favourite=true\&programId=$/).respond(response);

			variablesService.getFavouriteVariables();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/variables\?favourite=true\&programId=$/).respond(500, error);

			variablesService.getFavouriteVariables();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('addVariable', function() {

		it('should POST to /variables', function() {

			httpBackend.expectPOST(/\/variables\?programId=$/, CONVERTED_PLANT_VIGOR).respond(201);

			variablesService.addVariable(PLANT_VIGOR);

			httpBackend.flush();
		});

		it('should convert method, property and scale objects to ids and remove unnecessary properties before POSTing', function() {

			var variable = angular.copy(PLANT_VIGOR),
				expectedVariable = CONVERTED_PLANT_VIGOR;

			httpBackend.expectPOST(/\/variables\?programId=$/, expectedVariable).respond(201);

			variablesService.addVariable(variable);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful POST is made', function() {

			var response = 123;

			httpBackend.expectPOST(/\/variables\?programId=$/, CONVERTED_PLANT_VIGOR).respond(201, response);

			variablesService.addVariable(PLANT_VIGOR);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful POST is not made', function() {

			var error = 'Error!';

			httpBackend.expectPOST(/\/variables\?programId=$/).respond(500, error);

			variablesService.addVariable(PLANT_VIGOR);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getVariable', function() {

		it('should GET /variable, specifying the given id', function() {
			var id = 123;

			httpBackend.expectGET(/\/variables\/123\?programId=$/).respond();

			variablesService.getVariable(id);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var id = 123,
				response = ['variables go here'];

			httpBackend.expectGET(/\/variables\/123\?programId=$/).respond(response);

			variablesService.getVariable(id);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var id = 123,
				error = 'Error!';

			httpBackend.expectGET(/\/variables\/123\?programId=$/).respond(500, error);

			variablesService.getVariable(id);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});

	});

	describe('updateVariable', function() {

		it('should PUT to /variables/:id', function() {

			// FIXME not in use yet because services haven't been hooked up
			var id = 1;

			httpBackend.expectPUT(/\/variables\/1\?programId=$/, CONVERTED_PLANT_VIGOR).respond(204);

			variablesService.updateVariable(id, PLANT_VIGOR);

			httpBackend.flush();
		});

		it('should convert method, property and scale objects to ids and remove unnecessary properties before PUTing', function() {

			var variable = angular.copy(PLANT_VIGOR),
				id = 1,
				expectedVariable = CONVERTED_PLANT_VIGOR;

			variable.metadata = {
				deletable: true,
				editableFields: ['blah']
			};

			httpBackend.expectPUT(/\/variables\/1\?programId=$/, expectedVariable).respond(204);

			variablesService.updateVariable(id, variable);

			httpBackend.flush();
		});

		it('should return a 204 status if a successful PUT is made', function() {

			var id = 1,
				expectedResponse = 204,
				actualResponse;

			httpBackend.expectPUT(/\/variables\/1\?programId=$/, CONVERTED_PLANT_VIGOR).respond(expectedResponse);

			variablesService.updateVariable(id, PLANT_VIGOR).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful PUT is not made', function() {

			var error = 'Error!';

			httpBackend.expectPUT(/\/variables\/1\?programId=$/, CONVERTED_PLANT_VIGOR).respond(500, error);

			variablesService.updateVariable(1, PLANT_VIGOR);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('deleteVariable', function() {

		it('should DELETE /variables/:id', function() {
			var id = 1;

			httpBackend.expectDELETE(/\/variables\/1\?programId=$/).respond(204);

			variablesService.deleteVariable(id);

			httpBackend.flush();
		});

		it('should return a 204 status if a successful DELETE is made', function() {

			var id = 1,

			expectedResponse = 204,
			actualResponse;

			httpBackend.expectDELETE(/\/variables\/1\?programId=$/).respond(expectedResponse);

			variablesService.deleteVariable(id).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful DELETE is not made', function() {

			var error = 'Error!';

			httpBackend.expectDELETE(/\/variables\/1\?programId=$/).respond(500, error);

			variablesService.deleteVariable(1);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});

		it('should DELETE variableCache /variableCache/:ids', function() {
			var ids = [1, 2, 5];

			httpBackend.expectDELETE(/\/variableCache\/1,2,5/).respond(204);

			variablesService.deleteVariablesFromCache(ids);

			httpBackend.flush();
		});

		it('should return a 204 status if a successful DELETE is made', function() {

			var ids = [1, 2, 5],

			expectedResponse = 204,
			actualResponse;

			httpBackend.expectDELETE(/\/variableCache\/1,2,5/).respond(expectedResponse);

			variablesService.deleteVariablesFromCache(ids).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

	});
});
