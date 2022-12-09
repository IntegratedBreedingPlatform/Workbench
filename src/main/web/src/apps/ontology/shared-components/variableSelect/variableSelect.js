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

					var properyTracker = [];
					var properties = [];

					variables.forEach(function(variable) {

						if(!variable.obsolete) {
							var variableType = function (element) {
								// $scope.variableTypeIds is a string of variable type ids separated by comma (e.g. 1808,1802,1806)
								return $scope.variableTypeIds.indexOf(element.id) >= 0;
							};

							if (variable.variableTypes && variable.variableTypes.some(variableType)) {
								if (properyTracker.indexOf(variable.property.id) > -1) {
									var property = properties[properyTracker.indexOf(variable.property.id)];
									property.variableNames += ', ' + variable.name + (variable.alias ? ' (' + variable.alias + ')' : '');
									property.variables.push(variable);
								} else {
									properyTracker.push(variable.property.id);
									properties.push({
										name: variable.property.name,
										classes: variable.property.classes.join(','),
										variableNames: variable.name + (variable.alias ? ' (' + variable.alias + ')' : ''),
										variables: [variable]
									});
								}
							}
						}
					});

                    return properties;

				};

				$scope.replaceVariableIdsWithNames = function (formula) {

                    if (formula.definition) {

                        var variableNames = [];
                        $(formula.inputs).each(function(index, inputVariable) {
                            variableNames[inputVariable.id] = inputVariable.name;
                        });

                        var result = formula.definition;

                        // matches any word (with alphanumeric characters, underscore , space and carriage return) enclosed with {{ and }}
                        var expression = /\{\{[\w\r\s_]*\}\}/g;
                        var matches = formula.definition.match(expression);

                        if (matches) {
                            $.each(matches, function(index, token) {
                                result =
                                    result.replace(token, '{{' + variableNames[token.match(/\d+/)] + '}}');
                            });
                        }

                        return result;

                    } else {
                        return formula.definition;
                    }
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
                variableTypeIds: '@omVariableTypeIds',
				allowClear: '=omAllowClear',
                onAddClick: '&onAddClick'
			},
			templateUrl: 'static/views/ontology/variableSelect.html'

		};
	}]);
})();
