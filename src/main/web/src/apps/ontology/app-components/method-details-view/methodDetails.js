/*global angular*/
'use strict';

(function() {
	var methodDetailsModule = angular.module('methodDetails', ['formFields', 'input', 'textArea', 'methods', 'utilities', 'panel']);

	methodDetailsModule.directive('omMethodDetails', ['methodsService', 'serviceUtilities', 'formUtilities', 'panelService',
		function(methodsService, serviceUtilities, formUtilities, panelService) {

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.serverErrors = {};

					$scope.$watch('selectedMethod', function(method) {
						$scope.model = angular.copy(method);
						$scope.deletable = method && method.deletable || false;
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.methodId = selected && selected.id || null;
					}, true);

					$scope.editMethod = function(e) {
						e.preventDefault();
						$scope.editing = true;

						// Reset server errors
						$scope.serverErrors = {};
					};

					$scope.deleteMethod = function(e, id) {
						e.preventDefault();

						methodsService.deleteMethod(id).then(function() {
							// Remove method on parent scope if we succeeded
							panelService.hidePanel();
							$scope.updateSelectedMethod();
						}, function(response) {
							$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						});
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						$scope.editing = false;
						$scope.model = angular.copy($scope.selectedMethod);

						// Reset server errors
						$scope.serverErrors = {};
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();

						// Reset server errors
						$scope.serverErrors = {};

						if ($scope.mdForm.$valid) {
							methodsService.updateMethod(id, model).then(function() {

								// Update method on parent scope if we succeeded
								$scope.updateSelectedMethod(model);

								$scope.editing = false;
							}, function(response) {
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
