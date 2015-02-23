/*global angular, alert*/
'use strict';

(function() {
	var app = angular.module('addScale', []);

	app.controller('AddScaleController', ['$scope', function($scope) {

		this.dataTypes = [];

		$scope.saveScale = function(e) {
			e.preventDefault();
			alert('Save scale');
		};

		// We hide and show stuff based on whether the data type is numeric or categorical. We will need
		// to hook this up to the actually selected data type from the data type select
		$scope.selectedDataType = 'numeric';
	}]);

}());
