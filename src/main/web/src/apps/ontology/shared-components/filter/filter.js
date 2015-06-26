/*global angular*/
'use strict';

(function() {
	var filterModule = angular.module('filter', ['panel', 'variableTypes', 'dataTypes', 'utilities', 'multiSelect']);

	filterModule.directive('omFilter', ['panelService', 'variableTypesService', 'serviceUtilities', 'dataTypesService',
		function(panelService, variableTypesService, serviceUtilities, dataTypesService)  {
		return {
			controller: function($scope) {
				$scope.smallPanelName = 'filters';
				$scope.data = {
					types: [],
					scaleTypes: [{
						id: 0,
						name: '...'
					}]
				};

				$scope.addNewFilter = function() {
					panelService.showPanel($scope.smallPanelName);
				};

				$scope.isFilterActive = function() {
					return ($scope.filterOptions && $scope.filterOptions.variableTypes &&
						$scope.filterOptions.variableTypes.length !== 0) || ($scope.filterOptions && $scope.filterOptions.scaleType &&
						$scope.filterOptions.scaleType.name !== '...');
				};

				variableTypesService.getTypes().then(function(types) {
					$scope.data.types = types;
				}, function(response) {
					$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
					$scope.someListsNotLoaded = true;
				});

				dataTypesService.getDataTypes().then(function(types) {
					$scope.data.scaleTypes = $scope.data.scaleTypes.concat(types);
				}, function(response) {
					$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
					$scope.someListsNotLoaded = true;
				});
			},
			restrict: 'E',
			scope: {
				filterOptions: '=omFilterOptions'
			},
			templateUrl: 'static/views/ontology/filter.html'
		};
	}]);

}());
