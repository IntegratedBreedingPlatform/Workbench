/*global angular*/
'use strict';

(function() {
	var app = angular.module('propertiesView', ['properties', 'list', 'panel']);

	app.controller('PropertiesController', ['$scope', 'propertiesService', 'panelService',
		function($scope, propertiesService, panelService) {
			var ctrl = this;
			this.properties = [];

			$scope.panelName = 'properties';

			ctrl.colHeaders = ['Name', 'Classes'];

			propertiesService.getProperties().then(function(properties) {
				ctrl.properties = properties.map(function(item) {
					return {
						id: item.id,
						Name: item.name,
						Classes: item.classes.join(', ')
					};
				});
			});

			$scope.showPropertyDetails = function() {

				propertiesService.getProperty($scope.selectedItem.id).then(function(property) {
					$scope.selectedProperty = property;
				});

				panelService.visible = {show: $scope.panelName};
			};

			$scope.selectedItem = {id: null};
			$scope.selectedProperty = null;
		}
	]);
}());
