/*global angular*/
'use strict';

(function() {
	var propertyDetailsModule = angular.module('propertyDetails', ['formFields', 'properties', 'utilities']);

	propertyDetailsModule.directive('omPropertyDetails', ['propertiesService', 'serviceUtilities',
		function(propertiesService, serviceUtilities) {

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.$watch('selectedProperty', function(property) {
						$scope.model = angular.copy(property);
					});

					$scope.data = {
						classes: []
					};

					propertiesService.getClasses().then(function(classes) {
						$scope.data.classes = classes;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);

					$scope.$watch('selectedItem', function(selected) {
						$scope.propertyId = selected && selected.id || null;
					});

					$scope.editProperty = function(e) {
						e.preventDefault();
						$scope.editing = true;
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						$scope.editing = false;
						$scope.model = angular.copy($scope.selectedProperty);
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();

						propertiesService.updateProperty(id, model).then(function() {

							// Update property on parent scope if we succeeded
							$scope.updateSelectedProperty(model);

							$scope.editing = false;
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					};
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/propertyDetails.html'
			};
		}
	]);
})();
