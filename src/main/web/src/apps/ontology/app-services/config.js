/*global angular*/
'use strict';

(function() {
	var app = angular.module('config', []);

	app.service('configService', function() {

		var cropName = '',
			programId = '';

		return {
			setCropName: function(crop) {
				cropName = crop;
			},

			getCropName: function() {
				return cropName;
			},

			setProgramId: function(id) {
				programId = id;
			},

			getProgramId: function() {
				return programId;
			}
		};
	});

	app.directive('omConfig', function(configService) {
		return {
			restrict: 'A',
			scope: {
				cropName: '@omCrop',
				programId: '@omProgramId'
			},
			link: function(scope, element, attrs) {
				configService.setCropName(attrs.omCrop);
				configService.setProgramId(attrs.omProgramId);
			}
		};
	});
}());
