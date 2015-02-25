/*global angular*/
'use strict';

(function() {
	var app = angular.module('propertiesView', ['properties', 'list', 'panel']);

	app.controller('PropertiesController', ['$scope', 'propertiesService', function($scope, propertiesService) {
		var ctrl = this;
		this.properties = [];

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

		$scope.panelOpen = {show: false};

		$scope.showPropertyDetails = function() {

			propertiesService.getProperty($scope.selectedItem.id).then(function(property) {
				$scope.selectedProperty = property;
			});

			$scope.panelOpen.show = true;
		};

		$scope.selectedItem = {id: null};
		$scope.selectedProperty = null;
	}]);
}());
