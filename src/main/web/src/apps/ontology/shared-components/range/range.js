/*global angular*/
'use strict';

(function() {
	var rangeModule = angular.module('range', ['formFields']);

	rangeModule.directive('omRange', function(editable) {
		return {
			controller: function($scope) {
				// If the specified property does not exist on the model, add it
				if ($scope.model) {
					$scope.model[$scope.property] = $scope.model[$scope.property] || {};
				}
				$scope.editable = editable($scope);
			},
			link: function(scope, elm, attrs, ctrl) {

				var resetValidity = function() {
					ctrl.$setValidity('minTooBig', true);
					ctrl.$setValidity('minOutOfRange', true);
					ctrl.$setValidity('maxOutOfRange', true);
				};

				scope.$watch('numeric', function (numeric) {
					if (!numeric) {
						resetValidity();
					}
				});

				scope.$watch('model[property]', function (data) {
					resetValidity();

					if (!scope.numeric || !data) {
						return;
					}

					// Minimum must be less than the maximum
					if (data.max && data.min && data.max <= data.min) {
						ctrl.$setValidity('minTooBig', false);
					}

					if (data.min) {
						// If there is a minimum valid value specified, ensure minimum is not lower than this
						// If there is a maximum valid value specified, ensure minimum is not higher than this
						if ((scope.min && data.min < scope.min) || (scope.max && data.min > scope.max)) {
							ctrl.$setValidity('minOutOfRange', false);
						}
					}

					if (data.max) {
						// If there is a minimum valid value specified, ensure maximum is not lower than this
						// If there is a maximum valid value specified, ensure maximum is not higher than this
						if ((scope.min && data.max < scope.min) || (scope.max && data.max > scope.max)) {
							ctrl.$setValidity('maxOutOfRange', false);
						}
					}

				}, true);
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
	});
}());
