/*global angular*/
'use strict';

(function() {
	var app = angular.module('scalesView', ['scales', 'scaleDetails','list', 'panel']);

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

	app.controller('ScalesController', ['$scope', 'scalesService', 'panelService',
		function($scope, scalesService, panelService) {
			var ctrl = this;
			this.scales = [];

			$scope.panelName = 'scales';

			ctrl.colHeaders = ['name', 'description', 'dataType'];

			scalesService.getScales().then(function(scales) {
				ctrl.scales = transformToDisplayFormat(scales);
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
