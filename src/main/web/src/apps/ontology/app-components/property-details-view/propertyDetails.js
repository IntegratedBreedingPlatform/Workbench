/*global angular*/
'use strict';

(function() {
	var propertyDetailsModule = angular.module('propertyDetails', ['formFields', 'input', 'textArea', 'tagSelect', 'properties',
			'utilities', 'panel']),
		DELAY = 400;

	propertyDetailsModule.directive('omPropertyDetails', ['propertiesService', 'serviceUtilities', 'formUtilities', 'panelService',
		'$timeout',
		function(propertiesService, serviceUtilities, formUtilities, panelService, $timeout) {

			// Reset any errors we're showing the user
			function resetErrors($scope) {
		 		$scope.clientErrors = {};
		 	}

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.$watch('selectedProperty', function(property) {
						$scope.model = angular.copy(property);
						$scope.deletable = property && property.deletable || false;
						resetErrors($scope);
					});

					$scope.data = {
						classes: []
					};

					propertiesService.getClasses().then(function(classes) {
						$scope.data.classes = classes;
					}, serviceUtilities.genericAndRatherUselessErrorHandler);

					$scope.$watch('selectedItem', function(selected) {
						$scope.propertyId = selected && selected.id || null;
					}, true);

					$scope.editProperty = function(e) {
						e.preventDefault();
						resetErrors($scope);

						$scope.editing = true;
					};

					$scope.deleteProperty = function(e, id) {
						e.preventDefault();
						resetErrors($scope);

						formUtilities.confirmationHandler($scope, 'confirmDelete').then(function() {
							propertiesService.deleteProperty(id).then(function() {
								// Remove property on parent scope if we succeeded
								panelService.hidePanel();
								$scope.updateSelectedProperty();
							}, function() {
								$scope.clientErrors.failedToDelete = true;
							});
						});
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						resetErrors($scope);

						// The user hasn't changed anything
						if (angular.equals($scope.model, $scope.selectedProperty)) {
							$scope.editing = false;
						} else {
							formUtilities.confirmationHandler($scope, 'confirmCancel').then(function() {
								$scope.editing = false;
								$scope.model = angular.copy($scope.selectedProperty);
							});
						}
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();
						resetErrors($scope);

						if ($scope.pdForm.$valid) {
							$scope.submitted = true;
							$timeout(function() {
								if ($scope.submitted) {
									$scope.showThrobber = true;
								}
							}, DELAY);

							propertiesService.updateProperty(id, model).then(function() {

								// Update property on parent scope if we succeeded
								$scope.updateSelectedProperty(model);

								$scope.editing = false;
								$scope.submitted = false;
								$scope.showThrobber = false;
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
