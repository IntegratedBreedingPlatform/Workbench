/*global angular*/
'use strict';

(function() {
	var app = angular.module('utilities', []);

	app.factory('serviceUtilities', ['$q', 'collectionUtilities', function($q, collectionUtilities) {

		return {
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
			},

			serverErrorHandler: function(currentErrors, response) {
				var errors = this.formatErrorsForDisplay(response);

				if (currentErrors) {
					return collectionUtilities.mergeObjectsWithArrayProperties(errors, currentErrors);
				}

				return errors;
			}
		};
	}]);

	app.factory('formUtilities', ['$window', '$q', '$timeout', '$location', function($window, $q, $timeout, $location) {

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

			formParentHasError: function($scope, formName) {
				return function(fieldName, serverFieldName) {
					var className = '';

					// If the field hasn't been initialised yet, don't do anything!

					if ($scope[formName] && $scope[formName][fieldName]) {

						if ($scope.$parent && $scope.$parent.serverErrors && $scope.$parent.serverErrors[serverFieldName] &&
							!$scope[formName][fieldName].$touched) {
							className = 'has-error';
						}

						// Don't mark as invalid until we are relatively sure the user is finished doing things
						if ($scope[formName].$submitted || $scope[formName][fieldName].$touched) {

							// Only mark as invalid if the field is.. well, invalid
							if ($scope[formName][fieldName].$invalid) {
								className = 'has-error';
							}
						}
					}
					return className;
				};
			},

			cancelAddHandler: function(scope, formDirty, location) {
				if (formDirty) {
					formUtilities.confirmationHandler(scope).then($location.path(location));
				} else {
					$location.path(location);
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
			}
		};

		return formUtilities;
	}]);

	app.factory('collectionUtilities', function() {

		return {

			/*
			Takes a collection of objects and sorts those objects alphabetically by their name property.
			*/
			sortByName: function(collection) {
				return collection.sort(function(itemOne, itemTwo) {
					return itemOne.name.toLowerCase().localeCompare(itemTwo.name.toLowerCase());
				});
			},

			/*
			Takes a collection of objects and returns a comma separated string of their name properties.
			*/
			formatListForDisplay: function(items) {
				if (items) {
					var names = items.map(function(item) {
						return item.name;
					});
					return names.join(', ');
				}
				return '';
			},

			/*
			Takes two objects that have properties whose values are arrays, and merges those objects
			so that the resulting object has all of the properties of both objects, and any properties that
			were present in both objects, have an array containing all objects from both. Note that duplicates
			are not removed, so if an item is present in an array on both the source and destination object
			then it will be duplicated in the array on the resulting object.
			*/
			mergeObjectsWithArrayProperties: function(source, destination) {
				var sourceProperty;

				for (sourceProperty in source) {
					if (source.hasOwnProperty(sourceProperty)) {
						if (destination[sourceProperty]) {
							destination[sourceProperty] = destination[sourceProperty].concat(source[sourceProperty]);
						} else {
							destination[sourceProperty] = source[sourceProperty];
						}
						destination[sourceProperty] = this.removeDupesFromArray(destination[sourceProperty]);
					}
				}

				return destination;
			},

			removeDupesFromArray: function(array) {
				return array.filter(function(item, pos) {
					return array.indexOf(item) === pos;
				});
			}

		};
	});

	app.factory('ieUtilities', ['$timeout', function($timeout) {
		/*
		FIXME: Remove when the BMS is using a version of Vaadin above 7.2.x which supports native IE10 and IE11.
		This is a workaround for not being able to hide the x clear button in inputs on IE10 and IE11 because
		Vaadin forces IE9 compatibility mode.
		Since the x cannot be removed (https://connect.microsoft.com/IE/feedback/details/783743), instead
		we just make it act in the same way as the x that we have added. This does not happen by default
		due to an angular bug: https://github.com/angular/angular.js/issues/11193
		*/
		return {

			/*
			Takes an element which has the child input element that will be cleared by clicking the x, and the callback to
			execute when the x is clicked.
			*/
			addIeClearInputHandler: function(element, callback) {

				element.bind('mouseup', function() {
					var input = element.find('input'),
						value = input.val();

					if (value === '') {
						return;
					}

					// When this event is fired after clicking on the clear button the value is not cleared yet.
					// We have to wait for it.
					$timeout(function() {
						var newValue = input.val();

						if (newValue === '') {
							callback();
						}
					}, 1);

				});
			}
		};
	}]);

	app.filter('ifNumericOrderBy', function() {
		return function(items, field) {
			var someItemsNotNumbers;

			if (!items) {
				return items;
			}

			someItemsNotNumbers = items.some(function(item) {
				return isNaN(item[field]) || item[field] === '';
			});

			if (someItemsNotNumbers) {
				return items;
			}

			items.sort(function(a, b) {
				return Number(a[field]) - Number(b[field]);
			});
			return items;
		};
	});

	app.service('helpLinkService',['$http',function ($http) {
		return {
			helpLink: function (value) {
				var config = {responseType: 'text', observe: 'response'};
				return $http({
					method: 'GET',
					url: '/ibpworkbench/controller/help/getUrl/' + value,
					responseType: 'text',
					transformResponse: undefined
				}).then(function (response) {
					return response.data;
				});
			}
		};
	}]);

}());
