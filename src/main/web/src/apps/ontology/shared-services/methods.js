/*global angular*/
'use strict';

(function() {
	var app = angular.module('methods', ['utilities']);

	app.service('methodsService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			// Methods services (plural)
			getMethods: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods');
				return request.then(successHandler, failureHandler);
			},

			addMethod: function(method) {
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods', method);
				return request.then(successHandler, failureHandler);
			},

			// Method services (on a specific method)
			getMethod: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods/:id');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
