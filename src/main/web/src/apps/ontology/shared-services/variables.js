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
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables');
				return request.then(successHandler, failureHandler);
			},

			getFavouriteVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables' +
					'?favourite=true');
				return request.then(successHandler, failureHandler);
			},

			addVariable: function(variable) {
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables',
					variable);
				return request.then(successHandler, failureHandler);
			},

			updateVariable: function(id, variable) {
				var request = $http.put('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables/:id',
					variable);
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			// Variable services (on a specific variable)
			getVariable: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables/:id');
				return request.then(successHandler, failureHandler);
			},

			// Variable Types services (plural)
			getTypes: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variableTypes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);

}());
