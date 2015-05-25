/*global angular, expect, inject, spyOn*/
'use strict';

describe('Config Module', function() {

	var configService;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(function() {
		module('templates');
		module('config');
	});

	beforeEach(function() {
		inject(function(_configService_) {
			configService = _configService_;
		});

		spyOn(configService, 'setCropName').and.callThrough();
	});

	describe('cropName Directive', function() {

		var isolateScope;

		beforeEach(inject(function($rootScope) {
			var scope,
				directiveElement;

			scope = $rootScope.$new();

			inject(function($compile) {
				directiveElement = $compile('<div om-config om-crop="maize" om-program-id="284d56e4-189f-4da4-b152-8a6ac1476ae0">' +
					'</div>')(scope);

				scope.$digest();

				isolateScope = directiveElement.isolateScope();
			});
		}));

		it('should set the crop name from the omCrop attribute', function() {
			expect(configService.getCropName()).toEqual('maize');
		});

		it('should set the program id from the omProgramId attribute', function() {
			expect(configService.getProgramId()).toEqual('284d56e4-189f-4da4-b152-8a6ac1476ae0');
		});
	});
});
