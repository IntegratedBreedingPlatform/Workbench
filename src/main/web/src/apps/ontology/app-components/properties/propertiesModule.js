/*global angular*/
'use strict';

(function() {
	var app = angular.module('properties', ['list', 'panel']);

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

	app.service('propertiesService', ['$http', '$q', function($http, $q) {
		function successHandler(response) {
			return response.data;
		}

		function failureHandler(response) {
			var errorMessage = 'An unknown error occurred.';

			if (!angular.isObject(response.data)) {
				if (response.status === 400) {
					errorMessage = 'Request was malformed.';
				}
				return $q.reject(errorMessage);
			}
		}

		return {
			getProperty: function(/*id*/) {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties/:id');
				return request.then(successHandler, failureHandler);
			},

			getProperties: function() {
				var request = $http.get('http://private-905fc7-ontologymanagement.apiary-mock.com/properties');

				return request.then(successHandler, failureHandler);
			}
		};
	}]);
}());
