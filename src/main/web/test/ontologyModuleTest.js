/*global expect, inject, spyOn*/
'use strict';

describe('Ontology Controller', function() {

	var fakeEvent = {
			preventDefault: function() {}
		},
		controller,
		location,
		scope,
		window;

	beforeEach(module('ontology'));

	beforeEach(inject(function($rootScope, $location, $controller, $window) {

		controller = $controller('OntologyController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window
		});

		location = $location;
		scope = $rootScope;
		window = $window;

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

			// Pretend we've gone somewhere
			scope.addNew(fakeEvent, 'aPath');

			spyOn(window.history, 'back').and.callThrough();

			scope.goBack();
			expect(window.history.back).toHaveBeenCalled();
		});
	});
});
