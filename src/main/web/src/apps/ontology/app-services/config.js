/*global angular*/
'use strict';

(function() {
	var app = angular.module('config', []);

	app.service('configService', function() {

		var cropName = '';

		return {
			setCropName: function(crop) {
				cropName = crop;
			},

			getCropName: function() {
				return cropName;
			}
		};
	});

	app.directive('omCropName', function(configService) {
		return {
			restrict: 'A',
			scope: {
				cropName: '@omCrop'
			},
			link: function(scope, element, attrs) {
				configService.setCropName(attrs.omCrop);
			}
		};
	});
}());
