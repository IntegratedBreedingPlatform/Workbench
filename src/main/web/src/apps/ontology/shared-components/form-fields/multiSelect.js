/*global angular*/
'use strict';

(function() {
	var multiSelect = angular.module('multiSelect', ['formFields', 'clickAway']);

	multiSelect.directive('omMultiSelect', ['editable', 'stringDataService', 'objectDataService',
		function(editable, stringDataService, objectDataService) {

			return {
				controller: function($scope) {
					$scope.editable = editable($scope);

					$scope.$watch('options', function(options) {
						// Check the type of data the multi-select needs to work with and set the appropriate service
						if (typeof options[0] === 'string') {
							$scope.service = stringDataService;
						} else if (typeof options[0] === 'object') {
							$scope.service = objectDataService;
						}

						// Set the functions of the multi-select based on the data service that we are using
						if ($scope.service) {
							$scope.addToSelectedItems = $scope.service.addToSelectedItems($scope);
							$scope.formatForDisplay = $scope.service.formatForDisplay;
							$scope.search = $scope.service.search($scope);
						}
					});
				},
				link: function(scope) {

					scope.suggestions = angular.copy(scope.options);
					scope.searchText = '';
					scope.selectedIndex = -1;

					// Set the input to contain the text of the selected item from the suggestions
					scope.$watch('selectedIndex', function(index) {
						if (index !== -1 && scope.suggestions.length > 0) {
							scope.searchText = scope.service.formatForDisplay(scope.suggestions[index]);
						}
					});

					scope.checkKeyDown = function(event) {

						// Down key, increment selectedIndex
						if (event.keyCode === 40) {
							event.preventDefault();

							// Load the suggestions if the user presses down with an empty input
							if (scope.selectedIndex === -1) {
								scope.showSuggestions();
							}

							if (scope.selectedIndex + 1 < scope.suggestions.length) {
								scope.selectedIndex++;
							}
						}
						// Up key, decrement selectedIndex
						else if (event.keyCode === 38) {
							event.preventDefault();

							if (scope.selectedIndex - 1 > -1) {
								scope.selectedIndex--;
							}
						}
						// Enter pressed, select item
						else if (event.keyCode === 13) {
							event.preventDefault();
							scope.addToSelectedItems(scope.selectedIndex);
							scope.hideSuggestions();
						}
					};

					scope.onClick = function(index) {
						scope.addToSelectedItems(index);
						scope.hideSuggestions();
					};

					scope.removeItem = function(index) {
						scope.model[scope.property].splice(index, 1);
					};

					scope.toggleSuggestions = function() {
						if (scope.selectedIndex === -1 && scope.suggestions.length === 0) {
							scope.showSuggestions();
						} else {
							scope.hideSuggestions();
						}
					};

					scope.showSuggestions = function() {
						scope.search();
						scope.enabled = true;
					};

					scope.hideSuggestions = function() {
						scope.suggestions = [];
						scope.selectedIndex = -1;
						scope.searchText = '';
						scope.enabled = false;
					};

				},
				restrict: 'E',
				scope: {
					adding: '=omAdding',
					editing: '=omEditing',
					id: '@omId',
					label: '@omLabel',
					model: '=omModel',
					options: '=omOptions',
					property: '@omProperty',
					tags: '@omTags'
				},
				templateUrl:'static/views/ontology/multiSelect.html'
			};
		}
	]);

	/*
	This service is used when the multi-select is dealing with arrays of strings
	*/
	multiSelect.service('stringDataService', function() {
		return {

			addToSelectedItems: function(scope) {
				return function(index) {
					var itemToAdd = scope.suggestions[index];

					// Allow the user to add the text they have entered as an item
					// without having to select it from the list
					if (scope.searchText && !itemToAdd && scope.tags) {
						itemToAdd = scope.searchText;
					}

					// Add the item if it hasn't already been added
					if (itemToAdd && scope.model[scope.property].indexOf(itemToAdd) === -1) {
						scope.model[scope.property].push(itemToAdd);
					}
				};
			},

			formatForDisplay: function(item) {
				return item;
			},

			search: function(scope) {
				return function() {
					scope.suggestions = angular.copy(scope.options);

					if (scope.tags) {

						// Add the search term text that the user has entered into the start of the
						// suggestions list so that they can add it if no suitable suggestion is found
						if (scope.searchText && scope.suggestions.indexOf(scope.searchText) === -1) {
							scope.suggestions.unshift(scope.searchText);
						}
					}

					// Only return options that match the search term
					scope.suggestions = scope.suggestions.filter(function(value) {

						var lowerValue = value.toLowerCase(),
							lowerSearchText = scope.searchText.toLowerCase();

						return lowerValue.indexOf(lowerSearchText) !== -1;
					});

					// Only return options that haven't already been selected
					scope.suggestions = scope.suggestions.filter(function(value) {

						return scope.model[scope.property].indexOf(value) === -1;
					});

					scope.selectedIndex = -1;
				};
			}
		};
	});

	/*
	This service is used when the multi-select is dealing with arrays of objects that have a name property
	*/
	multiSelect.service('objectDataService', function() {
		return {

			addToSelectedItems: function(scope) {
				return function(index) {
					var itemToAdd = scope.suggestions[index];

					// Add the item if it hasn't already been added
					if (itemToAdd && scope.model[scope.property].indexOf(itemToAdd) === -1) {
						scope.model[scope.property].push(itemToAdd);
					}
				};
			},

			formatForDisplay: function(item) {
				return item.name;
			},

			search: function(scope) {
				return function() {
					scope.suggestions = angular.copy(scope.options);

					// Only return options that match the search term
					scope.suggestions = scope.suggestions.filter(function(value) {

						var lowerValue = value.name.toLowerCase(),
							lowerSearchText = scope.searchText.toLowerCase();

						return lowerValue.indexOf(lowerSearchText) !== -1;
					});

					// Only return options that haven't already been selected
					scope.suggestions = scope.suggestions.filter(function(value) {

						var isAlreadySelected = scope.model[scope.property].some(function(selectedItem) {
								return selectedItem.name === value.name;
							});

						return !isAlreadySelected;
					});

					scope.selectedIndex = -1;
				};
			}
		};
	});
}());
