/*global angular*/
'use strict';

(function() {
	var app = angular.module('addScale', ['scales', 'dataTypes', 'variableState', 'utilities', 'categories', 'range', 'ngMessages']);

	app.controller('AddScaleController', ['$scope', '$location', '$window', 'dataTypesService', 'scalesService', 'variableStateService',
		'serviceUtilities', 'formUtilities',
		function($scope, $location, $window, dataTypesService, scalesService, variableStateService, serviceUtilities, formUtilities) {

			$scope.scale = {};

			$scope.showRangeWidget = false;
			$scope.showCategoriesWidget = false;

			dataTypesService.getDataTypes().then(function(types) {
				$scope.types = types;
			}, serviceUtilities.genericAndRatherUselessErrorHandler);

			$scope.saveScale = function(e, scale) {
				e.preventDefault();

				if ($scope.asForm.$valid) {
					scalesService.addScale(scale).then(function(response) {
						scale.id = response.id;
						if (variableStateService.updateInProgress()) {
							variableStateService.setScale(scale.id, scale.name).finally(function() {
								$window.history.back();
							});
						} else {
							// FIXME Go somewhere more useful
							$location.path('/scales');
						}
					}, serviceUtilities.genericAndRatherUselessErrorHandler);
				}
			};

			$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'asForm');

			$scope.$watch('scale.dataType', function(newType) {
				if (newType) {
					$scope.showRangeWidget = newType.name === 'Numeric';
					$scope.showCategoriesWidget = newType.name === 'Categorical';
				}
			});
		}
	]);
}());
