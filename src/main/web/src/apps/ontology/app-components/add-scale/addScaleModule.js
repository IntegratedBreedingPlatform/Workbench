/*global angular*/
'use strict';

(function() {
	var app = angular.module('addScale', ['scales', 'dataTypes']);

	app.controller('AddScaleController', ['$scope', '$location', 'dataTypesService', 'scaleService',
		function($scope, $location, dataTypesService, scaleService) {

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

				// TODO Error handling
				scaleService.saveScale(scale);

				$location.path('/scales');
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
