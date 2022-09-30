/*global angular*/
'use strict';

(function() {
	var filterModule = angular.module('filter', ['panel', 'variableTypes', 'dataTypes', 'utilities', 'multiSelect', 'select',
		'ui.bootstrap', 'errorList']);

	filterModule.directive('omFilter', ['panelService', 'variableTypesService', 'serviceUtilities', 'dataTypesService',
		function(panelService, variableTypesService, serviceUtilities, dataTypesService)  {

			var LISTS_NOT_LOADED_TRANSLATION = 'validation.filter.someListsNotLoaded';

			return {
				controller: ['$scope', function($scope) {
					$scope.smallPanelName = 'filters';
					$scope.data = {
						types: [],
						scaleDataTypes: [],
						fromCalendarOpened: false,
						toCalendarOpened: false,
						obsolete: false
					};
					$scope.dateOptions = {
						formatYear: 'yy',
						startingDay: 1
					};
					$scope.todaysDate = new Date();

					var excludeRestrictedTypes = false;
					variableTypesService.getTypes(excludeRestrictedTypes).then(function(types) {
						$scope.data.types = types;
					}, function(response) {
						$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
						$scope.serverErrors.someListsNotLoaded = [LISTS_NOT_LOADED_TRANSLATION];
					});

					dataTypesService.getNonSystemDataTypes().then(function(types) {
						$scope.data.scaleDataTypes = $scope.data.scaleDataTypes.concat(types);
					}, function(response) {
						$scope.serverErrors = serviceUtilities.serverErrorHandler($scope.serverErrors, response);
						$scope.serverErrors.someListsNotLoaded = [LISTS_NOT_LOADED_TRANSLATION];
					});

					$scope.addNewFilter = function() {
						panelService.showPanel($scope.smallPanelName);
					};

					$scope.clearFilters = function() {
						$scope.filterOptions = {
							variableTypes: [],
							scaleDataType: null,
							dateCreatedFrom: null,
							dateCreatedTo: null,
							obsolete: false
						};
					};

					$scope.isFilterActive = function() {
						var filterOptionsValued = $scope.filterOptions,
							variableTypesActive,
							scaleDataTypesActive,
							dateCreatedFromActive,
							dateCreatedToActive;
							obsoleteFilterActive;

						if (!filterOptionsValued) {
							return false;
						}

						variableTypesActive = $scope.filterOptions.variableTypes && $scope.filterOptions.variableTypes.length !== 0;

						scaleDataTypesActive = !!$scope.filterOptions.scaleDataType;

						dateCreatedFromActive = $scope.filterOptions.dateCreatedFrom &&
							$scope.filterOptions.dateCreatedFrom.getTime !== undefined;

						dateCreatedToActive = $scope.filterOptions.dateCreatedTo &&
							$scope.filterOptions.dateCreatedTo.getTime !== undefined;

						obsoleteFilterActive = !!$scope.filterOptions.obsolete;

						return variableTypesActive || scaleDataTypesActive || dateCreatedFromActive || dateCreatedToActive || obsoleteFilterActive;
					};

					$scope.openFromCalendar = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.fromCalendarOpened = true;
					};

					$scope.openToCalendar = function($event) {
						$event.preventDefault();
						$event.stopPropagation();

						$scope.data.toCalendarOpened = true;
					};

				}],
				restrict: 'E',
				scope: {
					filterOptions: '=omFilterOptions'
				},
				templateUrl: 'static/views/ontology/filter.html'
			};
		}
	]);

}());
