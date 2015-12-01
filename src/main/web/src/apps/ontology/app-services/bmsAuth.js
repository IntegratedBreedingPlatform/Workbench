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

	bmsAuth.factory('authExpiredInterceptor', ['$q', 'localStorageService', 'reAuthenticationService', function($q, localStorageService, reAuthenticationService) {
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
					reAuthenticationService.handleReAuthentication();
				}
				return $q.reject(response);
			}
		};
	}]);

	bmsAuth.service('reAuthenticationService', function() {
		var hasBeenHandled = false;
		return {
			// Current strategy to re-authenticate is to log the user out from Workbench by hitting Spring security internal logout endpoint
			//    which means re-login, which in turn means a fresh token will be issued ;)
			// TODO find a better alternative to use insead of alert then in the face punch to logout which is easy to unit test as well.
			handleReAuthentication: function() {
				if (!hasBeenHandled) {
					hasBeenHandled = true;
					alert('Ontology manager needs to authenticate you again. Redirecting to login page.');
					var isInFrame = window.location !== window.parent.location;
					var parentUrl = isInFrame ? document.referrer : document.location.href;
					var pathArray = parentUrl.split('/');
					var protocol = pathArray[0];
					var host = pathArray[2];
					var baseUrl = protocol + '//' + host;
					var logoutUrl = baseUrl + '/ibpworkbench/logout';
					window.top.location.href = logoutUrl;
				}
			}
		};
	});

})();
