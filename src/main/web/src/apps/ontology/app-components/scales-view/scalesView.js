/*global angular*/
'use strict';

(function() {
	var app = angular.module('scalesView', ['scales', 'list', 'panel']);

	app.controller('ScalesController', ['$scope', 'scalesService', 'panelService',
		function($scope, scalesService, panelService) {
			var ctrl = this;
			this.scales = [];

			$scope.panelName = 'scales';

			ctrl.colHeaders = ['Name', 'DataType'];

			scalesService.getScales().then(function(scales) {
				ctrl.scales = scales.map(function(item) {
					return {
						id: item.id,
						Name: item.name,
						Description: item.description,
						DataType: item.dataType.name
					};
				});
			});

			$scope.showScaleDetails = function() {

				scalesService.getScale($scope.selectedItem.id).then(function(scale) {
					$scope.selectedScale = scale;
				});

				panelService.showPanel($scope.panelName);
			};

			$scope.selectedItem = {id: null};
			$scope.selectedScale = null;
		}
	]);
}());
