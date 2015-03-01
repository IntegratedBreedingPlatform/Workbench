/*global expect, inject*/
'use strict';

describe('Add Property View', function() {
	var variableService = {
			saveVariable: function() {}
		},

		variablesService = {
			getTypes: function() {}
		},

		propertiesService = {
			getProperties: function() {}
		},

		methodsService = {
			getMethods: function() {}
		},

		scalesService = {
			getScales: function() {}
		},

		variableStateService = {
			updateInProgress: function() {},
			getVariableState: function() {},
			storeVariableState: function() {}
		},

		q,
		deferred = [],
		controller,
		location,
		scope;

	beforeEach(function() {
		module('addVariable');
	});

	function fakePromise() {
		return function() {
			var defer = q.defer();
			deferred.push(defer);
			return defer.promise;
		};
	}

	beforeEach(inject(function($q, $rootScope, $location, $controller) {

		q = $q;
		location = $location;
		scope = $rootScope;

		propertiesService.getProperties = fakePromise();
		methodsService.getMethods = fakePromise();
		scalesService.getScales = fakePromise();
		variablesService.getTypes = fakePromise();

		controller = $controller('AddVariableController', {
			$scope: $rootScope,
			$location: $location,
			variableService: variableService,
			variablesService: variablesService,
			propertiesService: propertiesService,
			methodsService: methodsService,
			scalesService: scalesService,
			variableStateService: variableStateService
		});

		deferred.forEach(function(d) {
			d.resolve();
		});

		scope.$apply();
	}));

	it('should hide the range widget by deafult', function() {
		expect(scope.showRangeWidget).toBe(false);
	});

	// To test

	// If a variable update is in progres..
	// 	- set $scope.variable = storedData.variable;
	//  - set $scope.data = storedData.scopeData;
	//  - don't call propertiesService.getProperties()
	//  - don't call methodsService.getMethods()
	//  - don't call scalesService.getScales()
	//  - don't call variablesService.getTypes()

	// If a variable update is not in progres..
	//  - call propertiesService.getProperties()
	//  - call methodsService.getMethods()
	//  - call scalesService.getScales()
	//  - call variablesService.getTypes()

	// If variable.scale.dataType.name changes to Numeric, show the range widget
	// If variable.scale.dataType.name changes to not Numeric, hide the range widget

	// $scope.saveVariable
	// - call variableService.saveVariable(variable);
	// - call reset on the variableStateService.reset();
	// - go to $location.path('/variables');

	// $scope.addNew
	// - call variableStateService.storeVariableState($scope.variable, $scope.data);
	// go to $location.path('/add/' + path);

});
