/*global expect, inject, spyOn*/
'use strict';

describe('Ontology Controller', function() {

	var fakeEvent = {
			preventDefault: function(){}
		},
		controller,
		location,
		scope;

	beforeEach(module('ontology'));

	beforeEach(inject(function($rootScope, $location, $controller) {

		controller = $controller('OntologyController', {
			$scope: $rootScope
		});

		location = $location;
		scope = $rootScope;

	}));

	describe('addNew', function() {
		it('should route and close the panel', function() {

			var path = 'myPath';

			spyOn(location, 'path').and.callThrough();
			scope.addNew(fakeEvent, path);

			expect(location.path).toHaveBeenCalledWith('/add/' + path);
		});
	});

	describe('goBack', function() {
		it('should load the previous url', function() {

			var oldPath = 'oldPath';

			// Where we're coming from
			scope.previousUrl = oldPath;

			// Pretend we've gone somewhere
			scope.addNew(fakeEvent, 'newPath');

			spyOn(location, 'path').and.callThrough();

			scope.goBack();
			expect(location.path).toHaveBeenCalledWith(oldPath);
		});
	});
});
