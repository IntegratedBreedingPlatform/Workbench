/*global angular, inject, expect*/
'use strict';

describe('List module', function() {
	var scope,
		ieUtilities,
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

	beforeEach(module('search', function($provide) {
		ieUtilities = {
			// Stub out the add IE clear input handler to call the callback when callCallback is invoked
			addIeClearInputHandler: function(element, callback) {
				this.callback = callback;
			},
			callCallback: function() {
				this.callback();
			}
		};
		// Provide mocks for the directive controller
		$provide.value('ieUtilities', ieUtilities);
	}));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope;
	}));

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-search om-model="searchText"></om-search>')(scope);
		});

		scope.$digest();
	}

	it('should clear the model when the clear input button is clicked in IE', function() {
		var isolateScope;

		compileDirective();
		isolateScope = directiveElement.isolateScope();
		isolateScope.model = 'text';
		ieUtilities.callCallback();

		expect(isolateScope.model).toEqual('');
	});

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
