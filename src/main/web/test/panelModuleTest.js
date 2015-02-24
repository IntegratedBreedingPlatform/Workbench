/*global angular, inject, expect*/
'use strict';

describe('Panel module', function() {
	var PANEL_CLASS = 'om-pa-wrapper-test',
		CONTENT_CLASS = 'om-pa-content-test',
		CONTENT = '<div class="' + CONTENT_CLASS + '"></div>',

		scope,
		directiveElement;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('panel'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
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
		scope.showPanel = {
			show: true
		};
		compileDirective('om-visible="showPanel"');
		expect(directiveElement).toHaveClass('om-pa-panel-visible');
	});

});
