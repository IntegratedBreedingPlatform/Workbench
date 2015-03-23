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
			directiveElement = $compile('<om-range om-model="model" om-property="validValues"></om-range>')(scope);
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
});
