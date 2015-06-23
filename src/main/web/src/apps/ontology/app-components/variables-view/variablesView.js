/*global angular*/
'use strict';

(function() {
	var app = angular.module('variablesView', ['list', 'panel', 'variables', 'variableDetails', 'utilities', 'filter']),
		DELAY = 400;

	function transformDetailedVariableToDisplayFormat(variable, id) {
		return {
			id: id,
			name: variable.name,
			alias: variable.alias || '',
			property: variable.propertySummary && variable.propertySummary.name || '',
			method: variable.methodSummary && variable.methodSummary.name || '',
			scale: variable.scale && variable.scale.name || '',
			'action-favourite': variable.favourite ? { iconValue: 'star' } : { iconValue: 'star-empty' }
		};
	}

	function transformVariableToDisplayFormat(variable) {
		return {
			id: variable.id,
			name: variable.name,
			alias: variable.alias || '',
			property: variable.propertySummary && variable.propertySummary.name || '',
			method: variable.methodSummary && variable.methodSummary.name || '',
			scale: variable.scaleSummary && variable.scaleSummary.name || '',
			'action-favourite': variable.favourite ? { iconValue: 'star' } : { iconValue: 'star-empty' }
		};
	}

	function transformToDisplayFormat(variables, actionFunction) {
		// TODO: check that variable has an ID and name
		var transformedVariables = variables.map(transformVariableToDisplayFormat);
		// add action functions to the variables
		transformedVariables.every(function(variable) {
			variable['action-favourite'].iconFunction = actionFunction;
			return true;
		});
		return transformedVariables;
	}

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

	app.controller('VariablesController', ['$scope', 'variablesService', 'panelService', '$timeout', 'collectionUtilities',
		function($scope, variablesService, panelService, $timeout, collectionUtilities) {
			var ctrl = this;

			ctrl.variables = [];
			ctrl.favouriteVariables = [];
			ctrl.showAllVariablesThrobberWrapper = true;
			ctrl.showFavouritesThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'property', 'method', 'scale', 'action-favourite'];
			ctrl.problemGettingList = false;

			ctrl.transformToDisplayFormat = transformToDisplayFormat;
			/* Exposed for testing */
			ctrl.transformVariableToDisplayFormat = transformVariableToDisplayFormat;
			ctrl.transformDetailedVariableToDisplayFormat = transformDetailedVariableToDisplayFormat;

			$scope.filterByProperties = ['name', 'alias', 'property', 'method', 'scale'];
			$scope.panelName = 'variables';
			$scope.filterOptions = {
				variableTypes: []
			};

			$timeout(function() {
				ctrl.showAllVariablesThrobber = true;
				ctrl.showFavouritesThrobber = true;
			}, DELAY);

			variablesService.getFavouriteVariables().then(function(variables) {
				ctrl.favouriteVariables = ctrl.transformToDisplayFormat(variables, $scope.toggleFavourite);

				if (ctrl.favouriteVariables.length === 0) {
					ctrl.showNoFavouritesMessage = true;
					return;
				}

				ctrl.addAliasToTableIfPresent(ctrl.favouriteVariables);
			}, function() {
				ctrl.problemGettingFavouriteList = true;
			}).finally (function() {
				ctrl.showFavouritesThrobberWrapper = false;
			});

			variablesService.getVariables().then(function(variables) {
				ctrl.variables = ctrl.transformToDisplayFormat(variables, $scope.toggleFavourite);

				if (ctrl.variables.length === 0) {
					ctrl.showNoVariablesMessage = true;
					return;
				}

				ctrl.addAliasToTableIfPresent(ctrl.variables);
			}, function() {
				ctrl.problemGettingList = true;
			}).finally (function() {
				ctrl.showAllVariablesThrobberWrapper = false;
			});

			// Exposed for testing
			ctrl.addAliasToTableIfPresent = function(variables) {
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

			$scope.showVariableDetails = function() {

				// Ensure the previously selected variable doesn't show in the panel before we've retrieved the new one
				$scope.selectedVariable = null;

				variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
					$scope.selectedVariable = variable;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.toggleFavourite = function() {
				var transformedVariable = find(ctrl.variables, $scope.selectedItem.id);

				transformedVariable['action-favourite'].iconValue = (transformedVariable['action-favourite'].iconValue === 'star') ?
					'star-empty' : 'star';
				$scope.updateSelectedVariable(transformedVariable);

				variablesService.getVariable($scope.selectedItem.id).then(function(variable) {
					$scope.selectedVariable = variable;
					$scope.selectedVariable.favourite = transformedVariable['action-favourite'].iconValue === 'star';
					variablesService.updateVariable($scope.selectedItem.id, $scope.selectedVariable);
				});

				// Show or hide the no favourites message depending on whether there are any favourite variables currently
				ctrl.showNoFavouritesMessage = ctrl.favouriteVariables.length > 0 ? false : true;
			};

			$scope.updateSelectedVariable = function(updatedVariable) {
				var transformedVariable;

				if (updatedVariable) {
					//if property 'action-favourite' present we assume that variable is already transformed, as it is added during
					//transformation
					transformedVariable = updatedVariable['action-favourite'] ? updatedVariable :
						transformDetailedVariableToDisplayFormat(updatedVariable, $scope.selectedItem.id);

					if (transformedVariable['action-favourite'].iconValue === 'star') {
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
		}
	]);

}());
