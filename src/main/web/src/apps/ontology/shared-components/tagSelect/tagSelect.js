/*global angular*/
'use strict';

(function() {
	var tagSelect = angular.module('tagSelect', ['formFields', 'clickAway']);

	tagSelect.directive('omTagSelect', ['editable', function(editable) {

		return {
			controller: function($scope) {
				$scope.editable = editable($scope);
			},

			link: function(scope, elm, attrs, ctrl) {
				scope.suggestions = angular.copy(scope.options);
				scope.searchText = '';
				scope.selectedIndex = -1;

				// Set the input to contain the text of the selected item from the suggestions
				scope.$watch('selectedIndex', function(index) {
					if (index !== -1 && scope.suggestions.length > 0) {
						scope.searchText = scope.suggestions[index];
					}
				});

				scope.$watch('model[property]', function(items) {
					ctrl.$setValidity('emptyValue', true);

					if (items && items.length < 1) {
						ctrl.$setValidity('emptyValue', false);
					}

				}, true);

				scope.addToSelectedItems = function(index) {
					var itemToAdd = scope.suggestions[index];

					// Allow the user to add the text they have entered as an item
					// without having to select it from the list
					if (scope.searchText && !itemToAdd) {
						itemToAdd = scope.searchText;
					}

					// Add the item if it hasn't already been added
					if (itemToAdd && scope.model[scope.property].indexOf(itemToAdd) === -1) {
						scope.model[scope.property].push(itemToAdd);
						return true;
					}

					return false;
				};

				scope.search = function() {
					scope.suggestions = angular.copy(scope.options);

					// Add the search term text that the user has entered into the start of the
					// suggestions list so that they can add it if no suitable suggestion is found
					if (scope.searchText && scope.suggestions.indexOf(scope.searchText) === -1) {
						scope.suggestions.unshift(scope.searchText);
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

				scope.checkKeyDown = function(event) {
					var itemAdded;

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

						itemAdded = scope.addToSelectedItems(scope.selectedIndex);
						if (itemAdded) {
							scope.hideSuggestions();
						}
					}
					// Escape pressed, close suggestions
					else if (event.keyCode === 27) {
						event.preventDefault();
						scope.hideSuggestions();
					}
				};

				scope.onClick = function(index) {
					scope.addToSelectedItems(index);
					scope.hideSuggestions();
				};

				scope.removeItem = function(event, index) {
					event.preventDefault();
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
					scope.suggestionsShown = true;
				};

				scope.hideSuggestions = function() {
					scope.suggestions = [];
					scope.selectedIndex = -1;
					scope.searchText = '';
					scope.suggestionsShown = false;
				};

			},
			require: 'ngModel',
			restrict: 'E',
			scope: {
				adding: '=omAdding',
				editing: '=omEditing',
				name: '@omName',
				model: '=ngModel',
				options: '=omOptions',
				property: '@omProperty'
			},
			templateUrl:'static/views/ontology/tagSelect.html'
		};
	}]);

}());
