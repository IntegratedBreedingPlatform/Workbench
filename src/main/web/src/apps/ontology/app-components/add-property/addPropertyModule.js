/*global angular*/
'use strict';

(function() {
	var app = angular.module('addProperty', ['addVariable']);

	app.controller('AddPropertyController', ['$scope', '$window', 'propertyService', 'variableService',
		function($scope, $window, propertyService, variableService) {
			this.classes = [];

			$scope.saveProperty = function(e, property) {
				e.preventDefault();

				// TODO Error handling - only set the property if it saved
				propertyService.saveProperty(property);

				// TODO Only do this if we're coming from the add variable screen
				variableService.setProperty(property);
				$window.history.back();
			};
		}]);

	app.service('propertyService', [function() {
		return {
			saveProperty: function(property) {
				// TODO Call actual save functionality
				console.log('Saving property');
				console.log(property);
			}
		};
	}]);

}());
