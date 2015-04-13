/*global expect, inject, spyOn*/
'use strict';

describe('Utilities Service', function() {
	var serviceUtilities,
		formUtilities,
		q;

	beforeEach(function() {
		module('utilities');
	});

	beforeEach(function() {
		inject(function(_serviceUtilities_, _formUtilities_, $q) {
			serviceUtilities = _serviceUtilities_;
			formUtilities = _formUtilities_;
			q = $q;
		});
	});

	describe('Service Utilities', function() {

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

	describe('Form Utilities', function() {
		describe('formGroupClassGenerator', function() {

			it('should return a function', function() {
				expect(typeof formUtilities.formGroupClassGenerator()).toBe('function');
			});

			describe('the returned function', function(){

				it('should return a form-group class if the specified form is not yet initialised', function() {
					var scope = {},
						formName = 'form',

						returnedFunction = formUtilities.formGroupClassGenerator(scope, formName);

					expect(returnedFunction()).toEqual('form-group');
				});

				it('should return a form-group class if the specified form field is not yet initialised', function() {
					var scope = {},
						formName = 'form',
						fieldName = 'field',

						returnedFunction = formUtilities.formGroupClassGenerator(scope, formName);

					scope[formName] = {
						$submitted: false
					};

					expect(returnedFunction(fieldName)).toEqual('form-group');
				});

				it('should return a form-group class if the form is not submitted and the input not touched', function() {
					var scope = {},
						formName = 'form',
						fieldName = 'field',

						returnedFunction = formUtilities.formGroupClassGenerator(scope, formName);

					scope[formName] = {
						$submitted: false
					};

					scope[formName][fieldName] = {
						$touched: false,
						$invalid: true
					};

					expect(returnedFunction(fieldName)).toEqual('form-group');
				});

				it('should append a has-error class to the returned class if the specified form field is invalid and the form is submitted',
					function() {

					var scope = {},
						formName = 'form',
						fieldName = 'field',

						returnedFunction = formUtilities.formGroupClassGenerator(scope, formName);

					scope[formName] = {
						$submitted: true
					};

					scope[formName][fieldName] = {
						$touched: false,
						$invalid: true
					};

					expect(returnedFunction(fieldName)).toEqual('form-group has-error');
				});

				it('should append a has-error class to the returned class if the specified form field is invalid and the field is touched',
					function() {

					var scope = {},
						formName = 'form',
						fieldName = 'field',

						returnedFunction = formUtilities.formGroupClassGenerator(scope, formName);

					scope[formName] = {
						$submitted: false
					};

					scope[formName][fieldName] = {
						$touched: true,
						$invalid: true
					};

					expect(returnedFunction(fieldName)).toEqual('form-group has-error');
				});

				it('should append a has-error class to the returned class if the specified field has server errors and is touched',
					function() {

					var scope = {},
						formName = 'form',
						fieldName = 'field',
						serverFieldName = 'serverField',

						returnedFunction = formUtilities.formGroupClassGenerator(scope, formName);

					scope.serverErrors = {};
					scope.serverErrors[serverFieldName] = ['Something is wrong with this field'];

					scope[formName] = {
						$submitted: true
					};

					scope[formName][fieldName] = {
						$touched: false,
						$invalid: false
					};

					expect(returnedFunction(fieldName, serverFieldName)).toEqual('form-group has-error');
				});
			});
		});
	});
});
