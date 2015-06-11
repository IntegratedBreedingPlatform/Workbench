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
				var request = $http.get('/bmsapi/ontology/datatypes', {timeout: 5000});
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
