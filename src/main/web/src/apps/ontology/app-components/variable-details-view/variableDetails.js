/*global angular*/
'use strict';

(function() {
	var variableDetailsModule = angular.module('variableDetails', ['formFields', 'properties', 'utilities', 'variables']);

	variableDetailsModule.directive('omVariableDetails', ['variablesService', function(variablesService) {

		return {
			controller: function($scope, propertiesService, serviceUtilities) {

				$scope.editing = false;

				$scope.$watch('selectedVariable', function(variable) {
					$scope.model = angular.copy(variable);
				});

				$scope.data = {
					properties: []
				};

				propertiesService.getProperties().then(function(properties) {
					$scope.data.properties = properties;
				}, serviceUtilities.genericAndRatherUselessErrorHandler);

				$scope.$watch('selectedItem', function(selected) {
					$scope.variableId = selected && selected.id || null;
				});

				// Hide the alias if the name is still editable
				$scope.hideAlias = function() {
					return $scope.model && $scope.model.editableFields && $scope.model.editableFields.indexOf('name') !== -1;
				};

				$scope.editVariable = function(e) {
					e.preventDefault();
					$scope.editing = true;
				};

				$scope.cancel = function(e) {
					e.preventDefault();
					$scope.editing = false;
					$scope.model = $scope.selectedVariable;
				};

				$scope.saveChanges = function(e, id, model) {
					e.preventDefault();

					variablesService.updateVariable(id, model).then(function() {

						// Update variable on parent scope if we succeeded
						$scope.updateSelectedVariable(model);

						$scope.editing = false;
					}, function(error) {

						// TODO Error handling
						console.log(error);
					});
				};
			},
			restrict: 'E',
			templateUrl: 'static/views/ontology/variableDetails.html'
		};
	}]);
})();
