/*global angular*/
'use strict';

(function() {
	var filterModule = angular.module('filter', ['panel', 'variableTypes', 'dataTypes', 'utilities', 'multiSelect',
		'ui.bootstrap']);

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
						}],
						calendarOpened1: false,
						calendarOpened2: false
					};

					$scope.addNewFilter = function() {
						panelService.showPanel($scope.smallPanelName);
					};

					$scope.isFilterActive = function() {
						return ($scope.filterOptions && $scope.filterOptions.variableTypes &&
							$scope.filterOptions.variableTypes.length !== 0) || ($scope.filterOptions && $scope.filterOptions.scaleType &&
							$scope.filterOptions.scaleType.name !== '...') || ($scope.filterOptions &&
							$scope.filterOptions.dateCreatedFrom && $scope.filterOptions.dateCreatedFrom.getTime !== undefined) ||
							($scope.filterOptions && $scope.filterOptions.dateCreatedTo &&
							$scope.filterOptions.dateCreatedTo.getTime !== undefined);
					};

					variableTypesService.getTypes().then(function(types) {
						$scope.data.types = types;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					dataTypesService.getNonSystemDataTypes().then(function(types) {
						$scope.data.scaleTypes = $scope.data.scaleTypes.concat(types);
					}, function(response) {
						$scope.serverErrors = serviceUtilities.formatErrorsForDisplay(response);
						$scope.someListsNotLoaded = true;
					});

					$scope.dateOptions = {
						formatYear: 'yy',
						startingDay: 1
					};

					$scope.today = function() {
						$scope.filterOptions.dateCreatedFrom = new Date();
						$scope.filterOptions.dateCreatedTo = new Date();
					};

					$scope.open1 = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.calendarOpened1 = true;
					};

					$scope.open2 = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.calendarOpened2 = true;
					};

					$scope.clear = function() {
						$scope.filterOptions.dateCreatedFrom = null;
						$scope.filterOptions.dateCreatedTo = null;
					};

					$scope.todaysDate = new Date();

				},
				restrict: 'E',
				scope: {
					filterOptions: '=omFilterOptions'
				},
				templateUrl: 'static/views/ontology/filter.html'
			};
		}
	]);

}());
