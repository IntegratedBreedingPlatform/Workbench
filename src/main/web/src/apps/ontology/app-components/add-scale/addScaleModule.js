/*global angular*/
'use strict';

(function() {
	var app = angular.module('addScale', ['scales', 'dataTypes']);

	app.controller('AddScaleController', ['$scope', 'dataTypesService', 'scaleService', function($scope, dataTypesService, scaleService) {

		$scope.types = [];
		$scope.scale = {
			categories: [{}]
		};

		$scope.showRangeWidget = false;
		$scope.showCategoriesWidget = false;

		// TODO Error handling
		dataTypesService.getDataTypes().then(function(types) {
			$scope.types = types;
		});

		// TODO Error handling
		$scope.saveScale = function(e, scale) {
			e.preventDefault();
			scaleService.saveScale(scale);
		};

		$scope.addCategory = function() {
			$scope.scale.categories.push({});
		};

		$scope.$watch('scale.type.name', function(newValue) {
			$scope.showRangeWidget = newValue === 'Numeric';
			$scope.showCategoriesWidget = newValue === 'Categorical';
		});

	}]);

}());
