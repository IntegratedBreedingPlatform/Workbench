/*global angular, expect, inject, spyOn, jasmine*/
'use strict';

describe('Variables Controller', function() {

	// This format is what is returned by a getVariables() call
	var CATEGORICAL_TYPE = {
			id: 1,
			name: 'Categorical'
		},

		NUMERIC_TYPE = {
			id: 2,
			name: 'Numeric'
		},

		PLANT_VIGOR = {
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
				dataType: NUMERIC_TYPE,
				validValues: {
					min: 1,
					max: 5
				}
			},
			variableTypes: [{
				id: 1,
				name: 'Analysis',
				description: ''
			}],
			favourite: true,
			obsolete: false,
			metadata: {
				dateCreated: new Date()
			}
		},

		FAVOURITE_VARIABLE = {
			id: 123,
			name: PLANT_VIGOR.name,
			property: PLANT_VIGOR.property.name,
			method: PLANT_VIGOR.method.name,
			scale: PLANT_VIGOR.scale.name,
			'action-favourite': { iconValue: 'star' }
		},

		PLANT_VIGOR_DETAILED = angular.copy(PLANT_VIGOR),
		PLANT_VIGOR_CONVERTED = angular.copy(PLANT_VIGOR),

		q,
		controller,
		scope,
		timeout,
		routeParams,

		variablesService,
		panelService,

		deferredGetVariable,
		deferredUpdateVariable,
		deferredGetVariables,
		deferredGetFavVariables;

	delete PLANT_VIGOR_CONVERTED.favourite;
	PLANT_VIGOR_CONVERTED['action-favourite'] = {
		iconValue: 'star',
		iconFunction: jasmine.any(Function)
	};
	PLANT_VIGOR_CONVERTED.scaleDataType = NUMERIC_TYPE;
	PLANT_VIGOR_CONVERTED.metadata = {
		dateCreated: new Date('2015-06-15')
	};

	PLANT_VIGOR_DETAILED.metadata = {
		deletable: true,
		editableFields: ['name', 'description'],
		dateCreated: new Date()
	};

	beforeEach(function() {
		module('variablesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope, $timeout) {
		q = $q;
		scope = $rootScope;
		timeout = $timeout;

		variablesService = {
			getVariable: function() {
				deferredGetVariable = q.defer();
				return deferredGetVariable.promise;
			},
			updateVariable: function() {
				deferredUpdateVariable = q.defer();
				return deferredUpdateVariable.promise;
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

		routeParams = {id: null}

		spyOn(variablesService, 'getVariable').and.callThrough();
		spyOn(variablesService, 'updateVariable').and.callThrough();
		spyOn(variablesService, 'getVariables').and.callThrough();
		spyOn(variablesService, 'getFavouriteVariables').and.callThrough();
		spyOn(panelService, 'showPanel');


		controller = $controller('VariablesController', {
			$scope: scope,
			variablesService: variablesService,
			panelService: panelService,
			$routeParams :routeParams
	});

		spyOn(controller, 'transformToDisplayFormat').and.callThrough();
		spyOn(controller, 'transformDetailedVariableToDisplayFormat').and.callThrough();
	}));

	describe('ctrl.transformDetailedVariableToDisplayFormat', function() {

		it('should transform a detailed variable into display format', function() {
			var newId = 3,
				transformedVariable = {
					id: newId,
					name: PLANT_VIGOR_DETAILED.name,
					alias: PLANT_VIGOR_DETAILED.alias,
					property: PLANT_VIGOR_DETAILED.property.name,
					method: PLANT_VIGOR_DETAILED.method.name,
					scale: PLANT_VIGOR_DETAILED.scale.name,
					variableTypes: PLANT_VIGOR_DETAILED.variableTypes,
					scaleDataType: PLANT_VIGOR_DETAILED.scale.dataType,
					'action-favourite': {
						iconValue: 'star',
						iconFunction: jasmine.any(Function)
					}
				};

			expect(controller.transformDetailedVariableToDisplayFormat(PLANT_VIGOR_DETAILED, newId)).toEqual(
				jasmine.objectContaining(transformedVariable));
		});

		it('should default some values to empty strings if they are not present', function() {
			var rawVariable = angular.copy(PLANT_VIGOR_DETAILED),
				createdDate = new Date(),
				transformedVariable;

			createdDate.setHours(0, 0, 0, 0);

			// Null out some values
			rawVariable.alias = null;
			rawVariable.property = null;
			rawVariable.method = null;
			rawVariable.scale = null;
			rawVariable.variableTypes = null;

			rawVariable.metadata = {
				dateCreated: createdDate
			};

			transformedVariable = {
				id: PLANT_VIGOR_DETAILED.id,
				name: PLANT_VIGOR_DETAILED.name,
				alias: '',
				property: '',
				method: '',
				scale: '',
				variableTypes: null,
				scaleDataType: null,
				metadata: {
					dateCreated: createdDate
				},
				'action-favourite': {
					iconValue: 'star',
					iconFunction: jasmine.any(Function)
				}
			};

			expect(controller.transformDetailedVariableToDisplayFormat(rawVariable, rawVariable.id)).toEqual(
				jasmine.objectContaining(transformedVariable));
		});

		it('should transform variables into display format', function() {
			var jsonData = [PLANT_VIGOR];

			deferredGetVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getVariables).toHaveBeenCalled();
			expect(controller.transformToDisplayFormat).toHaveBeenCalledWith(jsonData);
		});

		it('should show a message if there are no variables returned', function() {
			var jsonData = [];

			deferredGetVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getVariables).toHaveBeenCalled();
			expect(controller.showNoVariablesMessage).toBe(true);
		});

		it('should show an error if any of the variables are missing an id or name', function() {
			var plantVigor = angular.copy(PLANT_VIGOR),
				jsonData = [plantVigor];

			jsonData[0].name = '';
			deferredGetVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getVariables).toHaveBeenCalled();
			expect(controller.problemGettingList).toBe(true);
		});

		it('should show a message if there was a problem getting data for variables', function() {
			deferredGetVariables.reject();
			scope.$apply();

			expect(controller.showAllVariablesThrobberWrapper).toBe(false);
			expect(controller.problemGettingList).toBe(true);
		});

		it('should transform favourite variables into display format', function() {
			var jsonData = [PLANT_VIGOR];

			deferredGetFavVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getFavouriteVariables).toHaveBeenCalled();
			expect(controller.transformToDisplayFormat).toHaveBeenCalledWith(jsonData);
		});

		it('should show a message if there are no favourite variables returned', function() {
			var jsonData = [];

			deferredGetFavVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getFavouriteVariables).toHaveBeenCalled();
			expect(controller.showNoFavouritesMessage).toBe(true);
		});

		it('should show an error if any of the favourite variables are missing an id or name', function() {
			var jsonData = [angular.copy(PLANT_VIGOR)];

			jsonData[0].name = '';
			deferredGetFavVariables.resolve(jsonData);
			scope.$apply();

			expect(variablesService.getVariables).toHaveBeenCalled();
			expect(controller.problemGettingFavouriteList).toBe(true);
		});

		it('should set null created date if created date metadata is null ', function() {
			var variable = angular.copy(PLANT_VIGOR),
				result;

			variable.metadata = {
				dateCreated: null
			};

			result = controller.transformDetailedVariableToDisplayFormat(variable, PLANT_VIGOR.id);

			expect(result.metadata.dateCreated).toBe(null);
		});

		it('should show a message if there was a problem getting data for favourite variables', function() {
			deferredGetFavVariables.reject();
			scope.$apply();

			expect(controller.showFavouritesThrobberWrapper).toBe(false);
			expect(controller.problemGettingFavouriteList).toBe(true);
		});

		it('should set the selected item to be an object with an id property set to null by default', function() {
			expect(scope.selectedItem).toEqual({id: null});
		});

		it('should set the selected variable to be an empty object by default', function() {
			expect(scope.selectedVariable).toEqual({});
		});

		it('should show the all variables throbber after a delay', function() {
			timeout.flush();
			expect(controller.showAllVariablesThrobber).toBe(true);
		});

		it('should show the favourites throbber after a delay', function() {
			timeout.flush();
			expect(controller.showFavouritesThrobber).toBe(true);
		});

	});

	describe('$scope.addAliasToTableIfPresent', function() {

		it('should not change colHeaders if it already contains alias', function() {
			var variables = [PLANT_VIGOR],
			colHeaders = ['name', 'alias'];

			controller.colHeaders = colHeaders;
			scope.addAliasToTableIfPresent(variables);

			expect(controller.colHeaders).toBe(colHeaders);
		});

		it('should add alias to colHeaders if a variable has an alias', function() {
			var variables = [{
				name: PLANT_VIGOR.name,
				alias: 'PlVgr'
			}];

			controller.colHeaders = ['name', 'description'];
			scope.addAliasToTableIfPresent(variables);

			expect(controller.colHeaders).toEqual(['name', 'alias', 'description']);
		});

		it('should not add alias to colHeaders if no variable has an alias', function() {
			var variables = [PLANT_VIGOR],
			colHeaders = ['name', 'description'];

			controller.colHeaders = colHeaders;
			scope.addAliasToTableIfPresent(variables);

			expect(controller.colHeaders).toBe(colHeaders);
		});
	});

	describe('$scope.showVariableDetails', function() {

		beforeEach(function() {
			scope.selectedItem.id = 123;
			scope.panelName = 'variables';
		});

		it('should set the selected variable to null before retrieving the selected property', function() {
			scope.showVariableDetails();

			expect(scope.selectedVariable).toEqual(null);

			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();

			expect(scope.selectedVariable).toEqual(PLANT_VIGOR_DETAILED);
		});

		it('should retrieve the selected variable and display the panel', function() {
			scope.showVariableDetails();
			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();

			expect(variablesService.getVariable).toHaveBeenCalledWith(scope.selectedItem.id);
			expect(scope.selectedVariable).toEqual(PLANT_VIGOR_DETAILED);
			expect(panelService.showPanel).toHaveBeenCalledWith(scope.panelName);
		});
	});

	describe('$scope.toggleFavourite', function() {

		var nonFavouriteVariable,
			favouriteVariable;

		beforeEach(function() {
			scope.selectedItem.id = 123;

			favouriteVariable = angular.copy(FAVOURITE_VARIABLE);
			favouriteVariable.id = scope.selectedItem.id;

			nonFavouriteVariable = angular.copy(FAVOURITE_VARIABLE);
			nonFavouriteVariable.id = scope.selectedItem.id;
			nonFavouriteVariable['action-favourite'].iconValue = 'star-empty';

			spyOn(scope, 'updateSelectedVariable').and.callThrough();
		});

		it('should change the icon of the selected variable to the opposite one (star to star-empty)', function() {
			controller.variables = [favouriteVariable];
			scope.toggleFavourite();
			expect(scope.updateSelectedVariable).toHaveBeenCalledWith(nonFavouriteVariable);
		});

		it('should change the icon of the selected variable to the opposite one (star-empty to star)', function() {
			controller.variables = [nonFavouriteVariable];
			scope.toggleFavourite();
			expect(scope.updateSelectedVariable).toHaveBeenCalledWith(FAVOURITE_VARIABLE);
		});

		it('should toggle the value of selected variable on the backend', function() {
			controller.variables = [favouriteVariable];
			scope.toggleFavourite();
			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();
			expect(scope.selectedVariable.favourite).toBe(false);

			controller.variables = [nonFavouriteVariable];
			scope.toggleFavourite();
			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();
			expect(scope.selectedVariable.favourite).toBe(true);
		});

		it('should save the changes to the backend when selected variable is favourited or defavourited', function() {
			controller.variables = [favouriteVariable];
			scope.toggleFavourite();
			deferredGetVariable.resolve(PLANT_VIGOR_DETAILED);
			scope.$apply();
			expect(variablesService.updateVariable).toHaveBeenCalledWith(scope.selectedItem.id, PLANT_VIGOR_DETAILED);
		});
	});

	describe('$scope.updateSelectedVariable', function() {

		it('should sync the updated variable in the variables list', function() {
			var variable = angular.copy(PLANT_VIGOR),
				variableToUpdate = angular.copy(PLANT_VIGOR_CONVERTED),
				newName = 'Not Plant Vigor';

			controller.variables = [variable];
			controller.favouriteVariables = [variable];

			// Select our variable for editing
			scope.selectedItem.id = variable.id;

			// "Update" our variable
			variableToUpdate.name = newName;

			scope.updateSelectedVariable(variableToUpdate);

			expect(controller.variables[0].name).toEqual(newName);
			expect(controller.favouriteVariables[0].name).toEqual(newName);
		});

		it('should remove the updated variable in the variables list if the variable is undefined', function() {

			var id = 1;

			controller.variables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.property.name,
				method: PLANT_VIGOR.method.name,
				scale: PLANT_VIGOR.scale.name
			}];

			controller.favouriteVariables = [{
				id: id,
				name: PLANT_VIGOR.name,
				property: PLANT_VIGOR.property.name,
				method: PLANT_VIGOR.method.name,
				scale: PLANT_VIGOR.scale.name
			}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Delete" our variable
			scope.updateSelectedVariable();

			expect(controller.variables.length).toEqual(0);
			expect(controller.favouriteVariables.length).toEqual(0);
		});

		it('should remove an updated variable from the favourites list if it is no longer a favourite', function() {

			var variableToUpdate = angular.copy(PLANT_VIGOR_DETAILED),
				variable = angular.copy(PLANT_VIGOR_CONVERTED);

			controller.variables = [variable];

			controller.favouriteVariables = [variable];

			// Select our variable for editing
			scope.selectedItem.id = variable.id;

			// "Update" our variable
			variableToUpdate.favourite = false;

			scope.updateSelectedVariable(variableToUpdate);

			expect(controller.variables.length).toEqual(1);
			expect(controller.favouriteVariables.length).toEqual(0);

			expect(controller.variables[0]['action-favourite'].iconValue).toEqual('star-empty');
		});

		it('should only update the variable in the variables list matched by id', function() {

			var detailedVariableToUpdate = angular.copy(PLANT_VIGOR_DETAILED),

			displayVariableToLeaveAlone = {
				id: 2,
				name: 'A Name',
				property: 'A Property',
				method: 'A Method',
				scale: 'A Scale'
			},

			newName = 'Not Plant Vigor';

			controller.variables = [displayVariableToLeaveAlone];
			controller.favouriteVariables = [displayVariableToLeaveAlone];

			// Select our variable for editing
			scope.selectedItem.id = 1;

			// "Update" our variable
			detailedVariableToUpdate.name = newName;

			scope.updateSelectedVariable(detailedVariableToUpdate);

			// Ensure non-matching variable was left alone
			expect(controller.variables[0]).toEqual(displayVariableToLeaveAlone);
			expect(controller.favouriteVariables[0]).toEqual(displayVariableToLeaveAlone);
		});

		it('should not update variables list if there is no variable in the list with a matching id, but should update favourites list',
		function() {

			var variableToUpdate = angular.copy(PLANT_VIGOR_CONVERTED),

			nonMatchingVariable = {
				id: 1,
				name: 'test1',
				property: 'A Property',
				method: 'A Method',
				scale: 'A Scale'
			},

			anotherNonMatchingVariable = {
				id: 2,
				name: 'test2',
				property: 'Another Property',
				method: 'Another Method',
				scale: 'Another Scale'
			};

			controller.variables = [nonMatchingVariable, anotherNonMatchingVariable];
			controller.favouriteVariables = [nonMatchingVariable, anotherNonMatchingVariable];

			// Select a property not in the list (shouldn't happen, really)
			scope.selectedItem.id = 3;
			variableToUpdate.id = 3;

			scope.updateSelectedVariable(variableToUpdate);

			// Ensure no updates happened
			expect(controller.variables[0]).toEqual(nonMatchingVariable);
			expect(controller.variables[1]).toEqual(anotherNonMatchingVariable);
			expect(controller.favouriteVariables[0]).toEqual(variableToUpdate);
			expect(controller.favouriteVariables[1]).toEqual(nonMatchingVariable);
			expect(controller.favouriteVariables[2]).toEqual(anotherNonMatchingVariable);
		});
	});

	describe('$scope.isFilterActive', function() {
		it('should return true if there is at least one variable type selected to filter by', function() {
			scope.filterOptions = {
				variableTypes: ['type']
			};
			expect(scope.isFilterActive()).toBe(true);
		});

		it('should return true if there is a scale data type selected to filter by', function() {
			scope.filterOptions = {
				variableTypes: [],
				scaleDataType: 'Numeric'
			};
			expect(scope.isFilterActive()).toBe(true);
		});

		it('should return true if the date created to date is selected to filter by', function() {
			scope.filterOptions = {
				variableTypes: [],
				dateCreatedTo: new Date()
			};
			expect(scope.isFilterActive()).toBe(true);
		});

		it('should return true if the date created from date is selected to filter by', function() {
			scope.filterOptions = {
				variableTypes: [],
				dateCreatedFrom: new Date()
			};
			expect(scope.isFilterActive()).toBe(true);
		});

		it('should return false if there are no values selected to filter by', function() {
			scope.filterOptions = {
				variableTypes: []
			};
			expect(scope.isFilterActive()).toBe(false);
		});

	});

	describe('$scope.optionsFilter', function() {

		it('should return true if filter options are not set', function() {
			scope.filterOptions = undefined;
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);

			scope.filterOptions = { variableTypes: undefined };
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);

			scope.filterOptions = { variableTypes: [] };
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);
		});

		it('should return true if there is a match of variable type in variable and filter options', function() {
			scope.filterOptions = {
				variableTypes: [{
					id: 1,
					name: 'Analysis',
					description: '',
					deletable: 'true'
				}]
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);
		});

		it('should return false if there is no match of variable type in variable and filter options', function() {
			scope.filterOptions = {
				variableTypes: [{
					id: 8,
					name: 'Trait',
					description: 'Characteristics of a germplasm to be recorded during a study.',
					deletable: 'true'
				}]
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(false);
		});

		it('should return true if there is a match of scale data type in variable and filter options', function() {
			scope.filterOptions = {
				variableTypes: [],
				scaleDataType: NUMERIC_TYPE
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);
		});

		it('should return false if there is no match of scale data type in variable and filter options', function() {
			scope.filterOptions = {
				variableTypes: [],
				scaleDataType: CATEGORICAL_TYPE
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(false);
		});

		it('should return true if date created in variable metadata is between date created from and date created to of filter options',
		function() {
			scope.filterOptions = {
				variableTypes: [],
				dateCreatedFrom: new Date('2015-06-01'),
				dateCreatedTo: new Date('2015-07-01')
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);
		});

		it('should return true if date created in variable metadata is after date created from of filter options', function() {
			scope.filterOptions = {
				variableTypes: [],
				dateCreatedFrom: new Date('2015-06-01')
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);
		});

		it('should return false if date created in variable metadata is null', function() {
			var PLANT_VIGOR_CREATED_DATE_NULL = angular.copy(PLANT_VIGOR_CONVERTED);
			scope.filterOptions = {
				variableTypes: [],
				dateCreatedFrom: new Date('2015-06-01')
			};
			PLANT_VIGOR_CREATED_DATE_NULL.metadata.dateCreated = null;
			expect(scope.optionsFilter(PLANT_VIGOR_CREATED_DATE_NULL)).toBe(false);
		});

		it('should return true if date created in variable metadata is before date created to of filter options', function() {
			scope.filterOptions = {
				variableTypes: [],
				dateCreatedTo: new Date('2015-07-01')
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);
		});

		it('should return true if date created from or date created to of filter options are not dates', function() {
			scope.filterOptions = {
				variableTypes: [],
				dateCreatedFrom: 'notADate',
				dateCreatedTo: 'notADateEither'
			};
			expect(scope.optionsFilter(PLANT_VIGOR_CONVERTED)).toBe(true);
		});
	});

});
