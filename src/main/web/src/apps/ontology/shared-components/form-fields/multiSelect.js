/*global angular*/
'use strict';

(function() {
	var multiSelect = angular.module('multiSelect', []);

	// This will work for classes at this stage. Will just need to get rid of
	// adding your own in the search function and then it should work for multi
	// select as well
	multiSelect.directive('omMultiSelect', function() {
		return {
			link: function(scope) {
				scope.suggestions = angular.copy(scope.options);
				scope.searchText = '';
				scope.selectedTags = [];

				scope.selectedIndex = -1; //currently selected suggestion index

				// Set the input to contain the text of the selected item from the suggestions
				scope.$watch('selectedIndex', function(index) {
					if (index !== -1) {
						scope.searchText = scope.suggestions[index];
					}
				});

				scope.search = function() {
					scope.suggestions = angular.copy(scope.options);

					// Add the search term text that the user has entered into the start of the
					// suggestions list so that they can add it if no suitable tag is found
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

						return scope.selectedTags.indexOf(value) === -1;
					});


					scope.selectedIndex = -1;
				};

				scope.checkKeyDown = function(event) {

					// Down key, increment selectedIndex
					if (event.keyCode === 40) {
						event.preventDefault();

						// Load the suggestions if the user presses down with an empty input
						if (scope.selectedIndex === -1) {
							scope.search();
						}

						// Otherwise we just want to increase the selected index
						if (scope.selectedIndex + 1 !== scope.suggestions.length) {
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
						scope.addToSelectedTags(scope.selectedIndex);
						scope.selectedIndex = -1;
					}
				};

				scope.addToSelectedTags = function(index) {
					var tagToAdd = scope.suggestions[index];

					// Allow the user to add the text they have entered as a tag
					// without having to select it from the list
					if (scope.searchText && !tagToAdd) {
						tagToAdd = scope.searchText;
					}

					// Add the tag if it hasn't already been added
					if (tagToAdd && scope.selectedTags.indexOf(tagToAdd) === -1) {
						scope.selectedTags.push(tagToAdd);
						scope.searchText = '';
						scope.suggestions = [];
					}
				};

				scope.removeTag = function(index) {
					scope.selectedTags.splice(index, 1);
				};
			},
			restrict: 'E',
			scope: {
				label: '@omLabel',
				options: '=omOptions',
				selectedTags: '=omModel'
			},
			templateUrl:'static/views/ontology/multiSelect.html'
		};
	});
}());
