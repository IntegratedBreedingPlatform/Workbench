/*global expect, inject*/
'use strict';

describe('Ontology module', function() {

	beforeEach(module('ontology'));

	it('should map routes to views', function() {
		inject(function($route) {

			expect($route.routes['/properties'].templateUrl)
				.toEqual('../static/views/ontology/propertiesView.html');

			expect($route.routes['/variables'].templateUrl)
				.toEqual('../static/views/ontology/variablesView.html');
		});
	});
});
