/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['list']);

	app.controller('PropertiesController', ['propertiesService', function(propertiesService) {
		var ctrl = this;
		this.properties = [];

		propertiesService.getProperties().then(function(properties) {
			ctrl.properties = properties;
		});
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
			getProperties: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties');

				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
