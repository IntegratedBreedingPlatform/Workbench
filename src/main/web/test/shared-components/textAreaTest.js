/*global angular, expect, inject*/
'use strict';

describe('Text Area Module', function() {
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
		module('textArea');
	});

	beforeEach(function() {
		inject(function(_editable_) {
			editable = _editable_;
		});
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
		scope.model = {};
	}));

	describe('omSelect', function() {

		compileDirective = function() {
			inject(function($compile) {
				directiveElement = $compile('<om-text-area om-property="prop" om-model="model"></om-text-area>')(scope);

				scope.$digest();

				isolateScope = directiveElement.isolateScope();
			});
		};

		it('should set defaults for missing attributes', function() {
			compileDirective();
			expect(isolateScope.maxLength).toBe(-1);
		});
	});
});
