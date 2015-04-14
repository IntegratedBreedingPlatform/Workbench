/*global angular*/
'use strict';

(function() {
	var app = angular.module('propertiesView', ['properties', 'list', 'panel', 'propertyDetails']),
		DELAY = 400;

	function transformPropertyToDisplayFormat(property, id) {
		return {
			id: property.id || id,
			name: property.name,
			classes: property.classes.join(', ')
		};
	}

	function transformToDisplayFormat(properties) {
		return properties.map(transformPropertyToDisplayFormat);
	}

	app.controller('PropertiesController', ['$scope', 'propertiesService', 'panelService', '$timeout',
		function($scope, propertiesService, panelService, $timeout) {
			var ctrl = this;
			this.properties = [];

			$timeout(function() {
				ctrl.showThrobber = true;
			}, DELAY);

			$scope.panelName = 'properties';

			ctrl.colHeaders = ['name', 'classes'];

			propertiesService.getProperties().then(function(properties) {
				ctrl.properties = transformToDisplayFormat(properties);
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
					} else {
						ctrl.properties.splice(selectedIndex, 1);
					}
				}
			};

			$scope.selectedItem = {id: null};
			$scope.selectedProperty = null;
		}
	]);
}());
