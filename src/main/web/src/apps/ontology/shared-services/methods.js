/*global angular*/
'use strict';

(function() {
	var app = angular.module('methods', ['utilities', 'config']);

	app.service('methodsService', ['$http', 'serviceUtilities', 'configService', function($http, serviceUtilities, configService) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			/*
			Returns an array of methods in the format:

			[{
				'id': 23,
				'name': 'Cut and Dry',
				'description': 'Cut the plant 10cm above the root and air dry in a shadey place.',
				'metadata': {
					'dateCreated': '2013-10-21T13:28:06.419Z',
					'lastModified': '2013-10-21T13:28:06.419Z'
				}
			}]
			*/
			getMethods: function() {

				var url = '/bmsapi/crops/' + configService.getCropName() + '/methods?programUUID=' + configService.getProgramId(),
					request = $http.get(url, {timeout: 60000});

				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a method in the format:

			{
				'name': 'Cut and Dry',
				'description': 'Cut the plant 10cm above the root and air dry in a shadey place.'
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A method with that name already exists.'
				}]
			}
			*/
			addMethod: function(method) {

				var url = '/bmsapi/crops/' + configService.getCropName() + '/methods?programUUID=' + configService.getProgramId(),
					request = $http.post(url, method);

				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a method in the format:

			{
				'name': 'Cut and Dry',
				'description': 'Cut the plant 10cm above the root and air dry in a shadey place.'
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A method with that name already exists.'
				}]
			}
			*/
			updateMethod: function(id, method) {
				var url = '/bmsapi/crops/' + configService.getCropName() + '/methods/' + id + '?programUUID=' + configService.getProgramId(),
					convertedMethod = {
						name: method.name,
						description: method.description
					},
					request = $http.put(url, convertedMethod);

				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Deletes the method with the specified ID.
			*/
			deleteMethod: function(id) {

				var url = '/bmsapi/crops/' + configService.getCropName() + '/methods/' + id + '?programUUID=' + configService.getProgramId(),
					request = $http.delete(url);

				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Returns a single method in the format:

			{
				'name': 'Cut and Dry',
				'description': 'Cut the plant 10cm above the root and air dry in a shadey place.',
				'metadata': {
					'editableFields': ['description'],
					'deletable': false
					'dateCreated': '2013-10-21T13:28:06.419Z',
					'lastModified': '2013-10-21T13:28:06.419Z',
					'usage': {
						'variables': [{
							'id': 1,
							'name': 'Plant_Vigor'
						}]
					}
				}
			}
			*/
			getMethod: function(id) {
				var url = '/bmsapi/crops/' + configService.getCropName() + '/methods/' + id + '?programUUID=' + configService.getProgramId(),
					request = $http.get(url);

				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
