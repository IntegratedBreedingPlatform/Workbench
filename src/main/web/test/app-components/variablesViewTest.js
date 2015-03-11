/*global angular, expect, inject, spyOn*/
'use strict';

describe('Variables Controller', function() {

	// This format is what is returned by a getVariables() call
	var PLANT_VIGOR = {
			id: 1,
			name: 'Plant Vigor',
			alias: '',
			description: 'A little vigourous',
			propertySummary: {
				id: 1,
				name: 'Plant Vigor'
			},
			methodSummary: {
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
			variableTypeIds: [1],
			favourite: true
		},

		PLANT_VIGOR_DETAILED,

		q,
		controller,
		scope,

		variablesService,
		panelService,

		deferredGetVariable,
		deferredGetVariables,
		deferredGetFavVariables;

	// This format is what is returned by a getVariable() call (singular)
	PLANT_VIGOR_DETAILED = angular.copy(PLANT_VIGOR);

	delete PLANT_VIGOR_DETAILED.id;

	PLANT_VIGOR_DETAILED.deletable = true;
	PLANT_VIGOR_DETAILED.metadata = {};
	PLANT_VIGOR_DETAILED.editableFields = ['name', 'description'];

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
					Property: PLANT_VIGOR.propertySummary.name,
					Method: PLANT_VIGOR.methodSummary.name,
					Scale: PLANT_VIGOR.scale.name,
					'action-favourite': PLANT_VIGOR.favourite
				}];

			expect(controller.transformToDisplayFormat(rawVariables)).toEqual(transformedVariabless);
		});

		it('should default values to empty strings if they are not present', function() {
			var rawVariables = [PLANT_VIGOR],
				transformedVariables;

			// Null out some values
			rawVariables[0].propertySummary = null;
			rawVariables[0].methodSummary = null;
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

		it('should set the selected variable to null before retrieving the selected property', function() {

			var selectedId = 123,
				panelName = 'variables';

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showVariableDetails();

			expect(scope.selectedVariable).toEqual(null);

			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();

			expect(scope.selectedVariable).toEqual(PLANT_VIGOR_DETAILED);
		});

		it('should retrieve the selected variable and display the panel', function() {

			var selectedId = 123,
				panelName = 'variables';

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showVariableDetails();
			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();

			expect(variablesService.getVariable).toHaveBeenCalledWith(selectedId);
			expect(scope.selectedVariable).toEqual(PLANT_VIGOR_DETAILED);
			expect(panelService.showPanel).toHaveBeenCalledWith(panelName);
		});
	});

	describe('$scope.updateSelectedVariable', function() {

		it('should sync the updated variable in the variables list', function() {

			var updateSelectedVariable = angular.copy(PLANT_VIGOR_DETAILED),
				newName = 'Not Plant Vigor',
				id = 1;

			controller.variables = [{
					id: id,
					Name: updateSelectedVariable.name,
					Property: updateSelectedVariable.propertySummary.name,
					Method: updateSelectedVariable.methodSummary.name,
					Scale: updateSelectedVariable.scale.name
				}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Update" our variable
			updateSelectedVariable.name = newName;

			scope.updateSelectedVariable(updateSelectedVariable);

			expect(controller.variables[0].Name).toEqual(newName);
		});

		it('should only update the variable in the variables list matched by id', function() {

			var detailedVariableToUpdate = angular.copy(PLANT_VIGOR_DETAILED),

				displayVariableToLeaveAlone = {
					id: 2,
					Property: 'A Property',
					Method: 'A Method',
					Scale: 'A Scale'
				},

				displayVariableToUpdate = {
					id: 1,
					Property: detailedVariableToUpdate.propertySummary.name,
					Method: detailedVariableToUpdate.methodSummary.name,
					Scale: detailedVariableToUpdate.scale.name
				},

				newName = 'Not Plant Vigor';

			controller.variables = [displayVariableToLeaveAlone, displayVariableToUpdate];

			// Select our variable for editing
			scope.selectedItem.id = 1;

			// "Update" our variable
			detailedVariableToUpdate.name = newName;

			scope.updateSelectedVariable(detailedVariableToUpdate);

			// Ensure non-matching variable was left alone
			expect(controller.variables[0]).toEqual(displayVariableToLeaveAlone);
		});

		it('should not update any variables if there is no variable in the list with a matching id', function() {

			var variableToUpdate = angular.copy(PLANT_VIGOR_DETAILED),

				nonMatchingVariable = {
					id: 1,
					Property: 'A Property',
					Method: 'A Method',
					Scale: 'A Scale'
				},

				anotherNonMatchingVariable = {
					id: 2,
					Property: 'Another Property',
					Method: 'Another Method',
					Scale: 'Another Scale'
				};

			controller.variables = [nonMatchingVariable, anotherNonMatchingVariable];

			// Select a property not in the list (shouldn't happen, really)
			scope.selectedItem.id = 3;

			scope.updateSelectedVariable(variableToUpdate);

			// Ensure no updates happened
			expect(controller.variables[0]).toEqual(nonMatchingVariable);
			expect(controller.variables[1]).toEqual(anotherNonMatchingVariable);
		});
	});

});
