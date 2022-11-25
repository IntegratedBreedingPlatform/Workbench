/*global angular*/
'use strict';

(function() {
	var scaleDetailsModule = angular.module('scaleDetails', ['input', 'textArea', 'select', 'scales', 'dataTypes', 'utilities',
		'categories', 'panel', 'range', 'expandCollapseHeading', 'errorList']),
		DELAY = 400,
		NUM_EDITABLE_FIELDS = 3;

	scaleDetailsModule.directive('omScaleDetails', ['scalesService', 'variablesService', 'serviceUtilities', 'formUtilities', 'panelService',
		'dataTypesService', '$timeout', 'collectionUtilities',
		function(scalesService, variablesService, serviceUtilities, formUtilities, panelService, dataTypesService, $timeout, collectionUtilities) {

			var LISTS_NOT_LOADED_TRANSLATION = 'validation.scale.someListsNotLoaded';

			// Reset any errors we're showing the user
			function resetErrors($scope) {
				$scope.clientErrors = {};
				$scope.serverErrors = {};
			}

			return {
				controller: ['$scope', function($scope) {
					$scope.editing = false;
					$scope.showRangeWidget = false;
					$scope.showCategoriesWidget = false;

					$scope.$watch('selectedScale', function(scale) {
						// Should always open in read-only view
						$scope.editing = false;
						resetErrors($scope);

						// If a confirmation handler was in effect, get rid of it
						if ($scope.deny) {
							$scope.deny();
						}
						$scope.model = angular.copy(scale);
						$scope.scaleName = $scope.model ? $scope.model.name : '';
						$scope.deletable = scale && scale.metadata && scale.metadata.deletable || false;
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model &&
							$scope.model.metadata.editableFields.length < NUM_EDITABLE_FIELDS;
						$scope.showNoneditableCategoriesAlert =
							$scope.editing
							&& $scope.model
							&& $scope.model.validValues
							&& $scope.model.validValues.categories
							&& $scope.model.validValues.categories.some(function(x) {
								return !x.editable;
							});
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.scaleId = selected && selected.id || null;
					}, true);

					$scope.$watch('model.dataType', function(newType) {
						if (newType) {
							$scope.showRangeWidget = newType.name === 'Numeric';
							$scope.showCategoriesWidget = newType.name === 'Categorical';

							if ($scope.showCategoriesWidget) {
								// Initialise the categories so that the input fields for categories are shown
								if ($scope.model.validValues && $scope.model.validValues.categories &&
									$scope.model.validValues.categories.length < 1) {
									$scope.model.validValues.categories = [{}];
								}
							}
						}
					});

					function resetSubmissionState() {
						$scope.submitted = false;
						$scope.showThrobber = false;
					}

					$scope.formatListForDisplay = collectionUtilities.formatListForDisplay;

					$scope.editScale = function(e) {
						e.preventDefault();
						resetErrors($scope);

						dataTypesService.getNonSystemDataTypes().then(function(types) {
							$scope.types = types;
						}, function(response) {
							$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							$scope.serverErrors.someListsNotLoaded = [LISTS_NOT_LOADED_TRANSLATION];
						});

						$scope.editing = true;
					};

					$scope.deleteScale = function(e, id) {
						e.preventDefault();
						resetErrors($scope);

						formUtilities.confirmationHandler($scope, 'confirmDelete').then(function() {
							scalesService.deleteScale(id).then(function() {
								// Remove scale on parent scope if we succeeded
								panelService.hidePanel();
								$scope.updateSelectedScale();
							}, function() {
								$scope.clientErrors.failedToDelete = true;
							});
						});
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						resetErrors($scope);

						// The user hasn't changed anything
						if (angular.equals($scope.model, $scope.selectedScale)) {
							$scope.editing = false;
						} else {
							formUtilities.confirmationHandler($scope, 'confirmCancel').then(function() {
								$scope.editing = false;
								$scope.model = angular.copy($scope.selectedScale);
							});
						}
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();
						resetErrors($scope);

						if ($scope.sdForm.$valid) {
							$scope.submitted = true;
							$timeout(function() {
								if ($scope.submitted) {
									$scope.showThrobber = true;
								}
							}, DELAY);

							scalesService.updateScale(id, model).then(function() {
								// Check if scale details is not opened from Variables Panel
								if (!$scope.selectedVariable) {
									// Update scale on parent scope if we succeeded
									$scope.updateSelectedScale(model);
								}

								$scope.editing = false;
								resetSubmissionState();
								$scope.scaleName = model.name;

								if (model.metadata.usage
										&& model.metadata.usage.variables
										&& model.metadata.usage.variables.length > 0) {

									var variableIds = model.metadata.usage.variables
										.map(function(variable) {
											return parseInt(variable.id);
										});
									variablesService.deleteVariablesFromCache(variableIds);
								}

							}, function(response) {
								resetSubmissionState();
								$scope.sdForm.$setUntouched();
								$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
							});
						}
					};

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'sdForm');
				}],
				restrict: 'E',
				templateUrl: 'static/views/ontology/scaleDetails.html'
			};
		}
	]);
})();
