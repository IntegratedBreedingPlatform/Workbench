/*global angular*/
'use strict';

(function() {
	var propertyDetailsModule = angular.module('propertyDetails', ['formFields', 'input', 'textArea', 'properties', 'utilities', 'panel']);

	propertyDetailsModule.directive('omPropertyDetails', ['propertiesService', 'serviceUtilities', 'formUtilities', 'panelService',
		function(propertiesService, serviceUtilities, formUtilities, panelService) {

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.$watch('selectedProperty', function(property) {
						$scope.model = angular.copy(property);
						$scope.deletable = property && property.deletable || false;
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

					$scope.deleteProperty = function(e, id) {
						e.preventDefault();

						propertiesService.deleteProperty(id).then(function() {
							// Remove property on parent scope if we succeeded
							panelService.hidePanel();
							$scope.updateSelectedProperty();
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						$scope.editing = false;
						$scope.model = angular.copy($scope.selectedProperty);
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();

						if ($scope.pdForm.$valid) {
							propertiesService.updateProperty(id, model).then(function() {

								// Update property on parent scope if we succeeded
								$scope.updateSelectedProperty(model);

								$scope.editing = false;
							}, serviceUtilities.genericAndRatherUselessErrorHandler);
						}
					};

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'pdForm');
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/propertyDetails.html'
			};
		}
	]);
})();
