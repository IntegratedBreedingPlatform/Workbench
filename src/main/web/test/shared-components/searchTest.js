/*global angular, inject, expect*/
'use strict';

describe('List module', function() {
	var scope,
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
	beforeEach(module('search'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope;
	}));

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-search om-model="searchText"></om-search>')(scope);
		});

		scope.$digest();
	}

	describe('scope.clearText', function() {
		it('should set the model to an empty string', function() {
			var isolateScope;

			scope.searchText = 'hi';

			compileDirective();
			isolateScope = directiveElement.isolateScope();

			isolateScope.clearText();
			expect(isolateScope.model).toEqual('');
		});
	});

});
