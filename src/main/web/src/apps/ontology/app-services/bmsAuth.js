/*global angular*/
'use strict';

(function() {

	// LocalStorageModule is an Angular module we depend on: https://github.com/grevory/angular-local-storage.
	var bmsAuth = angular.module('bmsAuth', ['LocalStorageModule']);

	bmsAuth.factory('authInterceptor', ['localStorageService', function(localStorageService) {
		return {
			// Add authorization token to headers
			request: function(config) {
				config.headers = config.headers || {};
				/**
				 * BMSAPI x-auth-token is stored in local storage service as "bms.xAuthToken" see login.js.
				 * The prefix "bms" is configured in ontology.js as part of app.config:
				 *     localStorageServiceProvider.setPrefix('bms');
				 */
				var token = localStorageService.get('xAuthToken');

				if (token && token.expires && token.expires > new Date().getTime()) {
					config.headers['x-auth-token'] = token.token;
				}

				return config;
			}
		};
	}]);

	bmsAuth.factory('authExpiredInterceptor', ['$q', 'localStorageService', '$rootScope',
		function($q, localStorageService, $rootScope) {
		return {
			responseError: function(response) {
				// Token has expired or is invalid.
				if (response.status === 401 && (response.data.error === 'invalid_token' || response.data.error === 'Unauthorized')) {
					/**
					 * BMSAPI x-auth-token is stored in local storage service as "bms.xAuthToken" see login.js.
					 * The prefix "bms" is configured in ontology.js as part of app.config:
					 *     localStorageServiceProvider.setPrefix('bms');
					 */
					localStorageService.remove('xAuthToken');
					$rootScope.$broadcast('authenticationError');
				}
				return $q.reject(response);
			}
		};
	}]);
})();
