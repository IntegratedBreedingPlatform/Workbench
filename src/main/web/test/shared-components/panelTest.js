/*global angular, inject, expect*/
'use strict';

describe('Panel module', function() {
	var PANEL_CLASS = 'om-pa-wrapper-test',
		CONTENT_CLASS = 'om-pa-content-test',
		CONTENT = '<div class="' + CONTENT_CLASS + '"></div>',

		scope,
		panelService,
		directiveElement;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('panel'));

	beforeEach(inject(function($rootScope, _panelService_) {
		scope = $rootScope.$new();
		panelService = _panelService_;
	}));

	function compileDirective(attribute) {
		var attr = attribute || '';
		inject(function($compile) {
			directiveElement = $compile('<om-panel ' + attr + '>' + CONTENT + '</om-panel>')(scope);
		});
		scope.$digest();
	}

	it('should display any html inside the panel tags', function() {
		compileDirective();

		expect(directiveElement).toContainElement('.' + PANEL_CLASS);
		expect(directiveElement.find('.' + PANEL_CLASS)).toContainElement('.' + CONTENT_CLASS);
	});

	it('should be shown when the panel-visible attribute is set to true', function() {
		scope.panelName = 'panel';

		compileDirective('om-panel-identifier="panelName"');
		panelService.showPanel('panel');
		scope.$digest();
		expect(directiveElement).toHaveClass('om-pa-panel-visible');
	});

	it('should hide the panel when closePanel is called', function() {
		var isolateScope;
		scope.panelName = 'panel';

		compileDirective('om-panel-identifier="panelName"');
		isolateScope = directiveElement.isolateScope();

		panelService.showPanel('panel');
		scope.$digest();
		expect(directiveElement).toHaveClass('om-pa-panel-visible');

		isolateScope.closePanel({
			preventDefault: function() {}
		});
		scope.$digest();
		expect(panelService.getShownPanel()).toBe(null);
		expect(directiveElement).not.toHaveClass('om-pa-panel-visible');
	});

});
