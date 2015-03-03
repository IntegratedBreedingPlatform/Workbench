/*global expect, inject*/
'use strict';

describe('Variables Service', function() {
	var variablesServiceUrl = 'http://private-905fc7-ontologymanagement.apiary-mock.com/variables',
		variablesService,
		httpBackend;

	beforeEach(function() {
		module('variables');

		inject(function(_variablesService_, $httpBackend) {
			variablesService = _variablesService_;
			httpBackend = $httpBackend;
		});
	});

	afterEach(function() {
		httpBackend.verifyNoOutstandingExpectation();
		httpBackend.verifyNoOutstandingRequest();
	});

	it('should return an array of objects', function() {
		var variables = [{
				name: 'Var1',
				description: 'This is var1'
			}, {
				name: 'Var2',
				description: 'This is var2'
			}],
			result;

		httpBackend.expectGET(variablesServiceUrl).respond(variables);

		variablesService.getVariables().then(function(response) {
			result = response;
		});

		httpBackend.flush();

		expect(result instanceof Array).toBeTruthy();
		expect(result).toEqual(variables);
	});

	it('should return an error message when 500 response recieved', function() {
		var result;

		httpBackend.expectGET(variablesServiceUrl).respond(500);

		variablesService.getVariables().then(function(response) {
			result = response;
		}, function(reason) {
			result = reason;
		});

		httpBackend.flush();

		expect(result).toEqual('An unknown error occurred.');
	});

	it('should return an error message when 400 response recieved', function() {
		var result;

		httpBackend.expectGET(variablesServiceUrl).respond(400);

		variablesService.getVariables().then(function(response) {
			result = response;
		}, function(reason) {
			result = reason;
		});

		httpBackend.flush();

		expect(result).toEqual('Request was malformed.');
	});
});
