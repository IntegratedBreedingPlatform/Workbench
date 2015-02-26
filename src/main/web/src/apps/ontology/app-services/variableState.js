/*global angular*/
'use strict';

(function() {
	var app = angular.module('variableState', ['properties', 'methods', 'scales']);

	app.service('variableStateService', ['$q', 'propertiesService', 'methodsService', 'scalesService',
		function($q, propertiesService, methodsService, scalesService) {

			var variable = {},
				scopeData = {},
				editInProgress = false;

			/*
			Selects an item by name from the specified list on scopeData and sets it on the specified property on the variable.
			We do this so that this data will work correctly in Angular selects as the options and selected option (the selected item
			must be set by reference, not value).

			FIXME Change to match on id, not name

			@method setSelectedItem
			@param listOfItems the list of items to be stored on scopeData
			@param itemNameToSearchFor the name of the property to find and select in the listOfItems
			@param listName the name of the property which the items should be stored on the scopeData
			@param propertyName the name of the property which the selected item should be stored on the variable
			*/
			function setSelectedItem(listOfItems, itemNameToSearchFor, listName, propertyName) {

				var index = -1;

				listOfItems.some(function(item, idx) {
					// FIXME - Change to ID
					if (item.name === itemNameToSearchFor) {
						index = idx;
						return true;
					}
				});

				scopeData[listName] = listOfItems;
				variable[propertyName] = index === -1 ? null : scopeData[listName][index];
			}

			return {

				storeVariableState: function(_variable, _scopeData) {
					variable = _variable || {};
					scopeData = _scopeData || {};
					editInProgress = true;
				},

				getVariableState: function() {
					return {
						variable: variable,
						scopeData: scopeData
					};
				},

				updateInProgress: function() {
					return editInProgress;
				},

				reset: function() {
					variable = {};
					scopeData = {};
					editInProgress = false;
				},

				// FIXME - Change to ID
				/*
				This method will update the stored list of properties in scopeData, and find the property with the specified propertyName
				and select that property. The selected property will be stored on the variable.

				@param propertyName the name of the property to select from the updated list
				@returns a promise that will resolve when the properties and selected property have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setProperty: function(propertyName) {
					return $q(function(resolve, reject) {
						propertiesService.getProperties().then(function(properties) {
							setSelectedItem(properties, propertyName, 'properties', 'property');
							resolve();
						}, function(error) {
							reject(error);
						});
					});
				},

				// FIXME - Change to ID
				/*
				This method will update the stored list of methods in scopeData, and find the method with the specified methodName
				and select that method. The selected method will be stored on the variable.

				@param methodName the name of the method to select from the updated list
				@returns a promise that will resolve when the methods and selected method have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setMethod: function(methodName) {
					return $q(function(resolve, reject) {
						methodsService.getMethods().then(function(methods) {
							setSelectedItem(methods, methodName, 'methods', 'method');
							resolve();
						}, function(error) {
							reject(error);
						});
					});
				},

				// FIXME - Change to ID
				/*
				This scale will update the stored list of scales in scopeData, and find the scale with the specified scaleName
				and select that scale. The selected scale will be stored on the variable.

				@param scaleName the name of the scale to select from the updated list
				@returns a promise that will resolve when the scales and selected scale have been updated, and will reject
					with one parameter error containing the error if one occurs.
				*/
				setScale: function(scaleName) {
					return $q(function(resolve, reject) {
						scalesService.getScales().then(function(scales) {
							setSelectedItem(scales, scaleName, 'scales', 'scale');
							resolve();
						}, function(error) {
							reject(error);
						});
					});
				}
			};
		}
	]);
}());
