/*global angular*/
'use strict';

(function() {
	var app = angular.module('addScale', ['scales', 'dataTypes', 'variableState', 'utilities']);

	app.controller('AddScaleController', ['$scope', '$location', '$window', 'dataTypesService', 'scalesService', 'variableStateService',
		'serviceUtilities',
		function($scope, $location, $window, dataTypesService, scalesService, variableStateService, serviceUtilities) {

			$scope.scale = {
				categories: [{}]
			};

			$scope.data = {};

			$scope.showRangeWidget = false;
			$scope.showCategoriesWidget = false;

			// TODO Error handling
			dataTypesService.getDataTypes().then(function(types) {
				$scope.types = types;
			});

			$scope.saveScale = function(e, scale) {
				e.preventDefault();

				scalesService.addScale(scale).then(function(response) {
					scale.id = response.id;
					if (variableStateService.updateInProgress()) {
						variableStateService.setScale(scale.id).then(function() {
							$window.history.back();
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					} else {
						// FIXME Go somewhere more useful
						$location.path('/scales');
					}
				}, serviceUtilities.genericAndRatherUselessErrorHandler);
			};

			$scope.addCategory = function() {
				$scope.scale.categories.push({});
			};

			$scope.$watch('data.selectedType', function(newType) {
				if (newType) {
					$scope.scale.dataType = newType.id;

					$scope.showRangeWidget = newType.name === 'Numeric';
					$scope.showCategoriesWidget = newType.name === 'Categorical';
				}
			});
		}
	]);
}());
