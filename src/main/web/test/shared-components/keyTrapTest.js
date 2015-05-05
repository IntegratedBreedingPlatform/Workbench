/*global angular, inject, expect, spyOn*/
'use strict';

describe('Key trap module', function() {
	var scope,
	doc,

	fakeEvent = {};

	beforeEach(module('keyTrap'));

	beforeEach(inject(function($rootScope, $document) {
		scope = $rootScope;
		doc = $document;
	}));

	describe('Key trap directive', function() {
		var directiveElement,
		ESC_KEYCODE = 27,
		NOT_ESC_KEYCODE = 13;

		function compileDirective() {

			inject(function($compile) {
				directiveElement = $compile('<div om-key-trap></div>')(scope);
			});
			scope.$digest();
		}

		beforeEach(function() {

		});

		afterEach(function() {
			doc.off();
		});

		it('should broadcast an escKeydown event if the esc key was pressed', function() {
			spyOn(scope, '$broadcast');
			compileDirective();

			fakeEvent.keyCode = ESC_KEYCODE;
			scope.escHandler(fakeEvent);

			expect(scope.$broadcast).toHaveBeenCalledWith('escKeydown', fakeEvent);
		});

		it('should not broadcast an escKeydown event if the any other than esc key was pressed', function() {
			spyOn(scope, '$broadcast');
			compileDirective();

			fakeEvent.keyCode = NOT_ESC_KEYCODE;
			scope.escHandler(fakeEvent);

			expect(scope.$broadcast).not.toHaveBeenCalled();
		});

		it('should remove the keydown escape handler on destroy', function() {
			spyOn(doc, 'off').and.callThrough();

			compileDirective();

			scope.$broadcast('$destroy');

			expect(doc.off).toHaveBeenCalledWith('keydown', scope.escHandler);
		});
	});
});
