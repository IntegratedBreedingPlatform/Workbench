/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', []);

	app.controller('VariablesController', ['$scope', 'variablesProvider', function($scope, variablesProvider) {
		this.variables = variablesProvider.getVariables();
	}]);

	app.provider('variablesProvider', function() {
		var getVariables = function() {
			return [{
				id: 1,
				name: 'Plant Vigor',
				alias: '',
				description: 'A little vigourous',
				property: {
					id: '1',
					name: 'Plant Vigor'
				},
				method: {
					id: '1',
					name: 'Visual assessment at seedling stage'
				},
				scale: {
					id: '1',
					name: 'Score',
					dataType: '2',
					validValues: {
						min: '1',
						max: '5'
					}
				},
				variableType: [
					'1'
				],
				cropOntologyId: 'CO_12397f8',
				favourite: 'true',
				metadata: {
					dateCreated: '2013-10-21T13:28:06.419Z',
					lastModified: '2013-10-21T13:28:06.419Z',
					usage: {
						observations: '200'
					}
				},
				expectedRange: {
					min: '5',
					max: '8'
				}
			}];
		};

		return {
			$get: function() {
				return {
					getVariables: getVariables
				};
			}
		};
	});
}());
