/*global expect, inject, spyOn, fail*/
'use strict';

describe('Utilities Service', function() {

	beforeEach(module('utilities'));

	describe('Service Utilities', function() {
		var serviceUtilities,
			q;

		beforeEach(function() {
			inject(function(_serviceUtilities_, $q) {
				serviceUtilities = _serviceUtilities_;
				q = $q;
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
		var formUtilities,
			rootScope,
			window,
			location,
			timeout,
			q;

		beforeEach(function() {
			inject(function(_formUtilities_, $rootScope, $window, $timeout, $location, $q) {

				formUtilities = _formUtilities_;
				rootScope = $rootScope;
				window = $window;
				location = $location;
				timeout = $timeout;
				q = $q;
			});
		});

		describe('formGroupClassGenerator', function() {

			it('should return a function', function() {
				expect(typeof formUtilities.formGroupClassGenerator()).toBe('function');
			});

			describe('the returned function', function() {

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

		describe('formParentHasError', function() {
			it('should set a has-error class to the returned class if the specified form field is invalid and the form is submitted',
					function() {

						var scope = {},
							formName = 'form',
							fieldName = 'field',

							returnedFunction = formUtilities.formParentHasError(scope, formName);

						scope[formName] = {
							$submitted: true
						};

						scope[formName][fieldName] = {
							$touched: false,
							$invalid: true
						};

						expect(returnedFunction(fieldName)).toEqual('has-error');
					});

			it('should set a has-error class to the returned class if the specified field has server errors and is not touched',
					function() {

						var scope = {},
							formName = 'form',
							fieldName = 'field',
							serverFieldName = 'serverField',

							returnedFunction = formUtilities.formParentHasError(scope, formName);

						scope.$parent = {};
						scope.$parent.serverErrors = {};
						scope.$parent.serverErrors[serverFieldName] = ['Something is wrong with this field'];

						scope[formName] = {
							$submitted: true
						};

						scope[formName][fieldName] = {
							$touched: false,
							$invalid: false
						};

						expect(returnedFunction(fieldName, serverFieldName)).toEqual('has-error');
					});
		});

		describe('cancelAddHandler', function() {

			var PATH = '/path',
				confirmation;

			beforeEach(function() {
				formUtilities.confirmationHandler = function() {
					confirmation = q.defer();
					return confirmation.promise;
				};

				spyOn(formUtilities, 'confirmationHandler').and.callThrough();
				spyOn(location, 'path');
			});

			it('should immediately go to the passed in location if the form is empty', function() {
				formUtilities.cancelAddHandler({}, false, PATH);
				expect(location.path).toHaveBeenCalledWith(PATH);
			});

			it('should set up a confirmation handler if the form is not empty', function() {
				var scope = {
						prop: 'some scope'
					};
				formUtilities.cancelAddHandler(scope, true, '');
				expect(formUtilities.confirmationHandler).toHaveBeenCalledWith(scope);
			});

			it('should go to the passed in location when the confirmation is recieved', function() {
				var scope = {
						prop: 'some scope'
					};
				formUtilities.cancelAddHandler(scope, true, PATH);

				confirmation.resolve();
				rootScope.$apply();

				expect(location.path).toHaveBeenCalledWith(PATH);
			});
		});

		describe('confirmationHandler', function() {

			it('should return a promise', function() {
				var confirmation = formUtilities.confirmationHandler({});
				expect(typeof confirmation).toEqual('object');
				expect(typeof confirmation.then).toEqual('function');
			});

			it('should set the confirmationNecessary property on the specified scope to true', function() {
				var scope = {};

				formUtilities.confirmationHandler(scope);
				expect(scope.confirmationNecessary).toBe(true);
			});

			it('should (if passed) set the specified property on the specified scope to true', function() {
				var scope = {};

				formUtilities.confirmationHandler(scope, 'cancelling');
				expect(scope.cancelling).toBe(true);
			});

			it('should append a confirm method on the scope that will resolve the returned promise', function(done) {
				var scope = {},
					confirmation;

				confirmation = formUtilities.confirmationHandler(scope);

				expect(typeof scope.confirm).toEqual('function');

				confirmation.then(done, fail);

				scope.confirm();
				rootScope.$apply();
			});

			it('should append a deny method on the scope that will reject the returned promise', function(done) {
				var scope = {},
					confirmation;

				confirmation = formUtilities.confirmationHandler(scope);

				expect(typeof scope.deny).toEqual('function');

				confirmation.then(fail, done);

				scope.deny();
				rootScope.$apply();
			});

			it('should call preventDefault if the confirm method is passed an event', function(done) {
				var scope = {},
					fakeEvent = { preventDefault: function() {} },
					confirmation;

				confirmation = formUtilities.confirmationHandler(scope);

				spyOn(fakeEvent, 'preventDefault');

				confirmation.then(function() {
					expect(fakeEvent.preventDefault).toHaveBeenCalled();
					done();
				}, fail);

				scope.confirm(fakeEvent);
				rootScope.$apply();
			});

			it('should call preventDefault if the deny method is passed an event', function(done) {
				var scope = {},
					fakeEvent = { preventDefault: function() {} },
					confirmation;

				confirmation = formUtilities.confirmationHandler(scope);

				spyOn(fakeEvent, 'preventDefault');

				confirmation.then(fail, function() {
					expect(fakeEvent.preventDefault).toHaveBeenCalled();
					done();
				});

				scope.deny(fakeEvent);
				rootScope.$apply();
			});

			it('should reset the confirmationNecessary state after the promise is fulfilled', function(done) {
				var scope = {},
					confirmation;

				confirmation = formUtilities.confirmationHandler(scope);

				confirmation.finally(function() {
					timeout.flush();
				}).finally(function() {
					expect(scope.confirmationNecessary).toBe(false);
					expect(scope.confirm).toBe(undefined);
					expect(scope.deny).toBe(undefined);
					done();
				});

				scope.confirm();
				rootScope.$apply();
			});

			it('should set the specified property (if passed) after the promise is fulfilled', function(done) {
				var scope = {},
					confirmation;

				confirmation = formUtilities.confirmationHandler(scope, 'cancelling');

				confirmation.finally(function() {
					timeout.flush();
				}).finally(function() {
					expect(scope.cancelling).toBe(false);
					done();
				});

				scope.confirm();
				rootScope.$apply();
			});
		});

	});

	describe('Collection Utilities', function() {
		var collectionUtilities;

		beforeEach(function() {
			inject(function(_collectionUtilities_) {
				collectionUtilities = _collectionUtilities_;
			});
		});

		describe('sortByName', function() {

			it('should sort the collection of objects in alphabetical order by the name property', function() {
				var A = {name: 'A'},
					B = {name: 'b'},
					C = {name: 'C'},
					collection = [B, C, A];

				collection = collectionUtilities.sortByName(collection);

				expect(collection).toEqual([A, B, C]);
			});
		});

		describe('formatListForDisplay', function() {

			it('should append the collection into a coma separated string by name property', function() {
				var A = {name: 'A'},
					B = {name: 'b'},
					C = {name: 'C'},
					collection = [A, B, C];

				expect(collectionUtilities.formatListForDisplay(collection)).toEqual('A, b, C');
			});
		});
	});

	describe('IE Utilities', function() {
		var ieUtilities,
			val = '',
			newVal = '',
			element = {
				bind: function(event, fn) {
					fn();
				},
				find: function() {
					return {
						val: function() {
							return val;
						}
					};
				}
			},
			timeout = function(fn) {
				val = newVal;
				fn();
			};

		beforeEach(module(function($provide) {
			$provide.value('$timeout', timeout);
		}));

		beforeEach(function() {
			inject(function(_ieUtilities_) {
				ieUtilities = _ieUtilities_;
			});
		});

		describe('addIeClearInputHandler', function() {

			it('should bind the mouseup event to the passed in element', function() {
				var element = {bind: function() {}};
				spyOn(element, 'bind');
				ieUtilities.addIeClearInputHandler(element, function() {});
				expect(element.bind).toHaveBeenCalled();
			});

			it('should not call the callback if the value of the input is empty when the mouseup event occurs on the input', function() {
				var count = 0;
				val = '';

				ieUtilities.addIeClearInputHandler(element, function() {count++;});
				expect(count).toBe(0);
			});

			it('should not call the callback if the user didn\'t click the x when the mouseup event occurs on the input', function() {
				var count = 0;
				val = 'thing';
				newVal = 'things';
				ieUtilities.addIeClearInputHandler(element, function() {count++;});
				expect(count).toBe(0);
			});

			it('should call the callback if the user clicks the x when the mouseup event occurs on the input', function() {
				var count = 0;
				val = 'thing';
				newVal = '';
				ieUtilities.addIeClearInputHandler(element, function() {count++;});
				expect(count).toBe(1);
			});
		});
	});

});
