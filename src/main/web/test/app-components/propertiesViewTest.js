/*global expect, inject, spyOn*/
'use strict';

describe('Properties View', function() {
	var BLAST = {
			name: 'Blast',
			description: 'I\'ts a blast',
			classes: ['class', 'anotherClass']
		},

		q,
		controller,
		scope,
		deferredGetProperties,
		deferredGetProperty,
		propertiesService,
		panelService;

	beforeEach(function() {
		module('propertiesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {
		propertiesService = {
			getProperties: function() {
				deferredGetProperties = q.defer();
				return deferredGetProperties.promise;
			},
			getProperty: function() {
				deferredGetProperty = q.defer();
				return deferredGetProperty.promise;
			}
		};

		panelService = {
			showPanel: function() {}
		};

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(propertiesService, 'getProperty').and.callThrough();
		spyOn(panelService, 'showPanel');

		q = $q;
		scope = $rootScope;
		controller = $controller('PropertiesController', {
			$scope: scope,
			propertiesService: propertiesService,
			panelService: panelService
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

		deferredGetProperties.resolve(jsonData);
		scope.$apply();
		expect(propertiesService.getProperties).toHaveBeenCalled();
		expect(controller.properties).toEqual(transformedData);
	});

	it('should set the selected item to be an object with an id property set to null by default', function() {
		expect(scope.selectedItem).toEqual({id: null});
	});

	it('should set the selected property to be null by default', function() {
		expect(scope.selectedProperty).toEqual(null);
	});

	describe('$scope.showPropertyDetails', function() {

		it('should retrieve the selected property and display the panel', function() {

			var selectedId = 123,
				panelName = 'properties',
				property = BLAST;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showPropertyDetails();
			deferredGetProperty.resolve(property);
			scope.$apply();

			expect(propertiesService.getProperty).toHaveBeenCalledWith(selectedId);
			expect(scope.selectedProperty).toEqual(property);
			expect(panelService.showPanel).toHaveBeenCalledWith(panelName);
		});
	});
});
