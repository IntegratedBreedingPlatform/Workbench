/*global angular*/
'use strict';

(function() {
	var multiSelect = angular.module('multiSelect', ['formFields']);

	// This will work for classes at this stage. Will just need to get rid of
	// adding your own in the search function and then it should work for multi
	// select as well
	multiSelect.directive('omMultiSelect', function(editable) {
		return {
			controller: function($scope) {
				$scope.editable = editable($scope);
			},
			link: function(scope) {
				scope.suggestions = angular.copy(scope.options);
				scope.selectedTags = [];

				scope.selectedIndex = -1; //currently selected suggestion index

				// Set the input to contain the text of the selected item from the suggestions
				scope.$watch('selectedIndex', function(index) {
					if (index !== -1) {
						scope.model[scope.property] = scope.suggestions[index];
					}
				});

				scope.search = function() {
					scope.suggestions = angular.copy(scope.options);

					// Add the search term text that the user has entered into the start of the
					// suggestions list so that they can add it if no suitable tag is found
					if (scope.model[scope.property] && scope.suggestions.indexOf(scope.model[scope.property]) === -1) {
						scope.suggestions.unshift(scope.model[scope.property]);
					}

					// Only return options that match the search term
					scope.suggestions = scope.suggestions.filter(function(value) {

						var lowerValue = value.toLowerCase(),
							lowerSearchText = scope.model[scope.property].toLowerCase();

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
					if (scope.model[scope.property] && !tagToAdd) {
						tagToAdd = scope.model[scope.property];
					}

					// Add the tag if it hasn't already been added
					if (tagToAdd && scope.selectedTags.indexOf(tagToAdd) === -1) {
						scope.selectedTags.push(tagToAdd);
						scope.model[scope.property] = '';
						scope.suggestions = [];
					}
				};

				scope.removeTag = function(index) {
					scope.selectedTags.splice(index, 1);
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
				property: '=omProperty'
			},
			templateUrl:'static/views/ontology/multiSelect.html'
		};
	});
}());
