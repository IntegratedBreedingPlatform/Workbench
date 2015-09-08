/*global angular, expect, inject*/
'use strict';

describe('Select Module', function() {
	var editable,
		scope,
		compileDirective,
		isolateScope,
		directiveElement;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(function() {
		module('templates');
		module('select');
	});

	beforeEach(function() {
		inject(function(_editable_) {
			editable = _editable_;
		});
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();

		scope.model = {};
		scope.types = [];
	}));

	describe('omSelect', function() {

		compileDirective = function(extraAttrs) {
			var attrs = extraAttrs || '';

			inject(function($compile) {
				directiveElement = $compile('<om-select om-property="prop" ng-model="model" om-options="types"' + attrs +
					'></om-select>')(scope);

				scope.$digest();

				isolateScope = directiveElement.isolateScope();
			});
		};

		it('should set defaults for missing attributes', function() {
			compileDirective();
			expect(isolateScope.required).toBe(false);
		});
	});

	describe('Select validation', function() {

		function compileForm(extraAttrs) {
			var attrs = extraAttrs || '';

			inject(function($compile) {
				$compile('<form name="testForm" novalidate>' +
							'<om-select name="omSelect" om-property="prop" ng-model="model" om-options="types"' + attrs + '></om-select>' +
						'</form>')(scope);
			});

			scope.$digest();
		}

		it('should set the widget to be valid if it is not required', function() {

			compileForm('om-required="false"');

			expect(scope.testForm.omSelect.$error).toEqual({});
			expect(scope.testForm.$valid).toBe(true);
		});

		it('should set the widget to be valid if it is required and a value is selected', function() {

			scope.model = {
				prop: 'someValue'
			};

			compileForm('om-required="true"');

			expect(scope.testForm.omSelect.$error).toEqual({});
			expect(scope.testForm.$valid).toBe(true);
		});

		it('should set the widget to be invalid if it is required and no value is selected', function() {

			compileForm('om-required="true"');

			expect(scope.testForm.omSelect.$error).toEqual({required: true});
			expect(scope.testForm.$valid).toBe(false);
		});
	});
});
