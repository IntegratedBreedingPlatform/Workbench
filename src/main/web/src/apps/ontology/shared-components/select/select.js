/*global angular*/
'use strict';

(function() {
	var selectModule = angular.module('select', ['formFields', 'ngSanitize', 'ui.select']);

	selectModule.directive('omSelect', ['editable', function(editable) {
		return {
			controller: ['$scope', function($scope) {
				$scope.editable = editable($scope);
				$scope.required = $scope.required || false;
			}],
			link: function(scope, elm, attrs, ctrl) {
				scope.$watch('model[property]', function(data) {
					ctrl.$setValidity('required', true);

					if (scope.required && !data) {
						ctrl.$setValidity('required', false);
					}
				});

				scope.showModelPanel = function (event) {
					scope.showDetails({e: event});
				}
			},
			require: 'ngModel',
			restrict: 'E',
			scope: {
				// omOptions must be an array of objects with (at least) name and id properties.
				options: '=omOptions',
				name: '@omName',
				property: '@omProperty',
				adding: '=omAdding',
				editing: '=omEditing',
				model: '=ngModel',
				required: '=?omRequired',
				allowClear: '=omAllowClear',
				showDetails: '&'
			},
			templateUrl: 'static/views/ontology/select.html'
		};
	}]);
})();
