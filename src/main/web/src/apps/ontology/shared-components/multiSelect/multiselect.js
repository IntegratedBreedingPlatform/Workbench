/*global angular*/
'use strict';

(function() {
	var multiSelect = angular.module('multiSelect', ['formFields', 'clickAway', 'selectScroll', 'utilities']);

	multiSelect.directive('omMultiSelect', ['editable', 'selectScroll', 'collectionUtilities', 'ieUtilities', function(editable,
		selectScroll, collectionUtilities, ieUtilities) {

		return {
			controller: function($scope) {
				$scope.editable = editable($scope);
			},

			link: function(scope, elm, attrs, ctrl) {
				var listElement = elm.find('ul'),
					rawListElement = listElement[0];

				scope.suggestions = angular.copy(scope.options);
				scope.searchText = '';
				scope.selectedIndex = -1;

				ieUtilities.addIeClearInputHandler(elm, function() {
					scope.hideSuggestions();
					scope.$apply();
				});

				// Set the input to contain the text of the selected item from the suggestions
				scope.$watch('selectedIndex', function(index) {
					if (index !== -1 && scope.suggestions.length > 0) {
						scope.searchText = scope.formatForDisplay(scope.suggestions[index]);
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

					// Add the item if it hasn't already been added
					if (itemToAdd && scope.model[scope.property].indexOf(itemToAdd) === -1) {
						scope.model[scope.property].push(itemToAdd);
						return true;
					}

					return false;
				};

				scope.formatForDisplay = function(item) {
					return item.name;
				};

				scope.formatListForDisplay = collectionUtilities.formatListForDisplay;

				scope.search = function() {
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
						selectScroll.ensureHighlightVisible(listElement, rawListElement, scope.selectedIndex);
					} else if (event.keyCode === 38) {
						// Up key, decrement selectedIndex
						event.preventDefault();

						if (scope.selectedIndex - 1 > -1) {
							scope.selectedIndex--;
						}
						selectScroll.ensureHighlightVisible(listElement, rawListElement, scope.selectedIndex);
					} else if (event.keyCode === 13) {
						// Enter pressed, select item
						event.preventDefault();

						itemAdded = scope.addToSelectedItems(scope.selectedIndex);
						if (itemAdded) {
							scope.hideSuggestions();
						}
					} else if (event.keyCode === 27) {
						// Escape pressed, close suggestions
						event.preventDefault();
						if (!scope.isSuggestionBoxClosed()) {
							event.stopPropagation();
						}
						scope.hideSuggestions();
					} else if (event.keyCode === 9) {
						// Tab pressed, close suggestions and continue with event
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

				scope.isSuggestionBoxClosed = function() {
					return scope.selectedIndex === -1 && scope.suggestions.length === 0;
				};

				scope.toggleSuggestions = function() {
					if (scope.isSuggestionBoxClosed()) {
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
					selectScroll.resetScroll(rawListElement);
					scope.suggestions = [];
					scope.selectedIndex = -1;
					scope.searchText = '';
					scope.suggestionsShown = false;
					elm.find('input')[0].focus();
				};

			},
			require: 'ngModel',
			restrict: 'E',
			scope: {
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=ngModel',
				options: '=omOptions',
				property: '@omProperty'
			},
			templateUrl:'static/views/ontology/multiSelect.html'
		};
	}]);

}());
