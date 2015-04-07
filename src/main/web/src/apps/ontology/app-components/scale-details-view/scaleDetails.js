/*global angular*/
'use strict';

(function() {
	var scaleDetailsModule = angular.module('scaleDetails', ['formFields', 'input', 'textArea', 'select', 'scales', 'dataTypes',
		'utilities', 'categories', 'panel']);

	scaleDetailsModule.directive('omScaleDetails', ['scalesService', 'serviceUtilities', 'panelService', 'dataTypesService',
		function(scalesService, serviceUtilities, panelService, dataTypesService) {

		return {
			controller: function($scope) {
				$scope.editing = false;
				$scope.showRangeWidget = false;
				$scope.showCategoriesWidget = false;

				$scope.$watch('selectedScale', function(scale) {
					$scope.model = angular.copy(scale);
					$scope.deletable = scale && scale.deletable || false;
				});

				$scope.$watch('selectedItem', function(selected) {
					$scope.scaleId = selected && selected.id || null;
				});

				$scope.$watch('model.dataType', function(newType) {
					if (newType) {
						$scope.showRangeWidget = newType.name === 'Numeric';
						$scope.showCategoriesWidget = newType.name === 'Categorical';
					}
				});

				$scope.editScale = function(e) {
					e.preventDefault();

					dataTypesService.getDataTypes().then(function(types) {
						$scope.types = types;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);

					$scope.editing = true;
				};

				$scope.deleteScale = function(e, id) {
					e.preventDefault();

					scalesService.deleteScale(id).then(function() {
						// Remove scale on parent scope if we succeeded
						panelService.hidePanel();
						$scope.updateSelectedScale();
					}, serviceUtilities.genericAndRatherUselessErrorHandler);
				};

				$scope.cancel = function(e) {
					e.preventDefault();
					$scope.editing = false;
					$scope.model = angular.copy($scope.selectedScale || {});
				};

				$scope.saveChanges = function(e, id, model) {
					e.preventDefault();

					if ($scope.sdForm.$valid) {
						scalesService.updateScale(id, model).then(function() {

							// Update scale on parent scope if we succeeded
							$scope.updateSelectedScale(model);

							$scope.editing = false;
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					}
				};
			},
			restrict: 'E',
			templateUrl: 'static/views/ontology/scaleDetails.html'
		};
	}]);
})();
