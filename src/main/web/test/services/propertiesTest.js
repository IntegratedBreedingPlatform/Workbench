/*global expect, inject*/
'use strict';

describe('Properties Service', function() {
	var propertiesServiceUrl = 'http://private-905fc7-ontologymanagement.apiary-mock.com/properties',
		propertiesService,
		httpBackend;

	beforeEach(function() {
		module('properties');

		inject(function(_propertiesService_, $httpBackend) {
			propertiesService = _propertiesService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	it('should return an array of objects', function() {
		var properties = [{
				name: 'Prop1',
				description: 'This is prop1'
			}, {
				name: 'Prop2',
				description: 'This is prop2'
			}],
			result;

		httpBackend.expectGET(propertiesServiceUrl).respond(properties);

		propertiesService.getProperties().then(function(response) {
			result = response;
		});

		httpBackend.flush();

		expect(result instanceof Array).toBeTruthy();
		expect(result).toEqual(properties);
	});

	it('should return an error message when 500 response recieved', function() {
		var result;

		httpBackend.expectGET(propertiesServiceUrl).respond(500);

		propertiesService.getProperties().then(function(response) {
			result = response;
		}, function(reason) {
			result = reason;
		});

		httpBackend.flush();

		expect(result).toEqual('An unknown error occurred.');
	});

	it('should return an error message when 400 response recieved', function() {
		var result;

		httpBackend.expectGET(propertiesServiceUrl).respond(400);

		propertiesService.getProperties().then(function(response) {
			result = response;
		}, function(reason) {
			result = reason;
		});

		httpBackend.flush();

		expect(result).toEqual('Request was malformed.');
	});
});
