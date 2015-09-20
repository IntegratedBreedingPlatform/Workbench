/*global angular, inject, expect*/
'use strict';

describe('Range module', function() {
	var scope,
		isolateScope,
		directiveElement,
		mockTranslateFilter;

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-range ng-model="model" om-property="validValues" om-numeric="true"></om-range>')(scope);
		});

		scope.$digest();

		isolateScope = directiveElement.isolateScope();
	}

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};

		angular.mock.module('templates');

		module('range');
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	it('should instantiate the specified property on the model if not otherwise provided', function() {
		scope.model = {};

		compileDirective();

		expect(scope.model.validValues).not.toBeUndefined();
	});

	it('should do nothing if there\'s no model', function() {

		compileDirective();

		expect(scope.model).toBeUndefined();
	});

	it('should set the read only text to full range if both the min and max are set', function() {
		scope.model = {
			validValues: {
				min: '80',
				max: '90'
			}
		};
		compileDirective();

		expect(isolateScope.readOnlyRangeText).toEqual('range.fullRange');
	});

	it('should set the read only text to min only if the min is set and the max is not', function() {
		scope.model = {
			validValues: {
				min: '0'
			}
		};
		compileDirective();

		expect(isolateScope.readOnlyRangeText).toEqual('range.minOnly');
	});

	it('should set the read only text to max only if the max is set and the min is not', function() {
		scope.model = {
			validValues: {
				max: '-1'
			}
		};
		compileDirective();

		expect(isolateScope.readOnlyRangeText).toEqual('range.maxOnly');
	});

	it('should set the read only text to no range if nither the min or max are set', function() {
		scope.model = {
			validValues: {}
		};
		compileDirective();

		expect(isolateScope.readOnlyRangeText).toEqual('range.noRange');
	});

	describe('Range validation', function() {

		function compileForm(extraAttrs) {
			var attrs = extraAttrs || '';

			inject(function($compile) {
				directiveElement = $compile(
					'<form name="testForm" novalidate>' +
						'<om-range name="omRange" ng-model="model" om-property="validValues" ' + attrs + '></om-range>' +
					'</form>'
					)(scope);
			});

			scope.$digest();
		}

		it('should set the widget to be valid if the selected data type is not numeric', function() {

			compileForm('om-numeric="false"');

			expect(scope.testForm.omRange.$error).toEqual({});
			expect(scope.testForm.$valid).toBe(true);
		});

		it('should validate the widget if it changes to become numeric and already has values', function() {
			scope.model = {
				validValues: {
					min: '80',
					max: '90'
				}
			};
			scope.numeric = true;

			compileForm('om-numeric="numeric"');

			scope.numeric = false;
			scope.$apply();

			scope.numeric = true;
			scope.$apply();

			expect(scope.testForm.omRange.$error).toEqual({});
			expect(scope.testForm.$valid).toBe(true);
		});

		it('should set the widget to be valid if the specified model is undefined or null', function() {

			scope.model = undefined;
			compileForm('om-numeric="true"');

			expect(scope.testForm.$valid).toBe(true);

			scope.model = null;
			compileForm('om-numeric="true"');

			expect(scope.testForm.$valid).toBe(true);
		});

		it('should set the minTooBig error to be true if a minimum is provided and it exceeds the maximum', function() {

			scope.model = {
				validValues: {
					min: '90',
					max: '80'
				}
			};

			compileForm('om-numeric="true"');

			expect(scope.testForm.omRange.$error).toEqual({minTooBig: true});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the minOutOfRange error to be true if the minimum falls below the allowed range', function() {

			scope.model = {
				validValues: {
					min: '80'
				}
			};

			compileForm('om-numeric="true" om-min="90" om-max="100"');

			expect(scope.testForm.omRange.$error.minOutOfRange).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the minOutOfRange error to be true if the minimum falls above the allowed range', function() {

			scope.model = {
				validValues: {
					min: '80'
				}
			};

			compileForm('om-numeric="true" om-min="60" om-max="70"');

			expect(scope.testForm.omRange.$error.minOutOfRange).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the maxOutOfRange error to be true if the maximum falls below the allowed range', function() {

			scope.model = {
				validValues: {
					max: '80'
				}
			};

			compileForm('om-numeric="true" om-min="90" om-max="100"');

			expect(scope.testForm.omRange.$error.maxOutOfRange).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the maxOutOfRange error to be true if the maximum falls above the allowed range', function() {

			scope.model = {
				validValues: {
					max: '80'
				}
			};

			compileForm('om-numeric="true" om-min="60" om-max="70"');

			expect(scope.testForm.omRange.$error.maxOutOfRange).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the minNaN error if the minimum value is not a valid number', function() {

			var directiveScope;

			compileForm('om-numeric="true"');

			scope.model = {};

			directiveScope = directiveElement.find('ng-form').scope();
			directiveScope.rangeForm.omRangeMin = {
				$error: {
					number: true
				}
			};
			scope.$apply();

			expect(scope.testForm.omRange.$error.minNaN).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the maxNaN error if the maximum value is not a valid number', function() {

			var directiveScope;

			compileForm('om-numeric="true"');

			directiveScope = directiveElement.find('ng-form').scope();
			directiveScope.rangeForm.omRangeMax = {
				$error: {
					number: true
				}
			};
			scope.$apply();

			expect(scope.testForm.omRange.$error.maxNaN).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should not cause validation errors if the widget is not yet instantiated', function() {

			var directiveScope;

			compileForm('om-numeric="true"');

			directiveScope = directiveElement.find('ng-form').scope();
			directiveScope.rangeForm = null;
			scope.$apply();

			// Make a change to the model and ensure no errors happen if the embedded form is not yet present
			scope.model = {
				validValues: {
					max: 80
				}
			};
			scope.$apply();

			expect(scope.testForm.omRange.$error.maxNaN).toBeFalsy();
			expect(scope.testForm.omRange.$error.minNaN).toBeFalsy();
			expect(scope.testForm.$valid).toBe(true);
		});

		it('should set the parent form to be touched if the range minimum is touched', function() {

			var directiveScope;

			compileForm('om-numeric="true"');

			directiveScope = directiveElement.find('ng-form').scope();

			directiveScope.rangeForm.omRangeMin.$touched = true;
			scope.$apply();

			expect(scope.testForm.omRange.$touched).toBe(true);
		});

		it('should set the parent form to be touched if the range maximum is touched', function() {

			var directiveScope;

			compileForm('om-numeric="true"');

			directiveScope = directiveElement.find('ng-form').scope();

			directiveScope.rangeForm.omRangeMax.$touched = true;
			scope.$apply();

			expect(scope.testForm.omRange.$touched).toBe(true);
		});
	});

	describe('scope.clearRange', function() {

		it('should clear the textual min if it exists', function() {
			compileDirective();

			isolateScope.model = {
				validValues: {
					min: '5'
				}
			};

			isolateScope.clearRange();

			expect(isolateScope.model.validValues.min).toEqual('');
		});

		it('should clear the textual max if it exists', function() {
			compileDirective();

			isolateScope.model = {
				validValues: {
					max: '10'
				}
			};

			isolateScope.clearRange();

			expect(isolateScope.model.validValues.max).toEqual('');
		});

		it('should clear the numeric min if it exists', function() {
			compileDirective();

			isolateScope.rangeModel = {
				min: 5
			};

			isolateScope.clearRange();

			expect(isolateScope.rangeModel.min).toBe(null);
		});

		it('should clear the numeric max if it exists', function() {
			compileDirective();

			isolateScope.rangeModel = {
				max: 10
			};

			isolateScope.clearRange();

			expect(isolateScope.rangeModel.max).toBe(null);
		});

		it('should reset the read only text message', function() {
			compileDirective();

			isolateScope.readOnlyRangeText = 'range.maxOnly';
			isolateScope.clearRange();
			expect(isolateScope.readOnlyRangeText).toEqual('range.noRange');
		});
	});

});
