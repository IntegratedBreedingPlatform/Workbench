/*global angular*/
'use strict';

(function() {
	var app = angular.module('dataTypes', ['utilities', 'config']);

	app.service('dataTypesService', ['$http', 'serviceUtilities', 'configService', function($http, serviceUtilities, configService) {

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
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/data-types?programUUID='+ configService.getProgramId(), {timeout: 60000});
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
