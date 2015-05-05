/*global expect, inject*/
'use strict';

describe('selectScroll module', function() {

	describe('selectScroll factory', function() {
		var scope,
			selectScroll;

		beforeEach(function() {
			module('selectScroll');
		});

		beforeEach(inject(function($rootScope) {
			scope = $rootScope.$new();
		}));

		beforeEach(function() {
			inject(function(_selectScroll_) {
				selectScroll = _selectScroll_;
			});
		});

		describe('ensureHighlightVisible', function() {

			it('should return immediately and not modify the scroll if the selectedIndex is less than 0', function() {
				var listElement = {
						find: function() {}
					},
					rawListElement = {
						scrollTop: 5 // Distance from scrollbar to top of container
					};
				selectScroll.ensureHighlightVisible(listElement, rawListElement, -1);
				expect(rawListElement.scrollTop).toEqual(5);
			});

			it('should set the scroll position so that the selected element is visible when it is below the visible area', function() {
				var listElement = {
						find: function() {
							return [{
								clientHeight: 10, // Height of item minus padding
								offsetTop: 40 // Distance from top of container to this item
							}];
						},
					},
					rawListElement = {
						offsetHeight: 40, // Height of list element
						scrollTop: 0 // Distance from scrollbar to top of container
					};

				selectScroll.ensureHighlightVisible(listElement, rawListElement, 0);
				// The scroll should move down by the height of one item
				expect(rawListElement.scrollTop).toEqual(10);
			});

			it('should set the scroll position so that the selected element is visible when it is above the visible area', function() {
				var listElement = {
						find: function() {
							return [{
								clientHeight: 10, // Height of item minus padding
								offsetTop: 0 // Distance from top of container to this item
							}];
						}
					},
					rawListElement = {
						offsetHeight: 40, // Height of list element
						scrollTop: 10 // Distance from scrollbar to top of container
					};

				selectScroll.ensureHighlightVisible(listElement, rawListElement, 0);
				// The scroll should move up by the height of one item
				expect(rawListElement.scrollTop).toEqual(0);
			});

			it('should not change the scroll position when the selected element is already at the scroll position', function() {
				var listElement = {
						find: function() {
							return [{
								clientHeight: 10, // Height of item minus padding
								offsetTop: 0, // Distance from top of container to this item
							}];
						}
					},
					rawListElement = {
						offsetHeight: 40, // Height of list element
						scrollTop: 0 // Distance from scrollbar to top of container
					};

				selectScroll.ensureHighlightVisible(listElement, rawListElement, 0);
				// The scroll should not change
				expect(rawListElement.scrollTop).toEqual(0);
			});
		});

		describe('resetScroll', function() {

			it('should set the scroll back to 0', function() {
				var listElement = {
					scrollTop: 5
				};
				selectScroll.resetScroll(listElement);
				expect(listElement.scrollTop).toEqual(0);
			});
		});
	});
});
