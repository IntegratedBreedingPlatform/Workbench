/*global angular*/
'use strict';

(function() {
	var app = angular.module('dataTypes', ['utilities']);

	app.service('dataTypesService', ['$http', '$q', 'serviceUtilities', function($http, $q, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			/*
			Returns an array of data types in the format:

			[{
				'id': 1,
				'name': 'Categorical',
				'systemDataType': false
			}]

			*/
			getDataTypes: function() {
				var request = $http.get('/bmsapi/ontology/datatypes', {timeout: 5000});
				return request.then(successHandler, failureHandler);
			},

			/*
			Returns an array of data types returned from the getDataTypes call, but with
			data types that have the systemDataType value true filtered out.
			*/
			getNonSystemDataTypes: function() {
				return this.getDataTypes().then(function(dataTypes) {

					return dataTypes.filter(function(dataType) {
						return !dataType.systemDataType;
					});

				}, function(response) {
					return $q.reject(response);
				});
			}
		};
	}]);
}());
