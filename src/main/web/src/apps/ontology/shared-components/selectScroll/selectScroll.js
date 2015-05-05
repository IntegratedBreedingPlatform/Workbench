/*global angular*/
'use strict';

(function() {

	/*
	Module used to ensure that scrolling via keyboard navigation is possible in small
	scrollable containers, such as the multiselect dropdown.
	*/
	var selectScroll = angular.module('selectScroll', []);

	selectScroll.factory('selectScroll', function() {

		return {
			/*
			Ensures that the highlighted item in the given list element is visible in the
			current viewport of the list.

			@param listElement the angular jqLite element for the list
			@param rawListElement the raw DOM element for the list
			@param selectedIndex the index of the currently selected item in the list
			*/
			ensureHighlightVisible: function(listElement, rawListElement, selectedIndex) {
				var listItems = listElement.find('li'),
					highlighted,
					yPos,
					height;

				if (selectedIndex < 0) {
					return;
				}
				highlighted = listItems[selectedIndex];
				yPos = highlighted.offsetTop + highlighted.clientHeight - rawListElement.scrollTop;
				height = rawListElement.offsetHeight;

				if (yPos > height) {

					// When the element is off the bottom of the container
					rawListElement.scrollTop += yPos - height;
				} else if (yPos < highlighted.clientHeight) {

					// When the element is off the top of the container
					rawListElement.scrollTop -= highlighted.clientHeight - yPos;
				}
			},

			/*
			Resets the scroll position of the given list to the top of the container.

			@param the raw DOM element for the list
			*/
			resetScroll: function(rawListElement) {
				rawListElement.scrollTop = 0;
			}
		};
	});
}());
