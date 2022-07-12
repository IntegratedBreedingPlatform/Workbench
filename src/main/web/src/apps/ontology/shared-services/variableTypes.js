/*global angular*/
'use strict';

(function() {
	var app = angular.module('variableTypes', ['utilities', 'config']);

	app.service('variableTypesService', ['$http', 'serviceUtilities', 'configService', function($http, serviceUtilities, configService) {

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
			getTypes: function(excludeRestrictedTypes) {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/variable-types?programUUID=' + configService.getProgramId(), {
						params: {excludeRestrictedTypes: excludeRestrictedTypes}
					},
					{
						timeout: 60000
					});
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
