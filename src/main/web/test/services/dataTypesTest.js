/*global expect, inject, spyOn*/
'use strict';

describe('Data Types Service', function() {
	var dataTypesService,
		httpBackend,
		serviceUtilities;

	beforeEach(function() {
		module('dataTypes');
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

			httpBackend.expectGET(/\/datatypes$/).respond();

			dataTypesService.getDataTypes();

			httpBackend.flush();
		});

		it('should pass the result to the serviceUtilities.restSuccessHandler if a successful GET is made', function() {

			var response = ['dataTypes go here'];

			httpBackend.expectGET(/\/datatypes$/).respond(response);

			dataTypesService.getDataTypes();
			httpBackend.flush();

			expect(serviceUtilities.restSuccessHandler).toHaveBeenCalled();
			expect(serviceUtilities.restSuccessHandler.calls.mostRecent().args[0].data).toEqual(response);
			expect(serviceUtilities.restFailureHandler.calls.count()).toEqual(0);
		});

		it('should pass the result to the serviceUtilities.restFailureHandler if a successful GET is not made', function() {

			var error = 'Error!';

			httpBackend.expectGET(/\/datatypes$/).respond(500, error);

			dataTypesService.getDataTypes();
			httpBackend.flush();

			expect(serviceUtilities.restFailureHandler).toHaveBeenCalled();
			expect(serviceUtilities.restFailureHandler.calls.mostRecent().args[0].data).toEqual(error);
			expect(serviceUtilities.restSuccessHandler.calls.count()).toEqual(0);
		});
	});

	describe('getNonSystemDataTypes', function() {
		var deferredGetDataTypes,
			q,
			rootScope;

		beforeEach(inject(function($q, $rootScope) {
			q = $q;
			rootScope = $rootScope;

			dataTypesService.getDataTypes = function() {
				deferredGetDataTypes = q.defer();
				return deferredGetDataTypes.promise;
			};

			spyOn(dataTypesService, 'getDataTypes').and.callThrough();
		}));

		it('should filter out system data types and return the non system data types', function() {

			var systemType = {
					id: 1,
					name: 'system',
					systemDataType: true
				},
				nonSystemType = {
					id: 2,
					name: 'nonsystem',
					systemDataType: false
				},
				dataTypes = [systemType, nonSystemType],
				filteredTypesPromise,
				filteredDataTypes;

			filteredTypesPromise = dataTypesService.getNonSystemDataTypes();

			filteredTypesPromise.then(function(result) {
				filteredDataTypes = result;
			});

			deferredGetDataTypes.resolve(dataTypes);
			rootScope.$apply();

			expect(filteredDataTypes).toEqual([nonSystemType]);
		});

		it('should returned the failed response if unable to get the data types', function() {
			var filteredTypesPromise,
				rejectMessage = 'fail',
				response;

			filteredTypesPromise = dataTypesService.getNonSystemDataTypes();

			filteredTypesPromise.catch(function(result) {
				response = result;
			});

			deferredGetDataTypes.reject(rejectMessage);
			rootScope.$apply();

			expect(response).toEqual(rejectMessage);
		});
	});
});
