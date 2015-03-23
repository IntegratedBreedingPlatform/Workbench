/*global angular*/
'use strict';

(function() {
	var app = angular.module('methods', ['utilities']);

	app.service('methodsService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		return {
			/*
			Returns an array of methods in the format:

			[{
				'id': 23,
				'name': 'Cut and Dry',
				'description': 'Cut the plant 10cm above the root and air dry in a shadey place.'
			  }]
			*/
			getMethods: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods');
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
				var request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods', method);
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
				var request = $http.put('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods/:id',
					method);
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Deletes the method with the specified ID.
			*/
			deleteMethod: function(/*id*/) {
				var request;

				request = $http.delete('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods/:id');
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Returns a single method in the format:

			{
			 	'name': 'Cut and Dry',
			 	'description': 'Cut the plant 10cm above the root and air dry in a shadey place.',
			 	'editableFields': ['description'],
				'deletable': false
			}
			*/
			getMethod: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/methods/:id');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
