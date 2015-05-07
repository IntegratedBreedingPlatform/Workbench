/*global angular*/
'use strict';

(function() {
	var app = angular.module('scalesView', ['scales', 'scaleDetails', 'list', 'panel', 'utilities']),
		DELAY = 400;

	function transformScaleToDisplayFormat(scale, id) {
		return {
			id: scale.id || id,
			name: scale.name,
			description: scale.description,
			dataType: scale.dataType.name
		};
	}

	function transformToDisplayFormat(scales) {
		return scales.map(transformScaleToDisplayFormat);
	}

	app.controller('ScalesController', ['$scope', 'scalesService', 'panelService', '$timeout', 'collectionUtilities',
		function($scope, scalesService, panelService, $timeout, collectionUtilities) {
			var ctrl = this;

			ctrl.scales = [];
			ctrl.showThrobberWrapper = true;
			ctrl.colHeaders = ['name', 'description', 'dataType'];

			$scope.panelName = 'scales';

			$timeout(function() {
				ctrl.showThrobber = true;
			}, DELAY);

			scalesService.getScales().then(function(scales) {
				ctrl.scales = transformToDisplayFormat(scales);
				ctrl.showThrobberWrapper = false;

				if (ctrl.scales.length === 0) {
					ctrl.showNoItemsMessage = true;
				}
			});

			$scope.showScaleDetails = function() {
				// Ensure the previously selected scale doesn't show in the panel before we've retrieved the new one
				$scope.selectedScale = null;

				scalesService.getScale($scope.selectedItem.id).then(function(scale) {
					$scope.selectedScale = scale;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.updateSelectedScale = function(updatedScale) {

				var selectedIndex = -1,
					transformedScale = updatedScale && transformScaleToDisplayFormat(updatedScale, $scope.selectedItem.id);

				ctrl.scales.some(function(scale, index) {
					if (scale.id === $scope.selectedItem.id) {
						selectedIndex = index;
						return true;
					}
				});

				// Not much we can really do if we don't find it in the list. Just don't update.
				if (selectedIndex !== -1) {
					if (transformedScale) {
						ctrl.scales[selectedIndex] = transformedScale;
						collectionUtilities.sortByName(ctrl.scales);
					} else {
						ctrl.scales.splice(selectedIndex, 1);
					}
				}
			};

			$scope.selectedItem = {id: null};
			$scope.selectedScale = null;
		}
	]);
}());
