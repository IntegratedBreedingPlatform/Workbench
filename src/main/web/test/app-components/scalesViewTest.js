/*global expect, inject, spyOn*/
'use strict';

describe('Scales View', function() {
	var PERCENTAGE = {
			id: 1,
			name: 'Percentage',
			description: 'As per title',
			dataType: {
				id: 2,
				name: 'Numeric'
			},
			validValues: {
				min: 0,
				max: 100
			}
		},

		q,
		controller,
		scope,
		deferredGetScales,
		deferredGetScale,
		scalesService,
		panelService;

	beforeEach(function() {
		module('scalesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {
		scalesService = {
			getScales: function() {
				deferredGetScales = q.defer();
				return deferredGetScales.promise;
			},
			getScale: function() {
				deferredGetScale = q.defer();
				return deferredGetScale.promise;
			}
		};

		panelService = {
			showPanel: function() {}
		};

		spyOn(scalesService, 'getScales').and.callThrough();
		spyOn(scalesService, 'getScale').and.callThrough();
		spyOn(panelService, 'showPanel');

		q = $q;
		scope = $rootScope;
		controller = $controller('ScalesController', {
			$scope: scope,
			scalesService: scalesService,
			panelService: panelService
		});
	}));

	it('should transform scales into display format', function() {
		var jsonData = [PERCENTAGE],
			transformedData = [{
				id: PERCENTAGE.id,
				Name: PERCENTAGE.name,
				Description: PERCENTAGE.description,
				DataType: PERCENTAGE.dataType.name
			}];

		deferredGetScales.resolve(jsonData);
		scope.$apply();
		expect(scalesService.getScales).toHaveBeenCalled();
		expect(controller.scales).toEqual(transformedData);
	});

	it('should set the selected item to be an object with an id property set to null by default', function() {
		expect(scope.selectedItem).toEqual({id: null});
	});

	it('should set the selected scale to be null by default', function() {
		expect(scope.selectedScale).toEqual(null);
	});

	describe('$scope.showScaleDetails', function() {

		it('should retrieve the selected scale and display the panel', function() {

			var selectedId = 123,
				panelName = 'scales',
				scale = PERCENTAGE;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showScaleDetails();
			deferredGetScale.resolve(scale);
			scope.$apply();

			expect(scalesService.getScale).toHaveBeenCalledWith(selectedId);
			expect(scope.selectedScale).toEqual(scale);
			expect(panelService.showPanel).toHaveBeenCalledWith(panelName);
		});
	});
});
