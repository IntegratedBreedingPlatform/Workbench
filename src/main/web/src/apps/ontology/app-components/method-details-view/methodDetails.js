/*global angular*/
'use strict';

(function() {
	var methodDetailsModule = angular.module('methodDetails', ['formFields', 'methods', 'utilities', 'panel']);

	methodDetailsModule.directive('omMethodDetails', ['methodsService', 'serviceUtilities', 'panelService',
		function(methodsService, serviceUtilities, panelService) {

			return {
				controller: function($scope) {
					$scope.editing = false;

					$scope.$watch('selectedMethod', function(method) {
						$scope.model = angular.copy(method);
					});

					$scope.$watch('selectedItem', function(selected) {
						$scope.methodId = selected && selected.id || null;
					});

					$scope.editMethod = function(e) {
						e.preventDefault();
						$scope.editing = true;
					};

					$scope.deleteMethod = function(e, id) {
						e.preventDefault();

						methodsService.deleteMethod(id).then(function() {
							// Remove method on parent scope if we succeeded
							panelService.hidePanel();
							$scope.updateSelectedMethod();
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					};

					$scope.cancel = function(e) {
						e.preventDefault();
						$scope.editing = false;
						$scope.model = angular.copy($scope.selectedMethod);
					};

					$scope.saveChanges = function(e, id, model) {
						e.preventDefault();

						methodsService.updateMethod(id, model).then(function() {

							// Update method on parent scope if we succeeded
							$scope.updateSelectedMethod(model);

							$scope.editing = false;
						}, serviceUtilities.genericAndRatherUselessErrorHandler);
					};
				},
				restrict: 'E',
				templateUrl: 'static/views/ontology/methodDetails.html'
			};
		}
	]);
})();
