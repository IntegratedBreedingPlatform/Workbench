/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('scales', []);

	app.service('scaleService', [function() {
		return {
			saveScale: function(scale) {
				// TODO Call actual save functionality
				console.log('Saving scale');
				console.log(scale);
			}
		};
	}]);
}());
