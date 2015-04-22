/*global angular*/
'use strict';

(function() {
	var app = angular.module('utilities', []);

	app.factory('serviceUtilities', ['$q', function($q) {

		return {
			genericAndRatherUselessErrorHandler: function(error) {
				/* istanbul ignore else */
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

	app.factory('formUtilities', ['$window', '$q', '$timeout', function($window, $q, $timeout) {

		var formUtilities = {

			formGroupClassGenerator: function($scope, formName) {
				return function(fieldName, serverFieldName) {
					var className = 'form-group';

					// If the field hasn't been initialised yet, don't do anything!

					if ($scope[formName] && $scope[formName][fieldName]) {

						// If there are server errors for this field and the user hasn't tried to correct it, mark as invalid
						if ($scope.serverErrors && $scope.serverErrors[serverFieldName] && !$scope[formName][fieldName].$touched) {
							className += ' has-error';
						}

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
			},

			cancelAddHandler: function(scope, formDirty) {
				if (formDirty) {
					formUtilities.confirmationHandler(scope).then(formUtilities.goBack);
				} else {
					formUtilities.goBack();
				}
			},

			/*
			Exposes confirm and deny functions on the specified scope that allow callers to reject or resolve the returned
			promise. Also expses a confirmationNecessary property that is set to true when called and false when the promise
			is fulfilled.

			An optionalScopeProperty can be passed, and will be set to the value of confirmationNecessary. It can (for
			example) be used to distingush between two confirmationHandlers being used on the same page.
			*/
			confirmationHandler: function($scope, optionalScopeProperty) {
				var confirmation;

				// Generate a promise and expose two new methods on the scope to resolve and reject this promise.
				confirmation = $q.defer();
				$scope.confirmationNecessary = true;

				if (optionalScopeProperty) {
					$scope[optionalScopeProperty] = true;
				}

				$scope.confirm = function(e) {
					if (e) {
						e.preventDefault();
					}
					confirmation.resolve();
				};

				$scope.deny = function(e) {
					if (e) {
						e.preventDefault();
					}
					confirmation.reject();
				};

				return confirmation.promise.finally(function() {
					$timeout(function() {
						$scope.confirmationNecessary = false;

						if (optionalScopeProperty) {
							$scope[optionalScopeProperty] = false;
						}

						delete $scope.confirm;
						delete $scope.deny;
					}, 200);
				});
			},

			goBack: function() {
				$window.history.back();
			}
		};

		return formUtilities;
	}]);
}());
