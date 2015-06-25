/*global angular*/
'use strict';

(function() {
	var filterModule = angular.module('filter', ['panel', 'variableTypes', 'utilities', 'multiSelect']);

	filterModule.directive('omFilter', ['panelService', 'variableTypesService', 'serviceUtilities',
		function(panelService, variableTypesService, serviceUtilities)  {
		return {
			controller: function($scope) {
				$scope.smallPanelName = 'filters';
				$scope.data = {
					types: []
				};

				$scope.addNewFilter = function() {
					panelService.showPanel($scope.smallPanelName);
				};

				variableTypesService.getTypes().then(function(types) {
					$scope.data.types = types;
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
