/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['utilities']);

	app.service('variablesService', ['$http', 'serviceUtilities', function($http, serviceUtilities) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		function convertVariableForUpdating(variable) {
			var convertedVariable = {
					methodId: variable.methodSummary && variable.methodSummary.id,
					scaleId: variable.scaleSummary && variable.scaleSummary.id,
					propertyId: variable.propertySummary && variable.propertySummary.id,
					variableTypeIds: variable.variableTypes.map(function(variableType) {
						return variableType.id;
					})
				},
				propertiesToInclude = [
					'name',
					'alias',
					'description',
					'favourite',
					'expectedRange'
				];

				Object.keys(variable).forEach(function(key) {

					// Ignore properties we want to remove before sending
					if (propertiesToInclude.indexOf(key) > -1) {
						convertedVariable[key] = variable[key];
					}
				});

			return convertedVariable;
		}

		return {
			/*
			Returns an array of variables in the format:

			[{
				'id': 1,
				'name': 'Plant Vigor',
				'alias': '',
				'description': 'A little vigourous',
				'propertySummary': {...},
				'methodSummary': {...},
				'scaleSummary': {...},
				'variableTypes': [{...}],
				'favourite': true,
				'metadata': {...},
				'expectedRange': {..}
			}]

			metadata contains dateCreated, lastModified properties, and a usage object with an observations property.
			propertySummary and methodSummary have two properties, name and id. expectedRange is optional, and has optional
			min and max properties.
			*/
			getVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables' +
					'?propertyId=1');
				return request.then(successHandler, failureHandler);
			},

			/*
			Returns an array of variables in the same format as getVariables above.
			*/
			getFavouriteVariables: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables' +
					'?favourite=true?propertyId=1');
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a variable in the format:

			{
				'name': 'Plant Vigor',
				'description': 'A little vigourous',
				'propertySummary': {...},
				'methodSummary': {...},
				'scale': {...},
				'variableTypes': [{...}],
				'expectedRange': {...}
			}

			This will be converted before sending to change property, method and scale to only return their id, and
			to remove any other properties not listed above, resulting in a structure similar to:

			{
				'name': 'Plant Vigor',
				'description': 'A little vigourous',
				'propertyId': 34,
				'methodId': 68,
				'scaleId': 145,
				'variableTypes': [{...}],
				'expectedRange': {...}
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A variable with that name already exists.'
				}]
			}
			*/
			addVariable: function(variable) {
				var convertedVariable = convertVariableForUpdating(variable),
					request = $http.post('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables',
						convertedVariable);

				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a variable in the format:

			{
				'name': 'Plant Vigor',
				'alias': '',
				'description': 'A little vigourous',
				'propertySummary': {...},
				'methodSummary': {...},
				'scale': {...},
				'variableTypes': [{...}],
				'favourite': true,
				'expectedRange': {...}
			}

			This will be converted before sending to change property, method and scale to only return their id, and
			to remove any other properties not listed above, resulting in a structure similar to:

			{
				'name': 'Plant Vigor',
				'alias': ',
				'description': 'A little vigourous',
				'propertyId': 12,
				'methodId': 13,
				'scaleId': 16,
				'variableTypes': [{...}],
				'favourite': true,
				'expectedRange': {...}
			}

			If the response has a 400 status, the response data will contain an errors property which is an
			array of error objects, each with a message and optional fieldName, linking the message to a
			specific field on the page (by HTML name).

			{
				'errors': [{
					'fieldNames': ['name'],
					'message': 'A variable with that name already exists.'
				}]
			}
			*/
			updateVariable: function(id, variable) {

				var convertedVariable = convertVariableForUpdating(variable),
					request;

				request = $http.put('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables/:id' +
					'?propertyId=1', convertedVariable);
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Deletes the variable with the specified ID.
			*/
			deleteVariable: function(/*id*/) {
				var request;

				request = $http.delete('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables/:id');
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			Returns a single variable in the format:

			{
				'name': 'Plant Vigor',
				'alias': '',
				'description': 'A little vigourous',
				'propertySummary': {...},
				'methodSummary': {...},
				'scale': {...},
				'variableTypes': [{...}],
				'favourite': true,
				'metadata': {...},
				'editableFields': ['name', 'description', 'alias', 'variableTypes'],
				'deletable': false
			}

			metadata contains dateCreated, lastModified properties, and a usage object with an observations property.
			propertySummary and methodSummary have two properties, name and id. expectedRange is optional, and has optional
			min and max properties.
			*/
			getVariable: function(/*id*/) {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/rice/variables/:id' +
					'?propertyId=1');
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects an array of variable types in the format:

			[{
				'id': 1,
				'name': 'Analysis',
				'description': 'Variable to be used only in analysis (for example derived variables).'
			}]
			*/
			getTypes: function() {
				var request = $http.get('http://private-f74035-ontologymanagement.apiary-mock.com/bmsapi/ontology/variableTypes');
				return request.then(successHandler, failureHandler);
			}
		};
	}]);

}());
