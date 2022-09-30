/*global angular*/
'use strict';

(function() {
	var app = angular.module('variablesView', ['list', 'panel', 'variables', 'variableDetails', 'utilities', 'filter', 'search']),
		DELAY = 400;

	function findAndUpdate(list, id, updatedVariable, sortFunction) {
		var selectedVariableIndex = -1;

		list.some(function(variable, index) {
			if (variable.id === id) {
				selectedVariableIndex = index;
				return true;
			}
		});
		// Not much we can really do if we don't find it in the list. Just don't update.
		if (selectedVariableIndex !== -1) {
			if (updatedVariable) {
				list[selectedVariableIndex] = updatedVariable;
				sortFunction(list);
			} else {
				list.splice(selectedVariableIndex, 1);
			}
		}
	}

	function find(list, id) {
		var foundVariable = null;

		list.some(function(variable) {
			if (variable.id === id) {
				foundVariable = variable;
				return true;
			}
		});
		return foundVariable;
	}

	function findAndRemove(list, id) {
		findAndUpdate(list, id);
	}

	function addNotFound (list, id, updatedVariable, sortFunction) {
		//if not in the list, add to the list
		if (!find(list, id)) {
			list.push(updatedVariable);
			sortFunction(list);
		}
	}

	app.controller('VariablesController', ['$scope', 'variablesService', 'panelService', '$timeout', 'collectionUtilities', '$routeParams',
		function ($scope, variablesService, panelService, $timeout, collectionUtilities, $routeParams) {
			var ctrl = this;

			ctrl.variables = [];
			ctrl.formula = null;
			ctrl.favouriteVariables = [];
			ctrl.showAllVariablesThrobberWrapper = true;
			ctrl.showFavouritesThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'property', 'method', 'scale', 'action-favourite'];
			ctrl.colFormulaHeaders = ['calculation','inputVariables','buttons'];
			ctrl.problemGettingList = false;

			$scope.filterByProperties = ['name', 'alias', 'property', 'method', 'scale'];
			$scope.panelName = 'variables';
			$scope.filterOptions = {
				variableTypes: [],
				scaleDataType: null,
				obsolete: false
			};

			$scope.isFilterActive = function() {
				var variableTypesFilterActive = $scope.filterOptions.variableTypes.length > 0,
					scaleDataTypeFilterActive = !!$scope.filterOptions.scaleDataType,
					dateCreatedFilterActive = !!$scope.filterOptions.dateCreatedFrom || !!$scope.filterOptions.dateCreatedTo
					obsoleteFilterActive = !!$scope.filterOptions.obsolete;

				return variableTypesFilterActive || scaleDataTypeFilterActive || dateCreatedFilterActive || obsoleteFilterActive;
			};

			$scope.optionsFilter = function(variable) {
				var variableTypeMatch = true,
					scaleDataTypeMatch = true,
					obsoleteFilterMatch = true,
					dateCreatedMatch = true,
					variableDateCreatedTime;

				// Include variable if the filter options have not been set
				if (!$scope.filterOptions || !$scope.filterOptions.variableTypes) {
					return true;
				}

				// Check whether variable's variable type matches the chosen types
				if ($scope.filterOptions.variableTypes.length > 0) {
					variableTypeMatch = $scope.filterOptions.variableTypes.every(function(filterVariableType) {
						// Check if any of the variable types for the variable match the given variable types to filter on
						return variable.variableTypes === undefined ? false : variable.variableTypes.some(function(itemVariableType) {
							return angular.equals(filterVariableType.id, itemVariableType.id);
						});
					});
				}

				obsoleteFilterMatch =  $scope.filterOptions.obsolete || angular.equals(variable.obsolete, $scope.filterOptions.obsolete);

				if ($scope.filterOptions.scaleDataType) {
					scaleDataTypeMatch =  angular.equals(variable.scaleDataType, $scope.filterOptions.scaleDataType);
				}

				if ($scope.filterOptions.dateCreatedFrom || $scope.filterOptions.dateCreatedTo) {

					if (variable.metadata.dateCreated) {
						variableDateCreatedTime = variable.metadata.dateCreated.getTime();

						if ($scope.filterOptions.dateCreatedFrom && $scope.filterOptions.dateCreatedTo &&
							$scope.filterOptions.dateCreatedFrom.getTime && $scope.filterOptions.dateCreatedTo.getTime) {

							dateCreatedMatch = ($scope.filterOptions.dateCreatedFrom.getTime() <= variableDateCreatedTime) &&
								(variableDateCreatedTime <= $scope.filterOptions.dateCreatedTo.getTime());

						} else if ($scope.filterOptions.dateCreatedFrom && $scope.filterOptions.dateCreatedFrom.getTime) {

							dateCreatedMatch = ($scope.filterOptions.dateCreatedFrom.getTime() <= variableDateCreatedTime);

						} else if ($scope.filterOptions.dateCreatedTo && $scope.filterOptions.dateCreatedTo.getTime) {

							dateCreatedMatch = (variableDateCreatedTime <= $scope.filterOptions.dateCreatedTo.getTime());

						}

					} else {
						// If there is no created date then remove from results
						dateCreatedMatch = false;
					}
				}

				return variableTypeMatch && scaleDataTypeMatch && dateCreatedMatch && obsoleteFilterMatch;
			};

			$timeout(function() {
				ctrl.showAllVariablesThrobber = true;
				ctrl.showFavouritesThrobber = true;
			}, DELAY);

			variablesService.getFavouriteVariables().then(function(variables) {
				try {
					ctrl.favouriteVariables = ctrl.transformToDisplayFormat(variables);

					if (ctrl.favouriteVariables.length === 0) {
						ctrl.showNoFavouritesMessage = true;
						return;
					}

					$scope.addAliasToTableIfPresent(ctrl.favouriteVariables);

				} catch (e) {
					// The variables could not be transformed to display format
					ctrl.problemGettingFavouriteList = true;
				}

			}, function() {
				ctrl.problemGettingFavouriteList = true;
			}).finally (function() {
				ctrl.showFavouritesThrobberWrapper = false;
			});

			variablesService.getVariables().then(function(variables) {
				try {
					ctrl.variables = ctrl.transformToDisplayFormat(variables);

					if (ctrl.variables.length === 0) {
						ctrl.showNoVariablesMessage = true;
						return;
					}

					$scope.addAliasToTableIfPresent(ctrl.variables);

				} catch (e) {
					// The variables could not be transformed to display format
					ctrl.problemGettingList = true;
				}

			}, function() {
				ctrl.problemGettingList = true;
			}).finally (function() {
				ctrl.showAllVariablesThrobberWrapper = false;
			});

			ctrl.transformDetailedVariableToDisplayFormat = function(variable, id) {

				var tempDateCreated = variable.metadata.dateCreated ? new Date(variable.metadata.dateCreated) : null;
				if (tempDateCreated) {
					tempDateCreated.setHours(0, 0, 0, 0);
				}

				var returnValue = {
					id: id || variable.id,
					name: variable.name,
					alias: variable.alias || '',
					property: variable.property && variable.property.name || '',
					method: variable.method && variable.method.name || '',
					scale: variable.scale && variable.scale.name || '',
					variableTypes: variable.variableTypes, // used for filtering
					obsolete: variable.obsolete, // used for filtering
					scaleDataType: variable.scale && variable.scale.dataType,
					metadata: {
						dateCreated: tempDateCreated
					},
					'action-favourite': {
						iconValue: variable.favourite ? 'star' : 'star-empty',
						iconFunction: $scope.toggleFavourite
					}
				};

				return returnValue;
			};

			/*
			Transforms each of the given variables into the correct object structure to be displayed in the list.
			This object structure must include the function to call to make a variable a favourite or remove it from
			being a favourite.

			@return the array of transformed variables
			@throws malformed variable exception if any variable is missing an id or name
			*/
			ctrl.transformToDisplayFormat = function(variables) {
				var transformedVariables = [],
					i;

				for (i = 0; i < variables.length; i++) {
					if (!variables[i].id || !variables[i].name) {
						// Throw exception if there is any variable that comes back with no id or name
						throw new Error('Malformed variable');
					}
					transformedVariables.push(ctrl.transformDetailedVariableToDisplayFormat(variables[i]));
				}

				return transformedVariables;
			};

			// function that checks to see if an alias has been added for the first time and eeds a column for it
			$scope.addAliasToTableIfPresent = function(variables) {
				var ALIAS = 'alias';

				if (ctrl.colHeaders.indexOf(ALIAS) === -1) {

					// Add alias into the table if at least one variable has an alias
					variables.some(function(variable) {
						if (variable.alias) {
							// Add alias after name
							ctrl.colHeaders.splice(1, 0, ALIAS);
							return true;
						}
					});
				}
			};

			$scope.showVariableDetails = function(id) {

				if (id) {
					$scope.selectedItem.id = id;
				}

				// Ensure the previously selected variable doesn't show in the panel before we've retrieved the new one
				$scope.selectedVariable = null;

				variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
					$scope.selectedVariable = variable;
					ctrl.formula = variable.formula;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.toggleFavourite = function() {
				var transformedVariable = find(ctrl.variables, $scope.selectedItem.id),
					isFavourite = transformedVariable['action-favourite'].iconValue === 'star';

				// Change whether variable is favourite
				transformedVariable['action-favourite'].iconValue = isFavourite ? 'star-empty' : 'star';
				// Update the variable
				$scope.updateSelectedVariable(transformedVariable);

				// Retrieve variable and update it
				variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
					$scope.selectedVariable = variable;
					$scope.selectedVariable.favourite = !isFavourite;
					variablesService.updateVariable($scope.selectedItem.id, $scope.selectedVariable);
				});

				// Show or hide the no favourites message depending on whether there are any favourite variables currently
				ctrl.showNoFavouritesMessage = ctrl.favouriteVariables.length > 0 ? false : true;
			};

			$scope.updateSelectedVariable = function(updatedVariable) {
				var transformedVariable,
					isFavourite;

				if (updatedVariable) {
					// If property 'action-favourite' present we assume that variable is already transformed, as it is added during
					// transformation
					transformedVariable = updatedVariable['action-favourite'] ? updatedVariable :
						ctrl.transformDetailedVariableToDisplayFormat(updatedVariable, $scope.selectedItem.id);

					isFavourite = transformedVariable['action-favourite'].iconValue === 'star';

					if (isFavourite) {
						findAndUpdate(ctrl.favouriteVariables, $scope.selectedItem.id, transformedVariable, collectionUtilities.sortByName);
						addNotFound(ctrl.favouriteVariables, $scope.selectedItem.id, transformedVariable, collectionUtilities.sortByName);
					} else {
						// If the variable is not a favourite, we need to remove it if it's in the favourites list
						findAndRemove(ctrl.favouriteVariables, $scope.selectedItem.id);
					}

					findAndUpdate(ctrl.variables, $scope.selectedItem.id, transformedVariable, collectionUtilities.sortByName);

					$scope.selectedVariable = updatedVariable;
				} else {
					// If the updated variable is null or undefined, then remove it from the all variables and favourites lists
					findAndRemove(ctrl.variables, $scope.selectedItem.id);
					findAndRemove(ctrl.favouriteVariables, $scope.selectedItem.id);
				}
			};

			// An object only containing the selected item's id. This format is required for passing to the list directive.
			$scope.selectedItem = {id: null};
			// Contains the entire selected variable object once it has been updated.
			$scope.selectedVariable = {};

			if ($routeParams.id) {
				$scope.showVariableDetails($routeParams.id);
			}
		}
	]);

}());
