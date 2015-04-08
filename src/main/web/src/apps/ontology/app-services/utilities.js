/*global angular*/
'use strict';

(function() {
	var app = angular.module('utilities', []);

	app.factory('serviceUtilities', ['$q', function($q) {

		return {
			genericAndRatherUselessErrorHandler: function(error) {
				if (console) {
					console.log(error);
				}
			},

			formatErrorsForDisplay: function(response) {

				var errors = response.errors,
					formattedErrors = {};

				if (errors) {
					errors.forEach(function(err) {
						var formFieldNames = err.fieldNames;

						if (formFieldNames && formFieldNames.length > 0) {
							formFieldNames.forEach(function(field) {
								formattedErrors[field] = formattedErrors[field] || [];
								formattedErrors[field].push(err.message);
							});
						} else {
							formattedErrors.general = formattedErrors.general || [];
							formattedErrors.general.push(err.message);
						}
					});
				}
				return formattedErrors;
			},

			restSuccessHandler: function(response) {
				return response.data;
			},

			restFailureHandler: function(response) {
				return $q.reject({
					status: response.status,
					errors: response.data && response.data.errors
				});
			}
		};
	}]);

	app.factory('formUtilities', function() {

		return {
			formGroupClassGenerator: function($scope, formName) {
				return function(fieldName) {
					var className = 'formGroup';

					// If the field hasn't been initialised yet, don't do anything!
					if ($scope[formName] && $scope[formName][fieldName]) {

						// Don't mark as invalid until we are relatively sure the user is finished doing things
						if ($scope[formName].$submitted || $scope[formName][fieldName].$touched) {

							// Only mark as invalid if the field is.. well, invalid
							if ($scope[formName][fieldName].$invalid) {
								className += ' has-error';
							}
						}
					}
					return className;
				};
			}
		};
	});
}());
