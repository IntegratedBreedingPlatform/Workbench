/*global angular*/
'use strict';

(function() {
	var variableSelectModule = angular.module('variableSelect', ['formFields', 'ngSanitize', 'ui.select']);

    /**
     * AngularJS default filter with the following expression:
     * "property in properties | filter: {name: $select.search, variableNames: $select.search}"
     * performs a AND between 'name: $select.search' and 'variableNames: $select.search'.
     * We want to perform a OR.
     */
    variableSelectModule.filter('propsFilter', function() {
        return function(items, props) {
            var out = [];

            if (angular.isArray(items)) {
                var keys = Object.keys(props);

                items.forEach(function(item) {
                    var itemMatches = false;

                    for (var i = 0; i < keys.length; i++) {
                        var prop = keys[i];
                        var text = props[prop].toLowerCase();
                        if (item[prop].toString().toLowerCase().indexOf(text) !== -1) {
                            itemMatches = true;
                            break;
                        }
                    }

                    if (itemMatches) {
                        out.push(item);
                    }
                });
            } else {
                // Let the output be the input untouched
                out = items;
            }

            return out;
        };
    });

	variableSelectModule.directive('omVariableSelect', [function() {
		return {
			controller: ['$scope','variablesService', function($scope, variablesService) {

                $scope.properties;
                $scope.selectedProperty = {};

				variablesService.getVariables().then(function(variables) {
					$scope.properties = transformToSelectVariableItems(variables);
				});

				function transformToSelectVariableItems(variables) {

					var tracker = [];
					var properties = [];

					variables.forEach(function(variable) {
						if (tracker.indexOf(variable.property.id) > -1) {
                            var property = properties[tracker.indexOf(variable.property.id)];
                            property.variableNames += ', ' +  variable.name;
                            property.variables.push(variable);
						} else {
                            tracker.push(variable.property.id);
                            properties.push({
                            	name: variable.property.name,
								classes: variable.property.classes.join(','),
								variableNames: variable.name,
								variables: [ variable ]
							});
						}
					});

                    return properties;

				};

                $scope.add = function(data) {
                    if ($scope.onAddClick) {
                        $scope.onAddClick({ variableName: data });
                    }
                };

			}],
			link: function(scope, elm, attrs, ctrl) {
                elm.addClass('om-variable');
			},
			restrict: 'E',
			scope: {
				name: '@omName',
				allowClear: '=omAllowClear',
                onAddClick: '&onAddClick'
			},
			templateUrl: 'static/views/ontology/variableSelect.html'

		};
	}]);
})();
