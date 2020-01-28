/*global angular*/
'use strict';

(function() {
	var app = angular.module('scales', ['utilities', 'config']);

	app.service('scalesService', ['$http', '$q', 'serviceUtilities', 'configService', function($http, $q, serviceUtilities, configService) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			/*
			Returns an array of scales in the format:

			[{
				'id': 1,
				'name': 'Percentage',
				'description': 'Percentage',
				'dataType': {
					'id': 2,
					'name': 'Numeric'
				},
				'validValues': {
					'min': 0,
					'max': 100
				},
				'metadata': {
					'dateCreated': '2013-10-21T13:28:06.419Z',
					'lastModified': '2013-10-21T13:28:06.419Z'
				}
			}]
			*/
			getScales: function() {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/scales?programUUID=' + configService.getProgramId(), {timeout: 60000});
				return request.then(successHandler, failureHandler);
			},

			/*
			Returns an array of scales minus the ones that have non system data types.
			*/
			getScalesWithNonSystemDataTypes: function() {
				return this.getScales().then(function(scales) {
					return scales.filter(function(scale) {
						return !scale.dataType.systemDataType;
					});

				}, function(response) {
					return $q.reject(response);
				});
			},

			/*
			Expects a scale in the format:

			{
				'name': 'Percentage',
				'description': 'Percentage',
				'dataType': {
					'id': 2,
					'name': 'numeric'
				}
				'validValues': {
					'min': 0,
					'max': 100
				}
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A scale with that name already exists.'
				}]
			}
			*/
			addScale: function(scale) {
				var request = $http.post('/bmsapi/crops/' + configService.getCropName() + '/scales?programUUID=' + configService.getProgramId(), scale);
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a scale in the format:

			{
				'name': 'Percentage',
				'description': 'Percentage',
				'dataType': {
					'id': 2,
					'name': 'numeric'
				},
				'validValues': {
					'min': 0,
					'max': 100
				}
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A scale with that name already exists.'
				}]
			}
			*/
			updateScale: function(id, scale) {
				var url = '/bmsapi/crops/' + configService.getCropName() + '/scales/' + id + '?programUUID=' + configService.getProgramId(),
					convertedScale = {
						name: scale.name,
						description: scale.description,
						dataType: scale.dataType,
						validValues: scale.validValues
					},
					request = $http.put(url, convertedScale);

				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Deletes the scale with the specified ID.
			*/
			deleteScale: function(id) {
				var request;

				request = $http.delete('/bmsapi/crops/' + configService.getCropName() + '/scales/' + id + '?programUUID=' + configService.getProgramId());
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Returns a single scale in the format:

			{
				'name': 'Percentage',
				'description': 'Percentage',
				'dataType': {
					'id': 2,
					'name': 'Numeric'
				},
				'validValues': {
					'min': 0,
					'max': 100
				},
				'metadata': {
					'editableFields': ['description'],
					'deletable': false,
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
			getScale: function(id) {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/scales/' + id + '?programUUID=' + configService.getProgramId());
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
