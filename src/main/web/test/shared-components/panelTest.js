/*global angular, inject, expect*/
'use strict';

describe('Panel module', function() {
	var scope,
		panelService;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('panel'));

	beforeEach(inject(function($rootScope, _panelService_) {
		scope = $rootScope.$new();
		panelService = _panelService_;
	}));

	describe('Panel directive', function() {
		var PANEL_CLASS = 'om-pa-wrapper-test',
			CONTENT_CLASS = 'om-pa-content-test',
			CONTENT = '<div class="' + CONTENT_CLASS + '"></div>',

			directiveElement;

		function compileDirective(attribute) {
			var attr = attribute || '';
			inject(function($compile) {
				directiveElement = $compile('<om-panel ' + attr + '>' + CONTENT + '</om-panel>')(scope);
			});
			scope.$digest();
		}

		it('should display any content nested inside the panel tags', function() {
			compileDirective();

			expect(directiveElement).toContainElement('.' + PANEL_CLASS);
			expect(directiveElement.find('.' + PANEL_CLASS)).toContainElement('.' + CONTENT_CLASS);
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
			expect(panelService.getShownPanel()).toBe(null);
			expect(directiveElement).not.toHaveClass('om-pa-panel-visible');
		});
	});

	describe('Small Panel Directive', function() {

		it('should give the panel a smaller width', function() {
			var directiveElement;

			inject(function($compile) {
				directiveElement = $compile('<om-panel om-panel-small>Content</div>')(scope);
			});
			scope.$digest();

			expect(directiveElement).toHaveClass('om-pa-panel-small');
		});
	});

	describe('Panel Mask Directive', function() {
		var MASK_CLASS = 'om-pa-mask-test',
			CONTENT_CLASS = 'masked-content',
			CONTENT = '<div class="' + CONTENT_CLASS + '">Content</div>',
			directiveElement;

		function compileDirective() {
			inject(function($compile) {
				scope.panelName = 'panel';
				directiveElement = $compile('<div class="maskParent" om-mask-for-panel="panelName">' + CONTENT + '</div>')(scope);
			});
			scope.$digest();
		}

		it('should display any html inside the panel tags', function() {
			compileDirective();
			expect(directiveElement).toContainElement('.' + MASK_CLASS);
			expect(directiveElement).toContainElement('.' + CONTENT_CLASS);
		});

		it('should be shown when showPanel is called with the panel\'s identifier ', function() {
			compileDirective();

			panelService.showPanel('panel');
			scope.$digest();

			expect(directiveElement).toHaveClass('om-pa-mask-visible');
		});

		it('should be hidden when showPanel is called with a different panel\'s identifier ', function() {
			compileDirective();

			panelService.showPanel('differentPanel');

			scope.$digest();
			expect(directiveElement).not.toHaveClass('om-pa-mask-visible');
		});
	});

	describe('Panel Service', function() {

		it('should set the shown panel to the panel passed to showPanel', function() {
			panelService.showPanel('thisPanel');
			expect(panelService.getShownPanel()).toBe('thisPanel');
		});

		it('should set the shown panel to null when hidePanel is called', function() {
			panelService.showPanel('thisPanel');
			panelService.hidePanel();
			expect(panelService.getShownPanel()).toBe(null);
		});
	});
});
