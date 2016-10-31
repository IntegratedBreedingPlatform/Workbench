/*global angular*/
'use strict';

(function() {
	var app = angular.module('config', []);

	app.service('configService', function() {

		var cropName = '',
			programId = '',
			selectedProjectId = '',
			authToken = '',
			loggedInUserId = '';

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
			},

			setSelectedProjectId: function(projectId) {
				selectedProjectId = projectId;
			},

			getSelectedProjectId: function() {
				return selectedProjectId;
			},

			setAuthToken: function(token) {
				authToken = token;
			},

			getAuthToken: function() {
				return authToken;
			},

			setLoggedInUserId: function(userId) {
				loggedInUserId = userId;
			},

			getLoggedInUserId: function() {
				return loggedInUserId;
			}
		};
	});

	app.directive('omConfig', ['configService', function(configService) {
		return {
			restrict: 'A',
			scope: {
				cropName: '@omCrop',
				programId: '@omProgramId',
				selectedProjectId: '@omSelectedProjectId',
				authToken: '@omAuthToken',
				loggedInUserId: '@omLoggedInUserId'
			},
			link: function(scope, element, attrs) {
				configService.setCropName(attrs.omCrop);
				configService.setProgramId(attrs.omProgramId);
				configService.setSelectedProjectId(attrs.omSelectedProjectId);
				configService.setAuthToken(attrs.omAuthToken);
				configService.setLoggedInUserId(attrs.omLoggedInUserId);
			}
		};
	}]);
}());
