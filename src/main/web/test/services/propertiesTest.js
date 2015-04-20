/*global expect, inject, spyOn*/
'use strict';

describe('Properties Service', function() {
	var DETAILED_PROPERTY = {
			name: 'Alkali Injury',
			description: 'Condition characterized by discoloration of the leaves ranging from ...',
			classes: ['Abiotic Stress', 'Trait'],
			cropOntologyId: 'CO_192791864',
			editableFields: ['name', 'description', 'classes', 'cropOntologyId'],
			deletable: true
		},

		PROPERTY_FOR_ADD_OR_UPDATE = {
			name: DETAILED_PROPERTY.name,
			description: DETAILED_PROPERTY.description,
			classes: DETAILED_PROPERTY.classes,
			cropOntologyId: DETAILED_PROPERTY.cropOntologyId
		},

		propertiesService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('properties');
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
		inject(function(_propertiesService_, $httpBackend) {
			propertiesService = _propertiesService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	describe('getProperties', function() {

		it('should GET /properties', function() {

			httpBackend.expectGET(/\/properties$/).respond();

			propertiesService.getProperties();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['properties go here'];

			httpBackend.expectGET(/\/properties$/).respond(response);

			propertiesService.getProperties();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/properties$/).respond(500, error);

			propertiesService.getProperties();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('addProperty', function() {

		it('should POST to /properties', function() {

			httpBackend.expectPOST(/\/properties$/, PROPERTY_FOR_ADD_OR_UPDATE).respond(201);

			propertiesService.addProperty(PROPERTY_FOR_ADD_OR_UPDATE);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = 123;

			httpBackend.expectPOST(/\/properties$/, PROPERTY_FOR_ADD_OR_UPDATE).respond(201, response);

			propertiesService.addProperty(PROPERTY_FOR_ADD_OR_UPDATE);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectPOST(/\/properties$/).respond(500, error);

			propertiesService.addProperty({});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('updateProperty', function() {

		it('should PUT to /updateProperty', function() {

			httpBackend.expectPUT(/\/properties\/1$/).respond(204);

			propertiesService.updateProperty(1, {});

			httpBackend.flush();
		});

		it('should remove unnecessary properties before PUTing', function() {
			var id = 1;

			httpBackend.expectPUT(/\/properties\/1$/, PROPERTY_FOR_ADD_OR_UPDATE).respond(204);

			propertiesService.updateProperty(id, DETAILED_PROPERTY);

			httpBackend.flush();
		});

		it('should return the response status if a successful PUT is made', function() {
			var id = 1,
				expectedResponse = 204,
				actualResponse;

			httpBackend.expectPUT(/\/properties\/1$/).respond(expectedResponse);

			propertiesService.updateProperty(id, {}).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful PUT is not made', function() {

			var error = 'Error!';

			httpBackend.expectPUT(/\/properties\/1$/, {}).respond(500, error);

			propertiesService.updateProperty(1, {});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('deleteProperty', function() {

		it('should DELETE /properties/:id', function() {

			// FIXME not in use yet because services haven't been hooked up
			var id = 1;

			httpBackend.expectDELETE(/\/properties\/1$/).respond(204);

			propertiesService.deleteProperty(id);

			httpBackend.flush();
		});

		it('should return a 204 status if a successful DELETE is made', function() {

			var id = 1,

			expectedResponse = 204,
			actualResponse;

			httpBackend.expectDELETE(/\/properties\/1$/).respond(expectedResponse);

			propertiesService.deleteProperty(id).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful DELETE is not made', function() {

			var error = 'Error!';

			httpBackend.expectDELETE(/\/properties\/1$/).respond(500, error);

			propertiesService.deleteProperty(1);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getProperty', function() {

		it('should GET /properties, specifying the given id', function() {

			var id = 1;

			// FIXME check that the property with the specified ID is actually requested once we've hooked up the real service
			httpBackend.expectGET(/\/properties\/1$/).respond();

			propertiesService.getProperty(id);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var id = 1,
				response = ['properties go here'];

			httpBackend.expectGET(/\/properties\/1$/).respond(response);

			propertiesService.getProperty(id);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var id = 1,
				error = 'Error!';

			httpBackend.expectGET(/\/properties\/1$/).respond(500, error);

			propertiesService.getProperty(id);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});

	});

	describe('getClasses', function() {

		it('should GET /classes', function() {

			httpBackend.expectGET(/\/classes$/).respond();

			propertiesService.getClasses();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['classes go here'];

			httpBackend.expectGET(/\/classes$/).respond(response);

			propertiesService.getClasses();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/classes$/).respond(500, error);

			propertiesService.getClasses();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});
});
