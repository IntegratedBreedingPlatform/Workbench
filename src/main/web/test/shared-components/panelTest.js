/*global angular, inject, expect, spyOn*/
'use strict';

describe('Panel module', function() {
	var scope,
		panelService,
		mockTranslateFilter;

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};

		angular.mock.module('templates');

		module('panel');
	});

	beforeEach(inject(function($rootScope, _panelService_) {
		scope = $rootScope.$new();
		panelService = _panelService_;
	}));

	describe('Panel Directive', function() {
		var CONTENT_CLASS = 'om-pa-content-test',
			CONTENT = '<div class="' + CONTENT_CLASS + '"></div>',

			directiveElement,
			timeout;

		function compileDirective(attribute) {
			var attr = attribute || '';
			inject(function($compile) {
				directiveElement = $compile('<om-panel ' + attr + '>' + CONTENT + '</om-panel>')(scope);
			});
			scope.$digest();
		}

		beforeEach(inject(function($timeout) {
			timeout = $timeout;
		}));

		it('should display any content nested inside the panel tags', function() {
			compileDirective();
			expect(directiveElement).toContainElement('.' + CONTENT_CLASS);
		});

		it('should be shown when showPanel is called with the panel\'s identifier ', function() {
			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			panelService.showPanel('panel');
			scope.$digest();

			expect(directiveElement).toHaveClass('om-pa-panel-visible');
		});

		it('should be hidden when showPanel is called with a different panel\'s identifier ', function() {
			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			panelService.showPanel('differentPanel');

			scope.$digest();
			expect(directiveElement).not.toHaveClass('om-pa-panel-visible');
		});

		it('should hide the panel when closePanel is called', function() {
			var isolateScope;

			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			// Ensure the panel is shown first
			panelService.showPanel('panel');
			scope.$digest();
			expect(directiveElement).toHaveClass('om-pa-panel-visible');

			isolateScope = directiveElement.isolateScope();
			isolateScope.closePanel({
				preventDefault: function() {}
			});
			scope.$digest();
			expect(panelService.getCurrentPanel()).toBe(null);
			expect(directiveElement).not.toHaveClass('om-pa-panel-visible');
		});

		it('should show the throbber if the panel is opened', function() {
			var isolateScope;

			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			panelService.showPanel('panel');
			scope.$digest();

			isolateScope = directiveElement.isolateScope();

			timeout.flush();
			expect(isolateScope.showThrobber).toBe(true);
		});

		it('should not show the throbber the panel is closed', function() {
			var isolateScope;

			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			panelService.showPanel('panel');
			scope.$digest();

			isolateScope = directiveElement.isolateScope();
			isolateScope.closePanel({preventDefault: function() {}});

			timeout.flush();
			expect(isolateScope.showThrobber).toBeFalsy();
		});

		it('should close the panel if esc key is pressed and panel is opened', function() {
			var isolateScope;

			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			panelService.showPanel('panel');
			scope.$digest();

			isolateScope = directiveElement.isolateScope();
			spyOn(isolateScope, 'closePanel');
			isolateScope.escHandler('escKeydown', {preventDefault: function() {}});

			expect(isolateScope.closePanel).toHaveBeenCalled();
		});

		it('should not close the panel if esc key is pressed and panel is already closed', function() {
			var isolateScope;

			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			panelService.showPanel('panel');
			scope.$digest();
			panelService.hidePanel();
			scope.$digest();

			isolateScope = directiveElement.isolateScope();
			spyOn(isolateScope, 'closePanel');
			isolateScope.escHandler('escKeydown', {preventDefault: function() {}});

			expect(isolateScope.closePanel).not.toHaveBeenCalled();
		});

		it('should remove the escape handler on destroy', function() {
			var isolateScope;

			scope.panelName = 'panel';
			compileDirective('om-panel-identifier="panelName"');

			isolateScope = directiveElement.isolateScope();
			spyOn(isolateScope, 'removeEscHandler');

			isolateScope.$broadcast('$destroy');

			expect(isolateScope.removeEscHandler).toHaveBeenCalled();
		});
	});

	describe('Panel Mask Directive', function() {
		var directiveElement;

		function compileDirective() {
			inject(function($compile) {
				scope.panelName = 'panel';
				directiveElement = $compile('<om-mask></om-mask>')(scope);
			});
			scope.$digest();
		}

		it('should be shown when showPanel is called', function() {
			compileDirective();

			panelService.showPanel('panel');
			scope.$digest();

			expect(directiveElement).toHaveClass('om-mask-visible');
		});

		it('should be hidden when hidePanel is called', function() {
			compileDirective();

			panelService.hidePanel();

			scope.$digest();
			expect(directiveElement).not.toHaveClass('om-mask-visible');
		});
	});

	describe('Panel Service', function() {

		it('should set the shown panel to the panel passed to showPanel', function() {
			panelService.showPanel('thisPanel');
			expect(panelService.getCurrentPanel()).toBe('thisPanel');
		});

		it('should set the shown panel to null when hidePanel is called', function() {
			panelService.showPanel('thisPanel');
			panelService.hidePanel();
			expect(panelService.getCurrentPanel()).toBe(null);
		});
	});
});
