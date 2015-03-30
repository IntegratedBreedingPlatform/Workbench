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
				directiveElement = $compile('<div om-crop-name om-crop="maize"></div>')(scope);

				scope.$digest();

				isolateScope = directiveElement.isolateScope();
			});
		}));

		it('should set the crop name from the omCrop attribute', function() {
			expect(configService.getCropName()).toEqual('maize');
		});
	});
});
