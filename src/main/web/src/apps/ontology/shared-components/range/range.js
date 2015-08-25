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

	function mapRangeModelToModel(rangeModel, model) {
		var isMinValued = !(angular.isUndefined(rangeModel.min) || rangeModel.min === null),
			isMaxValued = !(angular.isUndefined(rangeModel.max) || rangeModel.max === null);

		model.min = isMinValued ? rangeModel.min.toString() : '';
		model.max = isMaxValued ? rangeModel.max.toString() : '';
	}

	/*
	Retrieve the translation key to use for the message that should be displayed when the range is read only.

	@param min a string containing the minimum value for the range
	@param max a string containing the maximum value for the range
	*/
	function getReadOnlyRangeText(min, max) {
		if (min && max) {
			return 'range.fullRange';
		} else if (min) {
			return 'range.minOnly';
		} else if (max) {
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
					} else if (scope.rangeModel) {
						validateValues(ctrl, scope.rangeModel, scope);
					}
				});

				scope.$watch('model[property]', function(data) {
					var min,
						max;

					resetValidity();

					if (!scope.numeric || !data) {
						return;
					}

					// Set the read only text to reflect the given range
					scope.readOnlyRangeText = getReadOnlyRangeText(data.min, data.max);

					// Set a specific model to use in the template where the values are numbers rather than strings
					// so that we can provide the user a better experience with the number type inputs that have up and
					// down arrows for changing the values, rather than just a freetext input.
					min = parseInt(data.min, 10);
					max = parseInt(data.max, 10);

					// Set the numeric range model to have the model's value if it is a number, otherwise null
					scope.rangeModel = {
						min: !isNaN(min) ? min : null,
						max: !isNaN(max) ? max : null
					};

					mapRangeModelToModel(scope.rangeModel, data);

					validateValues(ctrl, scope.rangeModel, scope);
				}, true);

				scope.$watch('rangeModel', function(rangeModel) {

					if (rangeModel) {
						mapRangeModelToModel(rangeModel, scope.model[scope.property]);
					}
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
					// Clear the textual min/max that is provided to the directive
					if (scope.model && scope.model[scope.property]) {
						scope.model[scope.property].min = '';
						scope.model[scope.property].max = '';
					}

					// Clear the numeric min/max that we use for displaying the range in the numeric input
					if (scope.rangeModel) {
						scope.rangeModel.min = null;
						scope.rangeModel.max = null;
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
