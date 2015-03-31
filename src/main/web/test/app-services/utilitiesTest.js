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

	describe('formatErrorsForDisplay', function() {

		it('should return an empty array if there are no errors', function() {
			var responseNoErrors = {},
				responseEmptyErrors = {
					errors: []
				},
				resultNoErrors,
				resultEmptyErrors;

			resultNoErrors = serviceUtilities.formatErrorsForDisplay(responseNoErrors);
			resultEmptyErrors = serviceUtilities.formatErrorsForDisplay(responseEmptyErrors);

			expect(resultNoErrors).toEqual({});
			expect(resultEmptyErrors).toEqual({});
		});

		it('should append error messages not belonging to a specific field on the general property array', function() {
			var response = {
				errors: [{
					fieldNames: [],
					message: 'An error message'
				}]
			},
			result;

			result = serviceUtilities.formatErrorsForDisplay(response);

			expect(result).toEqual({
				general: ['An error message']
			});
		});

		it('should append error messages belonging to a specific field to the property with the same name as the field', function() {
			var response = {
				errors: [{
					fieldNames: ['field'],
					message: 'An error message'
				}]
			},
			result;

			result = serviceUtilities.formatErrorsForDisplay(response);

			expect(result).toEqual({
				field: ['An error message']
			});
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

		it('should return a rejected promise with a status and response data errors', function() {

			var response = {
					status: 400,
					data: {
						errors: []
					}
				},
				expected = {
					status: 400,
					errors: []
				},
				result;

			result = serviceUtilities.restFailureHandler(response);

			expect(result).toEqual(q.reject(expected));
		});
	});
});
