/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['utilities']);

	app.service('propertiesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

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
				'cropOntologyId': 'CO_192791864'
			}]

			*/
			getProperties: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties');
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
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties',
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

				request = $http.put('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties/:id',
					convertedProperty);
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Deletes the property with the specified ID.
			*/
			deleteProperty: function(/*id*/) {
				var request;

				request = $http.delete('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties/:id');
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
				'editableFields': ['name', 'description', 'classes', 'cropOntologyId'],
				'deletable': true
			}
			*/
			getProperty: function(/*id*/) {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/properties/:id');
				return request.then(successHandler, failureHandler);
			},

			/*
			Returns an array of classes in the format:

			['Abiotic Stress', 'Agronomic', 'Biotic Stress', 'Germplasm']
			*/
			getClasses: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/classes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
