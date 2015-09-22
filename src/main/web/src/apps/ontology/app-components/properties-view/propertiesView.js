/*global angular*/
'use strict';

(function() {
	var app = angular.module('propertiesView', ['properties', 'list', 'panel', 'propertyDetails', 'utilities', 'search']),
		DELAY = 400;

	function transformPropertyToDisplayFormat(property, id) {
		return {
			id: property.id || id,
			name: property.name,
			description: property.description || '',
			classes: property.classes.join(', ')
		};
	}

	function transformToDisplayFormat(properties) {
		return properties.map(transformPropertyToDisplayFormat);
	}

	app.controller('PropertiesController', ['$scope', 'propertiesService', 'panelService', '$timeout', 'collectionUtilities',
		function($scope, propertiesService, panelService, $timeout, collectionUtilities) {
			var ctrl = this;

			ctrl.properties = [];
			ctrl.showThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'description', 'classes'];
			ctrl.problemGettingList = false;

			$scope.filterByProperties = ctrl.colHeaders;
			$scope.panelName = 'properties';

			$timeout(function() {
				ctrl.showThrobber = true;
			}, DELAY);

			propertiesService.getProperties().then(function(properties) {
				ctrl.properties = transformToDisplayFormat(properties);
				if (ctrl.properties.length === 0) {
					ctrl.showNoItemsMessage = true;
				}
			}, function() {
				ctrl.problemGettingList = true;
			}).finally (function() {
				ctrl.showThrobberWrapper = false;
			});

			$scope.showPropertyDetails = function() {

				// Ensure the previously selected property doesn't show in the panel before we've retrieved the new one
				$scope.selectedProperty = null;

				propertiesService.getProperty($scope.selectedItem.id).then(function(property) {
					$scope.selectedProperty = property;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.updateSelectedProperty = function(updatedProperty) {

				var selectedIndex = -1,
					transformedProperty = updatedProperty && transformPropertyToDisplayFormat(updatedProperty, $scope.selectedItem.id);

				ctrl.properties.some(function(property, index) {
					if (property.id === $scope.selectedItem.id) {
						selectedIndex = index;
						return true;
					}
				});

				// Not much we can really do if we don't find it in the list. Just don't update.
				if (selectedIndex !== -1) {
					if (transformedProperty) {
						ctrl.properties[selectedIndex] = transformedProperty;
						ctrl.properties = collectionUtilities.sortByName(ctrl.properties);
						$scope.selectedProperty = updatedProperty;
					} else {
						ctrl.properties.splice(selectedIndex, 1);
					}
				}
			};

			// An object only containing the selected item's id. This format is required for passing to the list directive.
			$scope.selectedItem = {id: null};
			// Contains the entire selected property object once it has been updated.
			$scope.selectedProperty = null;
		}
	]);
}());
