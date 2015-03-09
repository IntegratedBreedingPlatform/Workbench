/*global expect, inject, spyOn*/
'use strict';

describe('Variables Controller', function() {
	var PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			alias: '',
			description: 'A little vigourous',
			property: {
				id: 1,
				name: 'Plant Vigor'
			},
			method: {
				id: 1,
				name: 'Visual assessment at seedling stage'
			},
			scale: {
				id: 1,
				name: 'Score',
				dataType: 2,
				validValues: {
					min: 1,
					max: 5
				}
			},
			variableType: [
				1
			],
			favourite: true
		},
		q,
		controller,
		scope,

		variablesService,
		panelService,

		deferredGetVariable,
		deferredGetVariables,
		deferredGetFavVariables;

	beforeEach(function() {
		module('variablesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {
		q = $q;
		scope = $rootScope;

		variablesService = {
			getVariable: function() {
				deferredGetVariable = q.defer();
				return deferredGetVariable.promise;
			},
			getVariables: function() {
				deferredGetVariables = q.defer();
				return deferredGetVariables.promise;
			},
			getFavouriteVariables: function() {
				deferredGetFavVariables = q.defer();
				return deferredGetFavVariables.promise;
			}
		};

		panelService = {
			showPanel: function() {}
		};

		spyOn(variablesService, 'getVariable').and.callThrough();
		spyOn(variablesService, 'getVariables').and.callThrough();
		spyOn(variablesService, 'getFavouriteVariables').and.callThrough();
		spyOn(panelService, 'showPanel');

		controller = $controller('VariablesController', {
			$scope: scope,
			variablesService: variablesService,
			panelService: panelService
		});

		spyOn(controller, 'transformToDisplayFormat').and.callThrough();
	}));

	describe('ctrl.transformToDisplayFormat', function() {

		it('should transform variables into display format', function() {
			var rawVariables = [PLANT_VIGOR],
				transformedVariabless = [{
					id: PLANT_VIGOR.id,
					Name: PLANT_VIGOR.name,
					Property: PLANT_VIGOR.property.name,
					Method: PLANT_VIGOR.method.name,
					Scale: PLANT_VIGOR.scale.name,
					'action-favourite': PLANT_VIGOR.favourite
				}];

			expect(controller.transformToDisplayFormat(rawVariables)).toEqual(transformedVariabless);
		});

		it('should default values to empty strings if they are not present', function() {
			var rawVariables = [PLANT_VIGOR],
				transformedVariables;

			// Null out some values
			rawVariables[0].property = null;
			rawVariables[0].method = null;
			rawVariables[0].scale = null;

			transformedVariables = [{
				id: PLANT_VIGOR.id,
				Name: PLANT_VIGOR.name,
				Property: '',
				Method: '',
				Scale: '',
				'action-favourite': PLANT_VIGOR.favourite
			}];
			expect(controller.transformToDisplayFormat(rawVariables)).toEqual(transformedVariables);
		});

	});

	it('should transform variables into display format', function() {

		var jsonData = [PLANT_VIGOR];

		deferredGetVariables.resolve(jsonData);
		scope.$apply();

		expect(variablesService.getVariables).toHaveBeenCalled();
		expect(controller.transformToDisplayFormat).toHaveBeenCalledWith(jsonData);
	});

	it('should transform favourite variables into display format', function() {

		var jsonData = [PLANT_VIGOR];

		deferredGetFavVariables.resolve(jsonData);
		scope.$apply();

		expect(variablesService.getFavouriteVariables).toHaveBeenCalled();
		expect(controller.transformToDisplayFormat).toHaveBeenCalledWith(jsonData);
	});

	it('should set the selected item to be an object with an id property set to null by default', function() {
		expect(scope.selectedItem).toEqual({id: null});
	});

	it('should set the selected variable to be an empty object by default', function() {
		expect(scope.selectedVariable).toEqual({});
	});

	describe('$scope.showVariableDetails', function() {

		it('should retrieve the selected variable and display the panel', function() {

			var selectedId = 123,
				panelName = 'variables',
				variable = PLANT_VIGOR;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showVariableDetails();
			deferredGetVariable.resolve(variable);
			scope.$apply();

			expect(variablesService.getVariable).toHaveBeenCalledWith(selectedId);
			expect(scope.selectedVariable).toEqual(variable);
			expect(panelService.showPanel).toHaveBeenCalledWith(panelName);
		});
	});

});
