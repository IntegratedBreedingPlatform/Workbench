/*global angular*/
'use strict';

(function() {
	var app = angular.module('addScale', ['scales', 'dataTypes', 'variableState', 'utilities', 'categories', 'range', 'ngMessages']);

	app.controller('AddScaleController', ['$scope', '$location', '$window', 'dataTypesService', 'scalesService', 'variableStateService',
		'scaleFormService', 'serviceUtilities', 'formUtilities',
		function($scope, $location, $window, dataTypesService, scalesService, variableStateService, scaleFormService, serviceUtilities,
			formUtilities) {

			var ADD_VARIABLE_PATH = '/add/variable',
				SCALES_PATH = '/scales';

			$scope.scale = {};

			// Reset server errors
			$scope.serverErrors = {};

			$scope.showRangeWidget = false;
			$scope.showCategoriesWidget = false;

			dataTypesService.getDataTypes().then(function(types) {
				$scope.types = types;
			}, function(response) {
				$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
			});

			$scope.saveScale = function(e, scale) {
				e.preventDefault();

				if ($scope.asForm.$valid) {
					$scope.submitted = true;

					scalesService.addScale(scale).then(function(response) {
						scale.id = response.id;
						if (variableStateService.updateInProgress()) {
							variableStateService.setScale(scale.id, scale.name).finally(function() {
								$location.path(ADD_VARIABLE_PATH);
							});
						} else {
							$location.path(SCALES_PATH);
						}
					}, function(response) {
						$scope.asForm.$setUntouched();
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.submitted = false;
					});
				}
			};

			$scope.cancel = function(e) {
				var path = variableStateService.updateInProgress() ? ADD_VARIABLE_PATH : SCALES_PATH;

				e.preventDefault();
				formUtilities.cancelAddHandler($scope, !scaleFormService.formEmpty($scope.scale), path);
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
