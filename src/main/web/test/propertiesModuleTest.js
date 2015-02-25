/*global expect, inject, spyOn*/
'use strict';

describe('Properties View', function() {
	var q,
		controller,
		scope,
		deferred,
		propertiesService;

	beforeEach(function() {
		module('propertiesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {
		propertiesService = {
			getProperties: function() {
				deferred = q.defer();
				return deferred.promise;
			}
		};
		spyOn(propertiesService, 'getProperties').and.callThrough();

		q = $q;
		scope = $rootScope;
		controller = $controller('PropertiesController', {
			$scope: scope,
			propertiesService: propertiesService
		});
	}));

	it('should transform properties into display format', function() {
		var jsonData = [{
				id: 'prop1',
				name: 'prop1',
				classes: ['class1', 'class2']
			}],
			transformedData = [{
				id: 'prop1',
				Name: 'prop1',
				Classes: 'class1, class2'
			}];

		deferred.resolve(jsonData);
		scope.$apply();
		expect(propertiesService.getProperties).toHaveBeenCalled();
		expect(controller.properties).toEqual(transformedData);
	});
});
