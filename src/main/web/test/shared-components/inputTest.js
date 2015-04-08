/*global angular, expect, inject*/
'use strict';

describe('Input Module', function() {
	var editable;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(function() {
		module('templates');
		module('input');
	});

	beforeEach(function() {
		inject(function(_editable_) {
			editable = _editable_;
		});
	});

	describe('omInput', function() {

		var scope,
			compileDirective,
			isolateScope,
			directiveElement;

		beforeEach(inject(function($rootScope) {
			scope = $rootScope.$new();
		}));

		compileDirective = function(extraAttrs) {
			var attrs = extraAttrs || '';

			inject(function($compile) {
				directiveElement = $compile('<om-input om-name="name" om-label="label" om-property="name" om-model="model"' + attrs +'>' +
					'</om-input>')(scope);

				scope.$digest();

				isolateScope = directiveElement.isolateScope();
			});
		};

		it('should set defaults for missing attributes', function() {
			compileDirective();
			expect(isolateScope.maxLength).toEqual(-1);
			expect(isolateScope.required).toBe(false);
			expect(isolateScope.regex).toEqual(/[\s\S]*/);
		});

		it('should create a regular expression from any string passed into the pattern attribute', function() {
			compileDirective('om-pattern="^[a-zA-Z_%]$"');
			expect(isolateScope.regex).toEqual(/^[a-zA-Z_%]$/);
		});
	});
});
