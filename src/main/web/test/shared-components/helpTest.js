/*global expect, inject, jasmine, beforeEach*/
'use strict';

describe('Help Module', function() {
	var $window, $ngBootbox;

	beforeEach(function() {
		$window = {
			open: jasmine.createSpy()
		};

		$ngBootbox = {
			customDialog: jasmine.createSpy()
		};

		module('help', function($provide) {
			$provide.value('$window', $window);
			$provide.value('$ngBootbox', $ngBootbox);
		});
	});

	describe('omHelp', function() {

		var scope,
			compileDirective,
			isolateScope,
			directiveElement,
			$httpBackend;

		beforeEach(inject(function($injector) {
			scope = $injector.get('$rootScope').$new();
			$httpBackend = $injector.get('$httpBackend');
		}));

		compileDirective = function() {

			inject(function($compile) {
				directiveElement = $compile('<om-help module="MANAGE_ONTOLOGIES"/>')(scope);

				scope.$digest();

				isolateScope = directiveElement.isolateScope();
			});
		};

		// unit tests
		it('should go to online help if api call returns valid a help url', function() {
			$httpBackend.when('GET', '/ibpworkbench/controller/help/getUrl/MANAGE_ONTOLOGIES').respond('http://anyvalid.url');

			compileDirective();

			directiveElement.click();
			$httpBackend.flush();

			expect($window.open).toHaveBeenCalledWith('http://anyvalid.url');

		});

		it('should go to offline help if api call returns empty url', function() {
			$httpBackend.when('GET', '/ibpworkbench/controller/help/getUrl/MANAGE_ONTOLOGIES').respond('');

			// expect the ff apis are called
			$httpBackend.expect('GET', '/ibpworkbench/controller/help/headerText').respond();
			$httpBackend.expect('GET', '/ibpworkbench/VAADIN/themes/gcp-default/layouts/help_not_installed.html').respond();

			compileDirective();
			directiveElement.click();
			$httpBackend.flush();

			expect($ngBootbox.customDialog).toHaveBeenCalled();
		});
	});
});
