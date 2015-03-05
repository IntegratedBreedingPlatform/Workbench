/*global expect, inject, spyOn*/
'use strict';

describe('Data Types Service', function() {
	var dataTypesService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('dataTypes');
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
		inject(function(_dataTypesService_, $httpBackend) {
			dataTypesService = _dataTypesService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	describe('getDataTypes', function() {

		it('should GET /dataTypes', function() {

			httpBackend.expectGET(/\/dataTypes$/).respond();

			dataTypesService.getDataTypes();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['dataTypes go here'];

			httpBackend.expectGET(/\/dataTypes$/).respond(response);

			dataTypesService.getDataTypes();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/dataTypes$/).respond(500, error);

			dataTypesService.getDataTypes();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});
});
