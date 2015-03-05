/*global angular*/
'use strict';

(function() {
	var app = angular.module('scales', []);

	app.service('scalesService', ['$http', '$q', function($http, $q) {
		function successHandler(response) {
			return response.data;
		}

		function failureHandler(response) {
			var errorMessage = 'An unknown error occurred.';

			if (!angular.isObject(response.data)) {
				if (response.status === 400) {
					errorMessage = 'Request was malformed.';
				}
				return $q.reject(errorMessage);
			}
		}

		return {
			// Scales services (plural)
			getScales: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales');
				return request.then(successHandler, failureHandler);
			},

			addScale: function(scale) {
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales', scale);
				return request.then(successHandler, failureHandler);
			},

			// Scale services (on a specific scale)
			getScale: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales/:id');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
