/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['utilities']);

	app.service('propertiesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			// Properties services (plural)
			getProperties: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties');
				return request.then(successHandler, failureHandler);
			},

			addProperty: function(property) {
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties',
					property);
				return request.then(successHandler, failureHandler);
			},

			// Property services (on a specific property)
			getProperty: function(/*id*/) {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties/:id');
				return request.then(successHandler, failureHandler);
			},

			// Classes services (plural)
			getClasses: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/classes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
