/*global angular*/
'use strict';

(function() {
	var app = angular.module('addScale', ['scales', 'dataTypes', 'variableState', 'utilities', 'categories', 'range', 'ngMessages']);

	app.controller('AddScaleController', ['$scope', '$location', '$window', 'dataTypesService', 'scalesService', 'variableStateService',
		'scaleFormService', 'serviceUtilities', 'formUtilities',
		function($scope, $location, $window, dataTypesService, scalesService, variableStateService, scaleFormService, serviceUtilities,
			formUtilities) {

			$scope.scale = {};

			$scope.showRangeWidget = false;
			$scope.showCategoriesWidget = false;

			dataTypesService.getDataTypes().then(function(types) {
				$scope.types = types;
			}, serviceUtilities.genericAndRatherUselessErrorHandler);

			$scope.saveScale = function(e, scale) {
				e.preventDefault();

				if ($scope.asForm.$valid) {
					$scope.submitted = true;

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

			$scope.cancel = function(e) {
				e.preventDefault();
				formUtilities.cancelAddHandler($scope, !scaleFormService.formEmpty($scope.scale));
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

	app.factory('scaleFormService', [function() {
		return {
			formEmpty: function(model) {
				// Don't bother checking for valid values, because for there to be any
				// there must be a data type, in which case the form isn't empty.. :)
				return !!!model.name &&
					!!!model.description &&
					angular.isUndefined(model.dataType);
			}
		};
	}]);
}());
