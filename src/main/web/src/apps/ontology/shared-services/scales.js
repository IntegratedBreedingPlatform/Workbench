/*global angular*/
'use strict';

(function() {
	var app = angular.module('scales', ['utilities']);

	app.service('scalesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		function convertScaleForAdding(scale) {
			var convertedScale = {
					dataTypeId: scale.dataType && scale.dataType.id
				},
				propertiesToInclude = [
					'name',
					'description',
					'validValues'
				];

				Object.keys(scale).forEach(function(key) {
					// Ignore properties we want to remove before sending
					if (propertiesToInclude.indexOf(key) > -1) {
						convertedScale[key] = scale[key];
					}
				});

			return convertedScale;
		}

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
				}
			}]
			*/
			getScales: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales');
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a scale in the format:

			{
				'name': 'Percentage',
				'description': 'Percentage',
				'dataTypeId': 2,
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
				var convertedScale = convertScaleForAdding(scale),
					request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales',
						convertedScale);
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a scale in the format:

			{
				'name': 'Percentage',
				'description': 'Percentage',
				'dataTypeId': 2,
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
				var request = $http.put('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales/:id',
					scale);
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Deletes the scale with the specified ID.
			*/
			deleteScale: function(/*id*/) {
				var request;

				request = $http.delete('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales/:id');
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
				'editableFields': ['description'],
				'deletable': false
			}
			*/
			getScale: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/scales/:id');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
