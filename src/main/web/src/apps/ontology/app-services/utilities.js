/*global angular*/
'use strict';

(function() {
	var app = angular.module('utilities', []);

	app.factory('serviceUtilities', function() {
		return {
			genericAndRatherUselessErrorHandler: function(error) {
				if (console) {
					console.log(error);
				}
			}
		};
	});
}());
