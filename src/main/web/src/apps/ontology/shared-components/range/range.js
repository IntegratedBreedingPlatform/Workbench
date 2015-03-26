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
					ctrl.$setValidity('mustProvideBoth', true);
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

					if (!scope.numeric) {
						return;
					}

					if (data.max && !data.min || !data.max && data.min) {
						ctrl.$setValidity('mustProvideBoth', false);
					}

					if (data.max && data.min && data.max <= data.min) {
						ctrl.$setValidity('minTooBig', false);
					}

					if (scope.min && data.min && data.min < scope.min || data.min > scope.max) {
						ctrl.$setValidity('minOutOfRange', false);
					}

					if (scope.max && data.max && data.max > scope.max || data.max < scope.min) {
						ctrl.$setValidity('maxOutOfRange', false);
					}

				}, true);
			},
			require: 'ngModel',
			restrict: 'E',
			scope: {
				min: '@omMin',
				max: '@omMax',
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
