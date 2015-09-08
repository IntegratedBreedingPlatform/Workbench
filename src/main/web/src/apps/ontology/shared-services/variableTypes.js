/*global angular*/
'use strict';

(function() {
	var app = angular.module('variableTypes', ['utilities']);

	app.service('variableTypesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			/*
			Expects an array of variable types in the format:

			[{
				'id': 1,
				'name': 'Analysis',
				'description': 'Variable to be used only in analysis (for example derived variables).'
			}]
			*/
			getTypes: function() {
				var request = $http.get('/bmsapi/ontology/variableTypes', {timeout: 5000});
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
