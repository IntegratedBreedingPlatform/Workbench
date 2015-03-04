/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['utilities']);

	app.service('variablesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			// Variables services (plural)
			getVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/variables');
				return request.then(successHandler, failureHandler);
			},

			getFavouriteVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/variables?favourite=true');
				return request.then(successHandler, failureHandler);
			},

			addVariable: function(variable) {
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/variables', variable);
				return request.then(successHandler, failureHandler);
			},

			// Variable services (on a specific variable)
			getVariable: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/variables/:id');
				return request.then(successHandler, failureHandler);
			},

			// Variable Types services (plural)
			getTypes: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/variableTypes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);

}());
