/*global angular*/
'use strict';

(function() {
	var app = angular.module('utilities', []);

	app.factory('serviceUtilities', '$q', function($q) {
		return {
			genericAndRatherUselessErrorHandler: function(error) {
				if (console) {
					console.log(error);
				}
			},

			restSuccessHandler: function(response) {
				return response.data;
			},

			restFailureHandler: function(q, response) {
				var errorMessage = 'An unknown error occurred.';

				if (!angular.isObject(response.data)) {
					if (response.status === 400) {
						errorMessage = 'Request was malformed.';
					}
					return $q.reject(errorMessage);
				}
			}
		};
	});
}());
