/*global expect, inject, spyOn*/
'use strict';

describe('test.variablesModuleTest', function() {
	var data = [{
			name: 'var1',
			description: 'var1 description'
		}],
		q,
		controller,
		scope,
		deferred,
		variablesService;

	beforeEach(function() {
		module('variables');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {
		variablesService = {
			getVariables: function() {
				deferred = q.defer();
				return deferred.promise;
			}
		};
		spyOn(variablesService, 'getVariables').and.callThrough();

		q = $q;
		scope = $rootScope;
		controller = $controller('VariablesController', {
			variablesService: variablesService
		});
	}));

	it('should retrieve variables from the variablesService', function() {
		deferred.resolve(data);
		scope.$apply();
		expect(variablesService.getVariables).toHaveBeenCalled();
		expect(controller.variables).toEqual(data);
	});

});

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

	it('Should return an array of objects', function() {
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

	it('Should reject the promise when 500 response recieved', function() {
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

	it('Should reject the promise when 400 response recieved', function() {
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
