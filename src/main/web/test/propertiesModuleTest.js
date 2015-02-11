/*global expect, inject, spyOn*/
'use strict';

describe('test.propertiesModuleTest', function() {
	var data = [{
			name: 'prop1',
			description: 'prop1 description'
		}],
		q,
		controller,
		scope,
		deferred,
		propertiesService;

	beforeEach(function() {
		module('properties');
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
			propertiesService: propertiesService
		});
	}));

	it('should retrieve properties from the propertiesService', function() {
		deferred.resolve(data);
		scope.$apply();
		expect(propertiesService.getProperties).toHaveBeenCalled();
		expect(controller.properties).toEqual(data);
	});

});
