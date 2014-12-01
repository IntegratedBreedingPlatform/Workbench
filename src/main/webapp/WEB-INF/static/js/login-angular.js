/* global angular */
'use strict';

(function() {

	var loginApp = angular.module('loginApp', []);

	function formController($scope, $http) {

		$scope.formData = {};

		// Set up a function to enable us to pass Thymeleaf-ed URLs through the HTML which will have correct context paths prefixed
		$scope.setActions = function(loginAction, createAccountAction) {
			$scope.loginAction = loginAction;
			$scope.createAccountAction = createAccountAction;
		};

		$scope.processForm = function() {

			$http({
				method: 'POST',
				url: $scope.loginAction,
				data: $scope.formData,
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				}
			});
		};
	}

	loginApp.controller('formController', ['$scope', '$http', formController]);
}());
