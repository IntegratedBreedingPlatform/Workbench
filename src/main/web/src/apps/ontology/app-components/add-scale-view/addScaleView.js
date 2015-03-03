/*global angular*/
'use strict';

(function() {
	var app = angular.module('addScale', ['scales', 'dataTypes', 'variableState', 'utilities']);

	app.controller('AddScaleController', ['$scope', '$location', '$window', 'dataTypesService', 'scaleService', 'variableStateService',
		'serviceUtilities',
		function($scope, $location, $window, dataTypesService, scaleService, variableStateService, serviceUtilities) {

			$scope.scale = {
				categories: [{}]
			};

			$scope.showRangeWidget = false;
			$scope.showCategoriesWidget = false;

			// TODO Error handling
			dataTypesService.getDataTypes().then(function(types) {
				$scope.types = types;
			});

			$scope.saveScale = function(e, scale) {
				e.preventDefault();

				// TODO Error handling - only set the scale if it saved
				scaleService.saveScale(scale);

				if (variableStateService.updateInProgress()) {
					// FIXME Change to ID
					variableStateService.setScale(scale.name).then(function() {
						$window.history.back();
					}, serviceUtilities.genericAndRatherUselessErrorHandler);
				} else {
					// FIXME Go somewhere more useful
					$location.path('/scales');
				}
			};

			$scope.addCategory = function() {
				$scope.scale.categories.push({});
			};

			$scope.$watch('scale.type.name', function(newValue) {
				$scope.showRangeWidget = newValue === 'Numeric';
				$scope.showCategoriesWidget = newValue === 'Categorical';
			});
		}
	]);
}());
