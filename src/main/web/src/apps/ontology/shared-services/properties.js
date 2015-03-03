/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', []);

	app.service('propertiesService', ['$http', '$q', function($http, $q) {
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
			// Properties services (plural)
			getProperties: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties');
				return request.then(successHandler, failureHandler);
			},

			addProperty: function(property) {
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/properties', property);
				return request.then(successHandler, failureHandler);
			},

			// Property services (on a specific property)
			getProperty: function(/*id*/) {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties/:id');
				return request.then(successHandler, failureHandler);
			},

			// Classes services (plural)
			getClasses: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/classes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
