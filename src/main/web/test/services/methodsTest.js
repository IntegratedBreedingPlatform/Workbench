/*global expect, inject, spyOn*/
'use strict';

describe('Methods Service', function() {
	var methodsService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('methods');
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
		inject(function(_methodsService_, $httpBackend) {
			methodsService = _methodsService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	describe('getMethods', function() {

		it('should GET /methods', function() {

			httpBackend.expectGET(/\/methods$/).respond();

			methodsService.getMethods();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['methods go here'];

			httpBackend.expectGET(/\/methods$/).respond(response);

			methodsService.getMethods();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/methods$/).respond(500, error);

			methodsService.getMethods();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('addMethod', function() {

		it('should POST to /methods', function() {

			var method = {
				name: 'mymethod'
			};

			httpBackend.expectPOST(/\/methods$/, method).respond(201);

			methodsService.addMethod(method);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var method = {
				name: 'mymethod'
			},
			response = 123;

			httpBackend.expectPOST(/\/methods$/, method).respond(201, response);

			methodsService.addMethod(method);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectPOST(/\/methods$/).respond(500, error);

			methodsService.addMethod({});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('updateMethod', function() {

		it('should PUT to /updateMethod', function() {
			httpBackend.expectPUT(/\/methods\/1$/).respond(204);
			methodsService.updateMethod(1, {});
			httpBackend.flush();
		});

		it('should return the response status if a successful PUT is made', function() {
			var id = 1,
				expectedResponse = 204,
				actualResponse;

			httpBackend.expectPUT(/\/methods\/1$/).respond(expectedResponse);

			methodsService.updateMethod(id, {}).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful PUT is not made', function() {
			var error = 'Error!';

			httpBackend.expectPUT(/\/methods\/1$/, {}).respond(500, error);

			methodsService.updateMethod(1, {});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('deleteMethod', function() {

		it('should DELETE /methods/:id', function() {

			var id = 1;

			httpBackend.expectDELETE(/\/methods\/1$/).respond(204);

			methodsService.deleteMethod(id);

			httpBackend.flush();
		});

		it('should return a 204 status if a successful DELETE is made', function() {

			var id = 1,

			expectedResponse = 204,
			actualResponse;

			httpBackend.expectDELETE(/\/methods\/1$/).respond(expectedResponse);

			methodsService.deleteMethod(id).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful DELETE is not made', function() {

			var error = 'Error!';

			httpBackend.expectDELETE(/\/methods\/1$/).respond(500, error);

			methodsService.deleteMethod(1);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getMethod', function() {

		it('should GET /methods, specifying the given id', function() {

			var id = 123;

			httpBackend.expectGET(/\/methods\/123$/).respond();

			methodsService.getMethod(id);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var id = 123,
				response = ['methods go here'];

			httpBackend.expectGET(/\/methods\/123$/).respond(response);

			methodsService.getMethod(id);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var id = 123,
				error = 'Error!';

			httpBackend.expectGET(/\/methods\/123$/).respond(500, error);

			methodsService.getMethod(id);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});
});
