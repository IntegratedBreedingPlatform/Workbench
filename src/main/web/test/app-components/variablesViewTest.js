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
			scaleSummary: {
				id: 1,
				name: 'Score'
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


	PLANT_VIGOR_DETAILED.scale = {
			id: 1,
			name: 'Score',
			dataType: {
				id: 2,
				name: 'Numeric'
			},
			validValues: {
				min: 1,
				max: 5
			}
		};

	delete PLANT_VIGOR_DETAILED.id;
	delete PLANT_VIGOR_DETAILED.scaleSummary;

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
		spyOn(controller, 'transformVariableToDisplayFormat').and.callThrough();
		spyOn(controller, 'transformDetailedVariableToDisplayFormat').and.callThrough();
	}));

	describe('ctrl.transformVariableToDisplayFormat', function() {

		it('should transform a variable summary into display format', function() {
			var rawVariable = PLANT_VIGOR,
				transformedVariable = {
					id: PLANT_VIGOR.id,
					name: PLANT_VIGOR.name,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name,
					'action-favourite': PLANT_VIGOR.favourite
				};

			expect(controller.transformVariableToDisplayFormat(rawVariable)).toEqual(transformedVariable);
		});

		it('should default values to empty strings if they are not present', function() {
			var rawVariable = angular.copy(PLANT_VIGOR),
				transformedVariable;

			// Null out some values
			rawVariable.propertySummary = null;
			rawVariable.methodSummary = null;
			rawVariable.scaleSummary = null;

			transformedVariable = {
				id: PLANT_VIGOR.id,
				name: PLANT_VIGOR.name,
				property: '',
				method: '',
				scale: '',
				'action-favourite': PLANT_VIGOR.favourite
			};
			expect(controller.transformVariableToDisplayFormat(rawVariable)).toEqual(transformedVariable);
		});

	});

	describe('ctrl.transformDetailedVariableToDisplayFormat', function() {

		it('should transform a detailed variable into display format', function() {
			var newId = 3,
				transformedVariable = {
					id: newId,
					name: PLANT_VIGOR_DETAILED.name,
					property: PLANT_VIGOR_DETAILED.propertySummary.name,
					method: PLANT_VIGOR_DETAILED.methodSummary.name,
					scale: PLANT_VIGOR_DETAILED.scale.name,
					'action-favourite': PLANT_VIGOR_DETAILED.favourite
				};

			expect(controller.transformDetailedVariableToDisplayFormat(PLANT_VIGOR_DETAILED, newId)).toEqual(transformedVariable);
		});

		it('should default values to empty strings if they are not present', function() {
			var rawVariable = angular.copy(PLANT_VIGOR_DETAILED),
				transformedVariables;

			// Null out some values
			rawVariable.propertySummary = null;
			rawVariable.methodSummary = null;
			rawVariable.scale = null;

			transformedVariables = {
				id: PLANT_VIGOR_DETAILED.id,
				name: PLANT_VIGOR_DETAILED.name,
				property: '',
				method: '',
				scale: '',
				'action-favourite': PLANT_VIGOR_DETAILED.favourite
			};
			expect(controller.transformDetailedVariableToDisplayFormat(rawVariable)).toEqual(transformedVariables);
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
					name: PLANT_VIGOR.name,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name
				}];

			controller.favouriteVariables = [{
					id: id,
					name: PLANT_VIGOR.name,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name
				}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Update" our variable
			updateSelectedVariable.name = newName;

			scope.updateSelectedVariable(updateSelectedVariable);

			expect(controller.variables[0].name).toEqual(newName);
			expect(controller.favouriteVariables[0].name).toEqual(newName);
		});

		it('should remove the updated variable in the variables list if the variable is undefined', function() {

			var id = 1;

			controller.variables = [{
					id: id,
					name: PLANT_VIGOR.name,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name
				}];

			controller.favouriteVariables = [{
					id: id,
					name: PLANT_VIGOR.name,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name
				}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Delete" our variable
			scope.updateSelectedVariable();

			expect(controller.variables.length).toEqual(0);
			expect(controller.favouriteVariables.length).toEqual(0);
		});

		it('should remove an updated variable from the favourites list if it is no longer a favourite', function() {

			var updateSelectedVariable = angular.copy(PLANT_VIGOR_DETAILED),
				id = 1;

			controller.variables = [{
					id: id,
					name: PLANT_VIGOR.name,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name,
					'action-favourite': PLANT_VIGOR.favourite
				}];

			controller.favouriteVariables = [{
					id: id,
					name: PLANT_VIGOR.name,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name,
					'action-favourite': PLANT_VIGOR.favourite
				}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Update" our variable
			updateSelectedVariable.favourite = false;

			scope.updateSelectedVariable(updateSelectedVariable);

			expect(controller.variables.length).toEqual(1);
			expect(controller.favouriteVariables.length).toEqual(0);

			expect(controller.variables[0]['action-favourite']).toEqual(false);
		});

		it('should only update the variable in the variables list matched by id', function() {

			var detailedVariableToUpdate = angular.copy(PLANT_VIGOR_DETAILED),

				displayVariableToLeaveAlone = {
					id: 2,
					property: 'A Property',
					method: 'A Method',
					scale: 'A Scale'
				},

				displayVariableToUpdate = {
					id: 1,
					property: PLANT_VIGOR.propertySummary.name,
					method: PLANT_VIGOR.methodSummary.name,
					scale: PLANT_VIGOR.scaleSummary.name
				},

				newName = 'Not Plant Vigor';

			controller.variables = [displayVariableToLeaveAlone, displayVariableToUpdate];
			controller.favouriteVariables = [displayVariableToLeaveAlone, displayVariableToUpdate];

			// Select our variable for editing
			scope.selectedItem.id = 1;

			// "Update" our variable
			detailedVariableToUpdate.name = newName;

			scope.updateSelectedVariable(detailedVariableToUpdate);

			// Ensure non-matching variable was left alone
			expect(controller.variables[0]).toEqual(displayVariableToLeaveAlone);
			expect(controller.favouriteVariables[0]).toEqual(displayVariableToLeaveAlone);
		});

		it('should not update any variables if there is no variable in the list with a matching id', function() {

			var variableToUpdate = angular.copy(PLANT_VIGOR_DETAILED),

				nonMatchingVariable = {
					id: 1,
					property: 'A Property',
					method: 'A Method',
					scale: 'A Scale'
				},

				anotherNonMatchingVariable = {
					id: 2,
					property: 'Another Property',
					method: 'Another Method',
					scale: 'Another Scale'
				};

			controller.variables = [nonMatchingVariable, anotherNonMatchingVariable];
			controller.favouriteVariables = [nonMatchingVariable, anotherNonMatchingVariable];

			// Select a property not in the list (shouldn't happen, really)
			scope.selectedItem.id = 3;

			scope.updateSelectedVariable(variableToUpdate);

			// Ensure no updates happened
			expect(controller.variables[0]).toEqual(nonMatchingVariable);
			expect(controller.variables[1]).toEqual(anotherNonMatchingVariable);
			expect(controller.favouriteVariables[0]).toEqual(nonMatchingVariable);
			expect(controller.favouriteVariables[1]).toEqual(anotherNonMatchingVariable);
		});
	});

});
