/*global angular*/
'use strict';

(function() {
	var app = angular.module('dataTypes', []);

	app.service('dataTypesService', ['$http', '$q', function($http, $q) {
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
			getDataTypes: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/dataTypes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
