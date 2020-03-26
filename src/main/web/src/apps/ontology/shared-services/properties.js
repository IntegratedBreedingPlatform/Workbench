/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['utilities', 'config']);

	app.service('propertiesService', ['$http', 'serviceUtilities', 'configService', function($http, serviceUtilities, configService) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		function convertPropertyForUpdating(property) {
			var convertedProperty = {},

				propertiesToInclude = [
					'name',
					'description',
					'classes',
					'cropOntologyId'
				];

			Object.keys(property).forEach(function(key) {

				// Ignore properties we want to remove before sending
				if (propertiesToInclude.indexOf(key) > -1) {
					convertedProperty[key] = property[key];
				}
			});

			return convertedProperty;
		}

		return {
			/*
			Returns an array of properties in the format:

			[{
				'id': 23,
				'name': 'Alkali Injury',
				'description': 'Condition characterized by discoloration of the leaves ranging from ...',
				'classes': ['Abiotic Stress', 'Trait'],
				'cropOntologyId': 'CO_192791864',
				'metadata': {
					'dateCreated': '2013-10-21T13:28:06.419Z',
					'lastModified': '2013-10-21T13:28:06.419Z'
				}
			}]

			*/
			getProperties: function() {
				var url = '/bmsapi/crops/' + configService.getCropName() + '/properties?programUUID=' + configService.getProgramId(),
					request = $http.get(url, {timeout: 60000});
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a property in the format:

			{
				'name': 'Blast',
				'description': 'A fungus disease of rice caused by the fungus Pyricularia oryzae.',
				'classes': ['Abiotic Stress', 'Trait']
				'cropOntologyId': 'CO_192791349'
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A property with that name already exists.'
				}]
			}
			*/
			addProperty: function(property) {
				var request = $http.post('/bmsapi/crops/' + configService.getCropName() + '/properties?programUUID=' + configService.getProgramId(),
					property);
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a property in the format:

			{
				'name': 'Plant Height',
				'description': 'The plant height',
				'classes': ['Trait']
				'cropOntologyId': 'CO_192791864'
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A property with that name already exists.'
				}]
			}
			*/
			updateProperty: function(id, property) {
				var convertedProperty = convertPropertyForUpdating(property),
					request;

				request = $http.put('/bmsapi/crops/' + configService.getCropName() + '/properties/' + id + '?programUUID=' + configService.getProgramId(),
					convertedProperty);
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Deletes the property with the specified ID.
			*/
			deleteProperty: function(id) {
				var request;

				request = $http.delete('/bmsapi/crops/' + configService.getCropName() + '/properties/' + id + '?programUUID=' + configService.getProgramId());
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Returns a single property in the format:

			{
				'name': 'Alkali Injury',
				'description': 'Condition characterized by discoloration of the leaves ranging from ...',
				'classes': ['Abiotic Stress', 'Trait'],
				'cropOntologyId': 'CO_192791864',
				'metadata': {
					'editableFields': ['name', 'description', 'classes', 'cropOntologyId'],
					'deletable': true,
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
			getProperty: function(id) {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/properties/' + id + '?programUUID=' + configService.getProgramId());
				return request.then(successHandler, failureHandler);
			},

			/*
			Returns an array of classes in the format:

			['Abiotic Stress', 'Agronomic', 'Biotic Stress', 'Germplasm']
			*/
			getClasses: function() {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/classes?programUUID=' + configService.getProgramId());
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
