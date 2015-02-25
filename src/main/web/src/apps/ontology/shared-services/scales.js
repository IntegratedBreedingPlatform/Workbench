/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('scales', []);

	app.service('scaleService', [function() {
		return {
			saveScale: function(scale) {
				// TODO Call actual save functionality
				console.log('Saving scale');
				console.log(scale);
			}
		};
	}]);

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
			getScales: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/scales');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
