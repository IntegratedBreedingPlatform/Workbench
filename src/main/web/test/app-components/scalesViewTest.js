/*global angular, expect, inject, spyOn*/
'use strict';

describe('Scales View', function() {
	var PERCENTAGE = {
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
		timeout,
		deferredGetScales,
		deferredGetScale,
		scalesService,
		panelService;

	beforeEach(function() {
		module('scalesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope, $timeout) {
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
		timeout = $timeout;
		controller = $controller('ScalesController', {
			$scope: scope,
			scalesService: scalesService,
			panelService: panelService
		});
	}));

	it('should transform scales into display format', function() {
		var jsonData = [{
				id: 1,
				name: PERCENTAGE.name,
				description: PERCENTAGE.description,
				dataType: PERCENTAGE.dataType
			}],
			transformedData = [{
				id: 1,
				name: PERCENTAGE.name,
				description: PERCENTAGE.description,
				dataType: PERCENTAGE.dataType.name
			}];

		deferredGetScales.resolve(jsonData);
		scope.$apply();
		expect(scalesService.getScales).toHaveBeenCalled();
		expect(controller.scales).toEqual(transformedData);
	});

	it('should add an empty description when transforming scale into display format if one is missing', function() {
		var jsonData = [{
				id: 1,
				name: PERCENTAGE.name,
				dataType: PERCENTAGE.dataType
			}],
			transformedData = [{
				id: 1,
				name: PERCENTAGE.name,
				description: '',
				dataType: PERCENTAGE.dataType.name
			}];

		deferredGetScales.resolve(jsonData);
		scope.$apply();
		expect(scalesService.getScales).toHaveBeenCalled();
		expect(controller.scales).toEqual(transformedData);
	});

	it('should show a message if there are no scales returned', function() {
		var jsonData = [];

		deferredGetScales.resolve(jsonData);
		scope.$apply();

		expect(scalesService.getScales).toHaveBeenCalled();
		expect(controller.showNoItemsMessage).toBe(true);
	});

	it('should show an error message if there was a problem while returning a list of scales from server', function() {
		deferredGetScales.reject();
		scope.$apply();

		expect(controller.problemGettingList).toBe(true);
		expect(controller.showThrobberWrapper).toBe(false);
	});

	it('should set the selected item to be an object with an id property set to null by default', function() {
		expect(scope.selectedItem).toEqual({id: null});
	});

	it('should set the selected scale to be null by default', function() {
		expect(scope.selectedScale).toEqual(null);
	});

	it('should show the throbber after a delay', function() {
		timeout.flush();
		expect(controller.showThrobber).toBe(true);
	});

	describe('$scope.showScaleDetails', function() {

		it('should set the selected scale to null before retrieving the selected scale', function() {

			var selectedId = 123,
				panelName = 'scales',
				scale = PERCENTAGE;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showScaleDetails();

			expect(scope.selectedScale).toEqual(null);

			deferredGetScale.resolve(scale);
			scope.$apply();

			expect(scope.selectedScale).toEqual(scale);
		});

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

	describe('$scope.updateSelectedScale', function() {

		it('should sync the updated scale in the scales list', function() {

			var scaleToUpdate = angular.copy(PERCENTAGE),
				newName = 'Not Percentage';

			controller.scales = [{
				id: 1,
				name: scaleToUpdate.name
			}];

			// Select our scale for editing
			scope.selectedItem.id = 1;

			// "Update" our scale
			scaleToUpdate.name = newName;

			scope.updateSelectedScale(scaleToUpdate);

			expect(controller.scales[0].name).toEqual(newName);
		});

		it('should remove the updated scale in the scales list if the scale is undefined', function() {

			var id = 1;

			controller.scales = [{
				id: id,
				name: PERCENTAGE.name
			}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Delete" our variable
			scope.updateSelectedScale();

			expect(controller.scales.length).toEqual(0);
		});

		it('should only update the scale in the scales list matched by id', function() {

			var detailedScaleToUpdate = angular.copy(PERCENTAGE),

				displayScaleToLeaveAlone = {
					id: 2,
					name: 'Another Scale'
				},

				displayScaleToUpdate = {
					id: 1,
					name: detailedScaleToUpdate.name
				},

				newName = 'Not Cut and Dry';

			controller.scales = [displayScaleToLeaveAlone, displayScaleToUpdate];

			// Select our scale for editing
			scope.selectedItem.id = 1;

			// "Update" our scale
			detailedScaleToUpdate.name = newName;

			scope.updateSelectedScale(detailedScaleToUpdate);

			// Ensure non-matching scale was left alone
			expect(controller.scales[0]).toEqual(displayScaleToLeaveAlone);
		});

		it('should not update any scales if there is no scale in the list with a matching id', function() {

			var scaleToUpdate = angular.copy(PERCENTAGE),

				nonMatchingScale = {
					id: 1,
					name: 'Non Matching Scale'
				},

				anotherNonMatchingScale = {
					id: 2,
					name: 'Another Non Matching Scale'
				};

			controller.scales = [nonMatchingScale, anotherNonMatchingScale];

			// Select a scale not in the list (shouldn't happen, really)
			scope.selectedItem.id = 3;

			scope.updateSelectedScale(scaleToUpdate);

			// Ensure no updates happened
			expect(controller.scales[0]).toEqual(nonMatchingScale);
			expect(controller.scales[1]).toEqual(anotherNonMatchingScale);
		});
	});
});
