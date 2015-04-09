/*global angular, inject, expect*/
'use strict';

describe('Range module', function() {
	var scope,
		isolateScope,
		directiveElement,
		mockTranslateFilter;

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};
	});

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('range'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-range ng-model="model" om-property="validValues"></om-range>')(scope);
		});

		scope.$digest();

		isolateScope = directiveElement.isolateScope();
	}

	it('should instantiate the specified property on the model if not otherwise provided', function() {

		scope.model = {};
		expect(scope.model.validValues).toBeUndefined();

		compileDirective();

		expect(scope.model.validValues).not.toBeUndefined();
	});

	it('should do nothing if there\'s no model', function() {

		compileDirective();

		expect(scope.model).toBeUndefined();
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
					min: 90,
					max: 80
				}
			};

			compileForm('om-numeric="true"');

			expect(scope.testForm.omRange.$error).toEqual({minTooBig: true});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the minOutOfRange error to be true if the minimum falls below the allowed range', function() {

			scope.model = {
				validValues: {
					min: 80
				}
			};

			compileForm('om-numeric="true" om-min="90" om-max="100"');

			expect(scope.testForm.omRange.$error.minOutOfRange).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the minOutOfRange error to be true if the minimum falls above the allowed range', function() {

			scope.model = {
				validValues: {
					min: 80
				}
			};

			compileForm('om-numeric="true" om-min="60" om-max="70"');

			expect(scope.testForm.omRange.$error.minOutOfRange).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the maxOutOfRange error to be true if the maximum falls below the allowed range', function() {

			scope.model = {
				validValues: {
					max: 80
				}
			};

			compileForm('om-numeric="true" om-min="90" om-max="100"');

			expect(scope.testForm.omRange.$error.maxOutOfRange).toBe(true);
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the maxOutOfRange error to be true if the maximum falls above the allowed range', function() {

			scope.model = {
				validValues: {
					max: 80
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
});
