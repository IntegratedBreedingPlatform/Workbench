/*global angular*/
'use strict';

(function() {
	
	var bmsAuth = angular.module('bmsAuth', ['LocalStorageModule']);
	
	bmsAuth.factory('authInterceptor', ['$rootScope', '$q', '$location', 'localStorageService', function($rootScope, $q, $location, localStorageService) {
		return {
			// Add authorization token to headers
			request: function(config) {
				config.headers = config.headers || {};
				var token = localStorageService.get('xAuthToken');

				if (token && token.expires && token.expires > new Date().getTime()) {
					config.headers['x-auth-token'] = token.token;
				}

				return config;
			}
		};
	}]);
	
})();
