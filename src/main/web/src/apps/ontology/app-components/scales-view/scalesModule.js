/*global angular*/
'use strict';

(function() {
	var app = angular.module('scalesView', ['scales', 'list', 'panel']);

	app.controller('ScalesController', ['$scope', 'scalesService', function($scope, scalesService) {
		var ctrl = this;
		this.scales = [];

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

		$scope.panelOpen = {show: false};

		$scope.showScaleDetails = function() {

			scalesService.getScale($scope.selectedItem.id).then(function(scale) {
				$scope.selectedScale = scale;
			});

			$scope.panelOpen.show = true;
		};

		$scope.selectedItem = {id: null};
		$scope.selectedScale = null;
	}]);
}());
