/*global angular*/
'use strict';

(function() {
	var methodDetailsModule = angular.module('methodDetails', ['input', 'textArea', 'methods', 'utilities', 'panel',
		'expandCollapseHeading', 'errorList']),
		DELAY = 400,
		NUM_EDITABLE_FIELDS = 2;

	methodDetailsModule.directive('omMethodDetails', ['methodsService', 'serviceUtilities', 'formUtilities', 'panelService', '$timeout',
		'collectionUtilities',
		function(methodsService, serviceUtilities, formUtilities, panelService, $timeout, collectionUtilities) {

			// Reset any errors we're showing the user
			function resetErrors($scope) {
				$scope.serverErrors = {};
				$scope.clientErrors = {};
			}

			return {
				controller: ['$scope', function($scope) {
					$scope.editing = false;

					$scope.$watch('selectedMethod', function(method) {
						// Should always open in read-only view
						$scope.editing = false;
						resetErrors($scope);

						// If a confirmation handler was in effect, get rid of it
						if ($scope.deny) {
							$scope.deny();
						}
						$scope.model = angular.copy(method);
						$scope.methodName = $scope.model ? $scope.model.name : '';
						$scope.deletable = method && method.metadata && method.metadata.deletable || false;
						$scope.isSystemMethod = Boolean(method && method.metadata && method.metadata.usage && method.metadata.usage.systemTerm);
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model &&
							$scope.model.metadata.editableFields.length < NUM_EDITABLE_FIELDS;
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.methodId = selected && selected.id || null;
					}, true);

					function resetSubmissionState() {
						$scope.submitted = false;
						$scope.showThrobber = false;
					}

					$scope.formatListForDisplay = collectionUtilities.formatListForDisplay;

					$scope.editMethod = function(e) {
						e.preventDefault();
						resetErrors($scope);

						$scope.editing = true;
					};

					$scope.deleteMethod = function(e, id) {
						e.preventDefault();
						resetErrors($scope);

						formUtilities.confirmationHandler($scope, 'confirmDelete').then(function() {
							methodsService.deleteMethod(id).then(function() {
								// Remove method on parent scope if we succeeded
								panelService.hidePanel();
								$scope.updateSelectedMethod();
							}, function() {
								$scope.clientErrors.failedToDelete = true;
							});
						});
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						resetErrors($scope);

						// The user hasn't changed anything
						if (angular.equals($scope.model, $scope.selectedMethod)) {
							$scope.editing = false;
						} else {
							formUtilities.confirmationHandler($scope, 'confirmCancel').then(function() {
								$scope.editing = false;
								$scope.model = angular.copy($scope.selectedMethod);
							});
						}
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();
						resetErrors($scope);

						if ($scope.mdForm.$valid) {
							$scope.submitted = true;
							$timeout(function() {
								if ($scope.submitted) {
									$scope.showThrobber = true;
								}
							}, DELAY);

							methodsService.updateMethod(id, model).then(function() {
								// Check if method details is not opened from Variables Panel
								if (!$scope.selectedVariable) {
									// Update method on parent scope if we succeeded
									$scope.updateSelectedMethod(model);
								}

								$scope.editing = false;
								resetSubmissionState();
								$scope.methodName = model.name;
							}, function(response) {
								resetSubmissionState();
								$scope.mdForm.$setUntouched();
								$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
							});
						}
					};

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'mdForm');
				}],
				restrict: 'E',
				templateUrl: 'static/views/ontology/methodDetails.html'
			};
		}
	]);
})();
