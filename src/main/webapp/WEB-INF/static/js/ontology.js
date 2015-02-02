/*global angular*/
'use strict';

(function() {
	var VIEWS_LOCATION = '../static/views/ontology/',
		app = angular.module('ontology', ['ngRoute', 'variables', 'properties']);

	app.config(['$routeProvider', function($routeProvider) {

		$routeProvider
			.when('/properties', {
				controller: 'PropertiesController',
				controllerAs: 'propsCtrl',
				templateUrl: VIEWS_LOCATION + 'propertiesView.html'
			})
			.when('/variables', {
				controller: 'VariablesController',
				controllerAs: 'varsCtrl',
				templateUrl: VIEWS_LOCATION + 'variablesView.html'
			});
	}]);
}());

/*global angular*/
'use strict';

(function() {
	var app = angular.module('variables', ['list']);

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

/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['list']);

	app.controller('PropertiesController', ['$scope', 'propertiesProvider', function($scope, propertiesProvider) {
		this.properties = propertiesProvider.getProperties();
	}]);

	app.provider('propertiesProvider', function() {
		var getProperties = function() {
			return [{
				id: '23',
				name: 'Alkali Injury',
				description: 'Condition characterized by discoloration of the leaves ranging from white to reddish brown ' +
					'starting from the leaf tips.',
				classes: ['Abiotic Stress', 'Trait'],
				cropOntologyId: 'CO_192791864'
			}, {
				id: '45',
				name: 'Blast',
				description: 'A fungus disease of rice caused by the fungus Pyricularia oryzae.',
				classes: ['Abiotic Stress', 'Trait'],
				cropOntologyId: 'CO_192791349'
			}];
		};

		return {
			$get: function() {
				return {
					getProperties: getProperties
				};
			}
		};
	});
}());

/*global angular*/
'use strict';

(function() {
	var listModule = angular.module('list', []);

	listModule.directive('list', function() {
		return {
			restrict: 'E',
			scope: {
				data: '=data'
			},
			templateUrl: '../static/views/ontology/listView.html'
		};
	});
}());
