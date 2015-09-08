/*global expect, inject, spyOn*/
'use strict';

describe('Scales Service', function() {
	var scalesService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('scales');
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

	describe('getScalesWithNonSystemDataTypes', function() {
		var deferredGetScales,
			q,
			rootScope;

		beforeEach(inject(function($q, $rootScope) {
			q = $q;
			rootScope = $rootScope;

			scalesService.getScales = function() {
				deferredGetScales = q.defer();
				return deferredGetScales.promise;
			};

			spyOn(scalesService, 'getScales').and.callThrough();
		}));

		it('should filter out scales with system data types and return an array of scales', function() {

			var systemTypeScale = {
					dataType: {
						id: 1,
						name: 'system',
						systemDataType: true
					}
				},
				nonSystemTypeScale = {
					dataType: {
						id: 2,
						name: 'nonsystem',
						systemDataType: false
					}
				},
				scales = [systemTypeScale, nonSystemTypeScale],
				filteredScalesPromise,
				filteredScales;

			filteredScalesPromise = scalesService.getScalesWithNonSystemDataTypes();

			filteredScalesPromise.then(function(result) {
				filteredScales = result;
			});

			deferredGetScales.resolve(scales);
			rootScope.$apply();

			expect(filteredScales).toEqual([nonSystemTypeScale]);
		});

		it('should returned the failed response if unable to get the scales', function() {
			var filteredScalesPromise,
				rejectMessage = 'fail',
				response;

			filteredScalesPromise = scalesService.getScalesWithNonSystemDataTypes();

			filteredScalesPromise.catch(function(result) {
				response = result;
			});

			deferredGetScales.reject(rejectMessage);
			rootScope.$apply();

			expect(response).toEqual(rejectMessage);
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

	describe('updateScale', function() {

		it('should PUT to /updateScale', function() {
			httpBackend.expectPUT(/\/scales\/null$/).respond(204);
			scalesService.updateScale(null, {});
			httpBackend.flush();
		});

		it('should return the response status if a successful PUT is made', function() {
			var expectedResponse = 204,
				actualResponse;

			httpBackend.expectPUT(/\/scales\/1$/).respond(expectedResponse);

			scalesService.updateScale(1, {}).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful PUT is not made', function() {
			var error = 'Error!';

			httpBackend.expectPUT(/\/scales\/1$/, {}).respond(500, error);

			scalesService.updateScale(1, {});
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('deleteScale', function() {

		it('should DELETE /scales/:id', function() {

			httpBackend.expectDELETE(/\/scales\/1$/).respond(204);

			scalesService.deleteScale(1);

			httpBackend.flush();
		});

		it('should return a 204 status if a successful DELETE is made', function() {

			var expectedResponse = 204,
			actualResponse;

			httpBackend.expectDELETE(/\/scales\/1$/).respond(expectedResponse);

			scalesService.deleteScale(1).then(function(res) {
				actualResponse = res;
			});

			httpBackend.flush();

			expect(actualResponse).toEqual(expectedResponse);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful DELETE is not made', function() {

			var error = 'Error!';

			httpBackend.expectDELETE(/\/scales\/1$/).respond(500, error);

			scalesService.deleteScale(1);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getScale', function() {

		it('should GET /scales, specifying the given id', function() {

			httpBackend.expectGET(/\/scales\/123$/).respond();

			scalesService.getScale(123);

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['scales go here'];

			httpBackend.expectGET(/\/scales\/123$/).respond(response);

			scalesService.getScale(123);
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/scales\/123$/).respond(500, error);

			scalesService.getScale(123);
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});
});
