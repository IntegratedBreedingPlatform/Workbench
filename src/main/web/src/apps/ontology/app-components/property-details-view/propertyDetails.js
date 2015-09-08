/*global angular*/
'use strict';

(function() {
	var propertyDetailsModule = angular.module('propertyDetails', ['input', 'textArea', 'tagSelect', 'properties', 'utilities', 'panel',
		'expandCollapseHeading']),
		DELAY = 400,
		NUM_EDITABLE_FIELDS = 4;

	propertyDetailsModule.directive('omPropertyDetails', ['propertiesService', 'serviceUtilities', 'formUtilities', 'panelService',
		'$timeout', 'collectionUtilities',
		function(propertiesService, serviceUtilities, formUtilities, panelService, $timeout, collectionUtilities) {

			// Reset any errors we're showing the user
			function resetErrors($scope) {
				$scope.clientErrors = {};
				$scope.serverErrors = {};
			}

			function loadClasses($scope) {
				propertiesService.getClasses().then(function(classes) {
					$scope.data.classes = classes;
					$scope.someListsNotLoaded = false;
				}, function(response) {
					$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
					$scope.someListsNotLoaded = true;
				});
			}

			return {
				controller: function($scope) {
					$scope.editing = false;
					$scope.data = {
						classes: []
					};

					$scope.$watch('selectedProperty', function(property) {
						// Should always open in read-only view
						$scope.editing = false;
						loadClasses($scope);
						resetErrors($scope);

						// If a confirmation handler was in effect, get rid of it
						if ($scope.deny) {
							$scope.deny();
						}
						$scope.model = angular.copy(property);
						$scope.propertyName = $scope.model ? $scope.model.name : '';
						$scope.deletable = property && property.metadata && property.metadata.deletable || false;
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model &&
							$scope.model.metadata.editableFields.length < NUM_EDITABLE_FIELDS;
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.propertyId = selected && selected.id || null;
					}, true);

					function resetSubmissionState() {
						$scope.submitted = false;
						$scope.showThrobber = false;
					}

					$scope.editProperty = function(e) {
						e.preventDefault();
						resetErrors($scope);

						$scope.editing = true;
					};

					$scope.formatListForDisplay = collectionUtilities.formatListForDisplay;

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
								resetSubmissionState();
								$scope.propertyName = model.name;
							}, function(response) {
								resetSubmissionState();
								$scope.pdForm.$setUntouched();
								$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
							});
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
