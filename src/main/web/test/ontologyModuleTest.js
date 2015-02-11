/*global expect, inject*/
'use strict';

describe('test.ontologyModuleTest', function() {

	var PROPERTIES = '/properties',
		VARIABLES = '/variables';

	beforeEach(module('ontology'));

	it('should map routes to views', function() {
		inject(function($route) {

			expect($route.routes[PROPERTIES].templateUrl)
				.toEqual('../static/views/ontology/propertiesView.html');

			expect($route.routes[VARIABLES].templateUrl)
				.toEqual('../static/views/ontology/variablesView.html');
		});
	});

	it('should map routes to controllers', function() {
		inject(function($route) {

			expect($route.routes[PROPERTIES].controller)
				.toEqual('PropertiesController');
			expect($route.routes[PROPERTIES].controllerAs)
				.toEqual('propsCtrl');

			expect($route.routes[VARIABLES].controller)
				.toEqual('VariablesController');
			expect($route.routes[VARIABLES].controllerAs)
				.toEqual('varsCtrl');
		});
	});
});
