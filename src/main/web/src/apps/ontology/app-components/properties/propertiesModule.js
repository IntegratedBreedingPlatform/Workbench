/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['list']);

	app.controller('PropertiesController', ['$scope', 'propertiesService', function($scope, propertiesService) {
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
			if (!angular.isObject(response.data) || !response.data.message) {
				return $q.reject('An unknown error occurred.');
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
