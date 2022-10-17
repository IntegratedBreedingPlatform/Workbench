/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['utilities', 'config']);

	app.service('variablesService', ['$q', '$http', 'serviceUtilities', 'configService', function($q, $http, serviceUtilities, configService) {

		var successHandler = serviceUtilities.restSuccessHandler,
			failureHandler = serviceUtilities.restFailureHandler;

		function convertVariableForUpdating(variable) {
			var convertedVariable = {},
				propertiesToInclude = [
					'name',
					'alias',
					'description',
					'property',
					'method',
					'scale',
					'favourite',
					'obsolete',
					'expectedRange',
					'variableTypes'
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
				'property': {
					'id': 1,
					'name': 'Plant Vigor'
				},
				'method': {
					'id': 46,
					'name': 'Visually Assessed'
				},
				'scale': {
					'id': 1,
					'name': 'Percentage',
					'dataType': {
						'id': 2,
						'name': 'Numeric'
					}
				},
				'variableTypes': [{
					'id': 1,
					'name': 'Analysis',
					'description': 'Variable to be used only in analysis (for example derived variables).'
				}],
				'favourite': true,
				'metadata': {
					'dateCreated': '2013-10-21T13:28:06.419Z',
					'lastModified': '2013-10-21T13:28:06.419Z'
				},
				'expectedRange': {
					'min': 3,
					'max': 5
				}
			}]

			expectedRange is optional, and has optional min and max properties.
			*/
			getVariables: function() {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/variables?programUUID=' +
					configService.getProgramId(), {timeout: 60000});
				return request.then(successHandler, failureHandler);
			},

			/*
			Returns an array of variables in the same format as getVariables above.
			*/
			getFavouriteVariables: function() {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/variables?favourite=true&programUUID=' +
					configService.getProgramId(), {timeout: 60000});
				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a variable in the format:

			{
				'name': 'Plant Vigor',
				'description': 'A little vigourous',
				'property': {
					'id': 1,
					'name': 'Plant Vigor'
				},
				'method': {
					'id': 46,
					'name': 'Visually Assessed'
				},
				'scale': {
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
				},
				'variableTypes': [{
					'id': 1,
					'name': 'Analysis',
					'description': 'Variable to be used only in analysis (for example derived variables).'
				}],
				'expectedRange': {
					'min': 3,
					'max': 5
				}
			}

			This will be converted before sending to change property, method and scale to only return their id, and
			to remove any other properties not listed above, resulting in a structure similar to:

			{
				'name': 'Plant Vigor',
				'description': 'A little vigourous',
				'propertyId': 34,
				'methodId': 68,
				'scaleId': 145,
				'variableTypeIds': [1],
				'expectedRange': {
					'min': 3,
					'max': 5
				}
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
					request = $http.post('/bmsapi/crops/' + configService.getCropName() + '/variables?programUUID=' +
						configService.getProgramId(), convertedVariable);

				return request.then(successHandler, failureHandler);
			},

			/*
			Expects a variable in the format:

			{
				'name': 'Plant Vigor',
				'alias': '',
				'description': 'A little vigourous',
				'property': {
					'id': 1,
					'name': 'Plant Vigor'
				},
				'method': {
					'id': 46,
					'name': 'Visually Assessed'
				},
				'scale': {
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
				},
				'variableTypes': [{
					'id': 1,
					'name': 'Analysis',
					'description': 'Variable to be used only in analysis (for example derived variables).'
				}],
				'favourite': true,
				'expectedRange': {
					'min': 3,
					'max': 5
				}
			}

			This will be converted before sending to change property, method and scale to only return their id, and
			to remove any other properties not listed above, resulting in a structure similar to:

			{
				'name': 'Plant Vigor',
				'alias': '',
				'description': 'A little vigourous',
				'propertyId': 12,
				'methodId': 13,
				'scaleId': 16,
				'variableTypeIds': [1],
				'favourite': true,
				'expectedRange': {
					'min': 3,
					'max': 5
				}
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

				request = $http.put('/bmsapi/crops/' + configService.getCropName() + '/variables/' + id + '?programUUID=' +
						configService.getProgramId(), convertedVariable);
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			},

			/*
			 * Deletes the variable from cache with the specified ID.
			 * 
			 */
			deleteVariablesFromCache: function(variableIds) {
				var request;

				if (!variableIds || variableIds.length == 0) {
					return;
				}

				var params = variableIds.join(",")
					+ '?selectedProjectId=' + configService.getSelectedProjectId()
					+ '&loggedInUserId=' + configService.getLoggedInUserId();

				request = [
					$http.delete('/Fieldbook/variableCache/' + params),
					$http.delete('/ibpworkbench/controller/variableCache/' + params)
				];

				return $q.all(request).then(function(response) {
					return response.map(function (x) { return x.status } );
				}, failureHandler);
			},

			/*
			Deletes the variable with the specified ID.
			*/
			deleteVariable: function(id) {
				var request;

				request = $http.delete('/bmsapi/crops/' + configService.getCropName() + '/variables/' + id + '?programUUID=' +
					configService.getProgramId());
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
				'property': {
					'id': 1,
					'name': 'Plant Vigor'
				},
				'method': {
					'id': 46,
					'name': 'Visually Assessed'
				},
				'scale': {
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
				},
				'variableTypes': [{
					'id': 1,
					'name': 'Analysis',
					'description': 'Variable to be used only in analysis (for example derived variables).'
				}],
				'favourite': true,
				'expectedRange' {
					'min': 0,
					'max': 50
				},
				'metadata': {
					'editableFields': ['name', 'description', 'alias', 'variableTypes'],
					'deletable': false,
					'dateCreated': '2013-10-21T13:28:06.419Z',
					'lastModified': '2013-10-21T13:28:06.419Z',
					'usage': {
						'observations': 200,
						'studies': 2
					}
				},
			}

			expectedRange is optional, and has optional min and max properties.
			*/
			getVariable: function(id) {
				var request = $http.get('/bmsapi/crops/' + configService.getCropName() + '/variables/' + id + '?programUUID=' +
					configService.getProgramId());
				return request.then(successHandler, failureHandler);
			},

			addFormula: function (formula) {
				var request = $http.post('/bmsapi/crops/' + configService.getCropName() + '/formula?programUUID=' +
					configService.getProgramId(), formula);
				return request.then(successHandler, failureHandler);
			},

			updateFormula: function (formula) {
				var request = $http.put('/bmsapi/crops/' + configService.getCropName() + '/formula/' + formula.formulaId + '?programUUID=' +
					configService.getProgramId(), formula);
				return request.then(successHandler, failureHandler);
			},

			deleteFormula: function(formulaId) {
				var request;
				request = $http.delete('/bmsapi/crops/' + configService.getCropName() + '/formula/' + formulaId + '?programUUID=' +
					configService.getProgramId());
				return request.then(function(response) {
					return response.status;
				}, failureHandler);
			}
		};
	}]);

}());
