/*global expect, inject, spyOn*/
'use strict';

describe('Scales Service', function() {
	var scalesService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('scales');
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
		inject(function(_scalesService_, $httpBackend) {
			scalesService = _scalesService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	describe('getScales', function() {

		it('should GET /scales', function() {

			httpBackend.expectGET(/\/scales$/).respond();

			scalesService.getScales();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['scales go here'];

			httpBackend.expectGET(/\/scales$/).respond(response);

			scalesService.getScales();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/scales$/).respond(500, error);

			scalesService.getScales();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('addScale', function() {

		it('should POST to /scales', function() {

			var scale = {
				name: 'myscale'
			};

			httpBackend.expectPOST(/\/scales$/, scale).respond(201);

			scalesService.addScale(scale);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var scale = {
				name: 'myscale'
			},
			response = 123;

			httpBackend.expectPOST(/\/scales$/, scale).respond(201, response);

			scalesService.addScale(scale);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectPOST(/\/scales$/).respond(500, error);

			scalesService.addScale({});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getScale', function() {

		it('should GET /scales, specifying the given id', function() {

			var id = 123;

			// FIXME check that the property with the specified ID is actually requested once we've hooked up the real service
			httpBackend.expectGET(/\/scales\/:id$/).respond();

			scalesService.getScale(id);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var id = 123,
				response = ['scales go here'];

			httpBackend.expectGET(/\/scales\/:id$/).respond(response);

			scalesService.getScale(id);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var id = 123,
				error = 'Error!';

			httpBackend.expectGET(/\/scales\/:id$/).respond(500, error);

			scalesService.getScale(id);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});
});
