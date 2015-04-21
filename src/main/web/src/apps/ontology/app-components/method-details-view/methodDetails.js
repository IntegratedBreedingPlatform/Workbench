/*global angular*/
'use strict';

(function() {
	var methodDetailsModule = angular.module('methodDetails', ['formFields', 'input', 'textArea', 'methods', 'utilities', 'panel']),
		DELAY = 400,
		NUM_EDITABLE_FIELDS = 2;

	methodDetailsModule.directive('omMethodDetails', ['methodsService', 'serviceUtilities', 'formUtilities', 'panelService', '$timeout',
		function(methodsService, serviceUtilities, formUtilities, panelService, $timeout) {

			// Reset any errors we're showing the user
			function resetErrors($scope) {
				$scope.serverErrors = {};
		 		$scope.clientErrors = {};
		 	}

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.$watch('selectedMethod', function(method) {
						$scope.model = angular.copy(method);
						$scope.deletable = method && method.deletable || false;
						// Should always open in read-only view
						$scope.editing = false;
						resetErrors($scope);
					});

					$scope.$watch('editing', function() {
						$scope.showNoneditableFieldsAlert = $scope.editing && $scope.model.editableFields.length < NUM_EDITABLE_FIELDS;
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.methodId = selected && selected.id || null;
					}, true);

					function resetSubmissionState() {
						$scope.submitted = false;
						$scope.showThrobber = false;
					}

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
								// Update method on parent scope if we succeeded
								$scope.updateSelectedMethod(model);

								$scope.editing = false;
								resetSubmissionState();
							}, function(response) {
								resetSubmissionState();
								$scope.mdForm.$setUntouched();
								$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
							});
						}
					};

					$scope.formGroupClass = formUtilities.formGroupClassGenerator($scope, 'mdForm');
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/methodDetails.html'
			};
		}
	]);
})();
