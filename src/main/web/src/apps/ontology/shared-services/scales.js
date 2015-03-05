/*global angular*/
'use strict';

(function() {
	var app = angular.module('scales', ['utilities']);

	app.service('scalesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

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
