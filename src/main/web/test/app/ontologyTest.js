/*global expect, inject, spyOn*/
'use strict';

describe('Ontology Controller', function() {

	var fakeEvent = {
			preventDefault: function() {}
		},
		controller,
		deferred,
		location,
		panelService,
		q,
		scope,
		window;

	beforeEach(module('ontology'));

	beforeEach(inject(function($rootScope, $location, $controller, $q, $window) {
		panelService = {
			hidePanel: function() {
				deferred = q.defer();
				return deferred.promise;
			},
			showPanel: function () {
				deferred = q.defer();
				return deferred.promise;
			}
		};

		controller = $controller('OntologyController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			panelService: panelService
		});

		location = $location;
		q = $q;
		scope = $rootScope;
		window = $window;

	}));

	it('should store the previous location when the location changes', function() {
		var oldUrl = '/oldUrl',
			newUrl = '/newUrl';

		expect(scope.previousUrl).toBeUndefined();
		scope.$broadcast('$locationChangeStart', newUrl, oldUrl);
		expect(scope.previousUrl).toEqual(oldUrl);
		scope.$broadcast('$locationChangeStart', '/newNewUrl', newUrl);
		expect(scope.previousUrl).toEqual(newUrl);
	});

	describe('addNew', function() {
		it('should route and close the panel', function() {
			var path = 'myPath';

			spyOn(location, 'path').and.callThrough();
			spyOn(panelService, 'hidePanel').and.callThrough();

			scope.addNew(fakeEvent, path);

			expect(location.path).toHaveBeenCalledWith('/add/' + path);
			expect(panelService.hidePanel).toHaveBeenCalled();
		});
	});

	describe('addNewSelection', function() {
		it('should show the panel', function() {

			spyOn(panelService, 'showPanel').and.callThrough();
			scope.addNewSelection();
			expect(panelService.showPanel).toHaveBeenCalledWith(scope.panelName);
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
