/*global angular, inject, expect*/
'use strict';

describe('Error list module', function() {
	var TRANSLATIONS = {
			'error.message': 'translated error message'
		},
		scope,
		directiveElement;

	function compileDirective(attrs) {
		inject(function($compile) {
			directiveElement = $compile('<om-error-list ' + attrs + '"></om-error-list>')(scope);
		});

		scope.$digest();
	}

	beforeEach(function() {

		angular.mock.module('templates');

		module('errorList', function($translateProvider) {

			$translateProvider
				.translations('en', TRANSLATIONS)
				.preferredLanguage('en');
		});
	});

	beforeEach(inject(function($rootScope) {
		scope = $rootScope;
	}));

	it('should default translatedErrors to an empty array', function() {
		var isolateScope;

		compileDirective();
		isolateScope = directiveElement.isolateScope();

		expect(isolateScope.translatedErrors).toEqual([]);
	});

	it('should default errors to an empty array', function() {
		var isolateScope;

		compileDirective();
		isolateScope = directiveElement.isolateScope();

		expect(isolateScope.errors).toEqual([]);
	});

	it('should set errorMessages to an empty array if both errorsToTranslate and errorsAlreadyTranslated are falsy', function() {
		var isolateScope;

		scope.errorsToTranslate = null;
		scope.errorsAlreadyTranslated = null;

		compileDirective('om-errors-to-translate="errorsToTranslate" om-errors-already-translated="errorsAlreadyTranslated"');
		isolateScope = directiveElement.isolateScope();

		expect(isolateScope.errorMessages).toEqual([]);
	});

	it('should translate any errors that need translating', function() {
		var isolateScope;

		scope.errorsToTranslate = ['error.message'];
		compileDirective('om-errors-to-translate="errorsToTranslate"');

		isolateScope = directiveElement.isolateScope();
		scope.$apply();

		expect(isolateScope.errorMessages).toEqual(['translated error message']);
	});

	it('should combine error messages from those that need translating and those that don\'t', function() {
		var isolateScope;

		scope.errorsToTranslate = ['error.message'];
		scope.errorsAlreadyTranslated = ['error message'];
		compileDirective('om-errors-to-translate="errorsToTranslate" om-errors-already-translated="errorsAlreadyTranslated"');

		isolateScope = directiveElement.isolateScope();
		scope.$apply();

		expect(isolateScope.errorMessages.length).toBe(2);
		expect(isolateScope.errorMessages).toContain('translated error message');
		expect(isolateScope.errorMessages).toContain('error message');
	});

});
