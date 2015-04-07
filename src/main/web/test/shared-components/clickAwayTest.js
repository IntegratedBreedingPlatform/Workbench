/*global angular, inject, expect, spyOn*/
'use strict';

describe('Click away module', function() {
	var scope,
		doc,
		isolateScope;

	beforeEach(module('clickAway'));

	beforeEach(inject(function($rootScope, $document) {
		scope = $rootScope;
		doc = $document;
	}));

	describe('Click anywhere but here directive', function() {
		var directiveElement,
			counter;

		function compileDirective() {

			inject(function($compile) {
				directiveElement = $compile('<div om-click-anywhere-but-here om-callback="callback()" om-enabled="enabled"></div>')(scope);
			});
			scope.$digest();
			isolateScope = directiveElement.isolateScope();
		}

		beforeEach(function() {
			counter = 0;

			scope.callback = function() {
				counter++;
			};
		});

		afterEach(function() {
			doc.off();
		});

		it('should call the callback on click if the event target is outside the element and enabled is true', function() {
			scope.enabled = true;

			compileDirective();

			doc.click();

			expect(counter).toBe(1);
		});


		it('should not call the callback on click if the event target is inside the element', function() {
			scope.enabled = true;

			compileDirective();

			directiveElement.click();

			expect(counter).toBe(0);
		});


		it('should not call the callback on click if enabled is false', function() {
			scope.enabled = false;

			compileDirective();

			doc.click();

			expect(counter).toBe(0);
		});

		it('should remove the click handler on destroy', function() {
			spyOn(doc, 'off').and.callThrough();

			compileDirective();

			scope.$broadcast('$destroy');

			expect(doc.off).toHaveBeenCalledWith('click', isolateScope.handler);
		});
	});
});
