/*global angular, inject, expect*/
'use strict';

describe('List module', function() {
	var PANEL_CLASS = 'om-pa-wrapper-test',
		CONTENT_CLASS = 'om-pa-content-test',
		CONTENT = '<div class="' + CONTENT_CLASS +'"></div>',

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
			directiveElement = $compile('<ompanel ' + attr + '>' + CONTENT + '</ompanel>')(scope);
		});
		scope.$digest();
	}

	it('should display any html inside the panel tags', function() {
		compileDirective();

		expect(directiveElement).toContainElement('.' + PANEL_CLASS);
		expect(directiveElement.find('.' + PANEL_CLASS)).toContainElement('.' + CONTENT_CLASS);
	});

	it('should be shown when the open-panel attribute is set to true', function() {
		compileDirective('om-panel-open="true"');
		expect(directiveElement).toHaveClass('om-pa-open');
	});

});
