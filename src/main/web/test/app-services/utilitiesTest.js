/*global expect, inject, spyOn*/
'use strict';

describe('Utilities Service', function() {
	var serviceUtilities,
		q;

	beforeEach(function() {
		module('utilities');
	});

	beforeEach(function() {
		inject(function(_serviceUtilities_, $q) {
			serviceUtilities = _serviceUtilities_;
			q = $q;
		});
	});

	describe('genericAndRatherUselessErrorHandler', function() {

		it('should print an error message', function() {

			var error = 'this is an error message';

			spyOn(console, 'log');

			serviceUtilities.genericAndRatherUselessErrorHandler(error);

			expect(console.log).toHaveBeenCalledWith(error);
		});
	});

	describe('restSuccessHandler', function() {

		it('should return the data from the response', function() {

			var response = {
					data: 'some data'
				},
			result;

			result = serviceUtilities.restSuccessHandler(response);

			expect(result).toEqual(response.data);
		});
	});

	describe('restFailureHandler', function() {

		it('should return a rejected promise with a malformed request error message if the response status is 400', function() {

			var response = {
					status: 400
				},
				expectedMessage = 'Request was malformed.',
				result;

			result = serviceUtilities.restFailureHandler(response);

			expect(result).toEqual(q.reject(expectedMessage));
		});

		it('should return a rejected promise with an unknown error message if the response status is not 400', function() {

			var response = {
					status: 500
				},
				expectedMessage = 'An unknown error occurred.',
				result;

			result = serviceUtilities.restFailureHandler(response);

			expect(result).toEqual(q.reject(expectedMessage));
		});

		it('should return a rejected promise with an unknown error message if the response is undefined', function() {

			var response = null,
				expectedMessage = 'An unknown error occurred.',
				result;

			result = serviceUtilities.restFailureHandler(response);

			expect(result).toEqual(q.reject(expectedMessage));
		});
	});


});
