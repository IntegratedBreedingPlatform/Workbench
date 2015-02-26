/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('methods', []);

	app.service('methodService', [function() {
		return {
			saveMethod: function(method) {
				// TODO Call actual save functionality
				console.log('Saving method');
				console.log(method);
			}
		};
	}]);

	app.service('methodsService', ['$http', '$q', function($http, $q) {
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
			getMethod: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/methods/:id');
				return request.then(successHandler, failureHandler);
			},

			getMethods: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/methods');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
