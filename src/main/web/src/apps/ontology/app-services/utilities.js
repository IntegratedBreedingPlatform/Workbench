/*global angular*/
'use strict';

(function() {
	var app = angular.module('utilities', []);

	app.factory('serviceUtilities', ['$q', function($q) {

		return {
			genericAndRatherUselessErrorHandler: function(error) {
				if (console) {
					console.log(error);
				}
			},

			restSuccessHandler: function(response) {
				return response.data;
			},

			restFailureHandler: function(response) {
				var error = response && response.status === 400 ? 'Request was malformed.' : 'An unknown error occurred.';
				return $q.reject(error);
			}
		};
	}]);
}());
