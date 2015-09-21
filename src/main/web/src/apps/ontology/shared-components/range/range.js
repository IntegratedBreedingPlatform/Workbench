/*global angular*/
'use strict';

(function() {
	var rangeModule = angular.module('range', ['formFields', 'utilities']);

	function isNumber(value) {
		return typeof value === 'number';
	}

	function validateValues(ctrl, range, scope) {

		var rangeForm = scope.rangeForm,

			// Whether or not the user has filled in the min and max
			minimumSpecified = isNumber(range.min),
			maximumSpecified = isNumber(range.max),

			// Whether or not om-min and om-max were passed to the directive, providing a range of valid values
			validMinProvided = isNumber(scope.min),
			validMaxProvided = isNumber(scope.max);

		if (minimumSpecified) {
			// If there is a minimum valid value specified, ensure minimum is not lower than this
			// If there is a maximum valid value specified, ensure minimum is not higher than this
			if ((validMinProvided && range.min < scope.min) || (validMaxProvided && range.min > scope.max)) {
				ctrl.$setValidity('minOutOfRange', false);
			}

			// Minimum must be less than the maximum
			if (maximumSpecified && range.max <= range.min) {
				ctrl.$setValidity('minTooBig', false);
			}
		}

		if (maximumSpecified) {
			// If there is a minimum valid value specified, ensure maximum is not lower than this
			// If there is a maximum valid value specified, ensure maximum is not higher than this
			if ((validMinProvided && range.max < scope.min) || (validMaxProvided && range.max > scope.max)) {
				ctrl.$setValidity('maxOutOfRange', false);
			}
		}

		if (rangeForm && rangeForm.omRangeMin) {
			ctrl.$setValidity('minNaN', !rangeForm.omRangeMin.$error.number);
		}

		if (rangeForm && rangeForm.omRangeMax) {
			ctrl.$setValidity('maxNaN', !rangeForm.omRangeMax.$error.number);
		}
	}

	/*
	Retrieve the translation key to use for the message that should be displayed when the range is read only.

	@param min a string containing the minimum value for the range
	@param max a string containing the maximum value for the range
	*/
	function getReadOnlyRangeText(min, max) {
		var isMinValued = !angular.isUndefined(min) && min != null,
			isMaxValued = !angular.isUndefined(max) && max != null;

		if (isMinValued && isMaxValued) {
			return 'range.fullRange';
		} else if (isMinValued) {
			return 'range.minOnly';
		} else if (isMaxValued) {
			return 'range.maxOnly';
		} else {
			return 'range.noRange';
		}
	}

	rangeModule.directive('omRange', ['formUtilities', 'editable', function(formUtilities, editable) {
		return {
			controller: function($scope) {
				// If the specified property does not exist on the model, add it
				if ($scope.model) {
					$scope.model[$scope.property] = $scope.model[$scope.property] || {};
				}
				$scope.editable = editable($scope);

				$scope.formParentHasError = formUtilities.formParentHasError($scope, 'rangeForm');
			},
			link: function(scope, elm, attrs, ctrl) {

				var resetValidity = function() {
					ctrl.$setValidity('minTooBig', true);
					ctrl.$setValidity('minOutOfRange', true);
					ctrl.$setValidity('maxOutOfRange', true);
					ctrl.$setValidity('minNaN', true);
					ctrl.$setValidity('maxNaN', true);
				};

				scope.$watch('numeric', function(numeric) {
					if (!numeric) {
						// Clear any previous values that were in the range widget when the range is no longer required
						scope.clearRange();
						resetValidity();
					} else if (scope.model && scope.model[scope.property]) {
						validateValues(ctrl, scope.model[scope.property], scope);
					}
				});

				scope.$watch('model[property]', function(data) {

					resetValidity();

					if (!scope.numeric || !data) {
						return;
					}

					// Set the read only text to reflect the given range
					scope.readOnlyRangeText = getReadOnlyRangeText(data.min, data.max);

					validateValues(ctrl, data, scope);
				}, true);

				scope.$watch('rangeForm.omRangeMin.$error.number', function(invalid) {
					ctrl.$setValidity('minNaN', !invalid);
				});

				scope.$watch('rangeForm.omRangeMax.$error.number', function(invalid) {
					ctrl.$setValidity('maxNaN', !invalid);
				});

				scope.$watch('rangeForm.omRangeMax.$touched', function(touched) {
					if (touched) {
						ctrl.$setTouched();
					}
				});

				scope.$watch('rangeForm.omRangeMin.$touched', function(touched) {
					if (touched) {
						ctrl.$setTouched();
					}
				});

				scope.clearRange = function() {

					if (scope.model && scope.model[scope.property]) {
						scope.model[scope.property] = {};
					}

					scope.readOnlyRangeText = getReadOnlyRangeText();
				};
			},
			require: 'ngModel',
			restrict: 'E',
			scope: {
				min: '=?omMin',
				max: '=?omMax',
				numeric: '=omNumeric',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=ngModel'
			},
			templateUrl: 'static/views/ontology/range.html'
		};
	}]);
}());
