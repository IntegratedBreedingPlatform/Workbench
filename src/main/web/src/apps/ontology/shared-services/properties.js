/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('properties', []);

	app.service('propertyService', [function() {
		return {
			saveProperty: function(property) {
				// TODO Call actual save functionality
				console.log('Saving property');
				console.log(property);
			}
		};
	}]);

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
			getProperty: function(/*id*/) {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties/:id');
				return request.then(successHandler, failureHandler);
			},

			getProperties: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties');

				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
