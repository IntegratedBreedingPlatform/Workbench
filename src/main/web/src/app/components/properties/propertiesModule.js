/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', []);

	app.controller('PropertiesController', function() {
		this.properties = [{
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
	});
}());
