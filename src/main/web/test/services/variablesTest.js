/*global expect, inject, spyOn*/
'use strict';

describe('Variables Service', function() {
	var PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			description: 'A little vigourous',
			propertySummary: {
				id: 1,
				name: 'Plant Vigor'
			},
			methodSummary: {
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
			variableTypeIds: [1]
		},

		CONVERTED_PLANT_VIGOR = {
			id: PLANT_VIGOR.id,
			name: PLANT_VIGOR.name,
			description: PLANT_VIGOR.description,
			propertyId: PLANT_VIGOR.propertySummary.id,
			methodId: PLANT_VIGOR.methodSummary.id,
			scaleId: PLANT_VIGOR.scale.id,
			variableTypeIds: PLANT_VIGOR.variableTypeIds
		},

		variablesService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('variables');
	});

	beforeEach(function () {

		serviceUtilities = {
			restSuccessHandler: function() {},
			restFailureHandler: function() {}
		};

		spyOn(serviceUtilities, 'restSuccessHandler');
		spyOn(serviceUtilities, 'restFailureHandler');

		module(function ($provide) {
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

			httpBackend.expectGET(/\/variables$/).respond();

			variablesService.getVariables();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['variables go here'];

			httpBackend.expectGET(/\/variables$/).respond(response);

			variablesService.getVariables();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/variables$/).respond(500, error);

			variablesService.getVariables();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getFavouriteVariables', function() {

		it('should GET /variables, setting favourite=true', function() {

			httpBackend.expectGET(/\/variables\?favourite=true$/).respond();

			variablesService.getFavouriteVariables();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['variables go here'];

			httpBackend.expectGET(/\/variables\?favourite=true$/).respond(response);

			variablesService.getFavouriteVariables();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/variables\?favourite=true$/).respond(500, error);

			variablesService.getFavouriteVariables();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('addVariable', function() {

		it('should POST to /variables', function() {

			var variable = {
				name: 'myvariable'
			};

			httpBackend.expectPOST(/\/variables$/, variable).respond(201);

			variablesService.addVariable(variable);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful POST is made', function() {

			var variable = {
				name: 'myvariable'
			},
			response = 123;

			httpBackend.expectPOST(/\/variables$/, variable).respond(201, response);

			variablesService.addVariable(variable);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful POST is not made', function() {

			var error = 'Error!';

			httpBackend.expectPOST(/\/variables$/).respond(500, error);

			variablesService.addVariable({});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getVariable', function() {

		it('should GET /variable, specifying the given id', function() {

			var id = 123;

			// FIXME check that the variable with the specified ID is actually requested once we've hooked up the real service
			httpBackend.expectGET(/\/variables\/:id$/).respond();

			variablesService.getVariable(id);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var id = 123,
				response = ['variables go here'];

			httpBackend.expectGET(/\/variables\/:id$/).respond(response);

			variablesService.getVariable(id);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var id = 123,
				error = 'Error!';

			httpBackend.expectGET(/\/variables\/:id$/).respond(500, error);

			variablesService.getVariable(id);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});

	});

	describe('updateVariable', function() {

		it('should PUT to /updateVariable', function() {

			var variable = {
				name: 'myvariable'
			},

			// FIXME not in use yet because services haven't been hooked up
			id = 1;

			httpBackend.expectPUT(/\/variables\/:id$/, variable).respond(204);

			variablesService.updateVariable(id, variable);

			httpBackend.flush();
		});

		it('should convert method, property and scale objects to ids and remove unnecessary properties before PUTing', function() {

			var variable = PLANT_VIGOR,
				id = 1,
				expectedVariable = CONVERTED_PLANT_VIGOR;

			httpBackend.expectPUT(/\/variables\/:id$/, expectedVariable).respond(204);

			variablesService.updateVariable(id, variable);

			httpBackend.flush();
		});

		it('should return the response status if a successful PUT is made', function() {

			var variable = {
				name: 'myvariable'
			},
			id = 1,

			expectedResponse = 204,
			actualResponse;

			httpBackend.expectPUT(/\/variables\/:id$/, variable).respond(expectedResponse);

			variablesService.updateVariable(id, variable).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful PUT is not made', function() {

			var error = 'Error!';

			httpBackend.expectPUT(/\/variables\/:id$/, {}).respond(500, error);

			variablesService.updateVariable(1, {});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getTypes', function() {

		it('should GET /variableTypes', function() {

			httpBackend.expectGET(/\/variableTypes$/).respond();

			variablesService.getTypes();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['variableTypes go here'];

			httpBackend.expectGET(/\/variableTypes$/).respond(response);

			variablesService.getTypes();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/variableTypes$/).respond(500, error);

			variablesService.getTypes();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});
});
