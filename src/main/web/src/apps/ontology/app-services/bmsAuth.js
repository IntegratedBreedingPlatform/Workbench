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

	bmsAuth.factory('authExpiredInterceptor', ['$rootScope', '$q', 'localStorageService', 'reAuthenticationService', function($rootScope, $q, localStorageService, reAuthenticationService) {
		return {
			responseError: function(response) {
				// Token has expired or is invalid.
				if (response.status === 401 && (response.data.error === 'invalid_token' || response.data.error === 'Unauthorized')) {
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
			// Current strategy is to logout the user from Workbench by hittig Spring secutiry's internal logout endpoint
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
