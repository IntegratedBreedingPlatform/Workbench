/*global expect, inject, spyOn*/
'use strict';

describe('test.variablesModuleTest', function() {
	var data = [{
			name: 'var1',
			description: 'var1 description'
		}],
		q,
		controller,
		scope,
		deferred,
		variablesService;

	beforeEach(function() {
		module('variables');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {
		variablesService = {
			getVariables: function() {
				deferred = q.defer();
				return deferred.promise;
			}
		};
		spyOn(variablesService, 'getVariables').and.callThrough();

		q = $q;
		scope = $rootScope;
		controller = $controller('VariablesController', {
			variablesService: variablesService
		});
	}));

	it('should retrieve variables from the variablesService', function() {
		deferred.resolve(data);
		scope.$apply();
		expect(variablesService.getVariables).toHaveBeenCalled();
		expect(controller.variables).toEqual(data);
	});

});
