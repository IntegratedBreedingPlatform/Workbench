/*global angular*/
'use strict';

(function() {
	var app = angular.module('dataTypes', ['utilities']);

	app.service('dataTypesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			/*
			Returns an array of data types in the format:

			[{
				'id': 1,
				'name': 'Categorical'
			}]
			*/
			getDataTypes: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/dataTypes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
