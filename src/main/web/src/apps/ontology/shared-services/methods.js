/*global angular, console*/
'use strict';

(function() {
	var app = angular.module('methods', []);

	app.service('methodService', [function() {
		return {
			saveMethod: function(method) {
				// TODO Call actual save functionality
				console.log('Saving method');
				console.log(method);
			}
		};
	}]);
}());
