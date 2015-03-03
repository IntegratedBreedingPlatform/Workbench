/*global expect, inject, spyOn*/
'use strict';

var CUT_AND_DRY = {
		name: 'Cut and Dry',
		description: 'Self explanatory really'
	},

	q,
	controller,
	scope,
	deferredGetMethods,
	deferredGetMethod,
	methodsService,
	panelService;

describe('Methods View', function() {

	beforeEach(function() {
		module('methodsView');
	});

	beforeEach(inject(function($q, $controller, $rootScope) {

		q = $q;
		scope = $rootScope;

		methodsService = {
			getMethod: function() {
				deferredGetMethod = q.defer();
				return deferredGetMethod.promise;
			},
			getMethods: function() {
				deferredGetMethods = q.defer();
				return deferredGetMethods.promise;
			}
		};

		panelService = {
			showPanel: function() {}
		};

		spyOn(methodsService, 'getMethods').and.callThrough();
		spyOn(methodsService, 'getMethod').and.callThrough();
		spyOn(panelService, 'showPanel');

		controller = $controller('MethodsController', {
			$scope: scope,
			methodsService: methodsService,
			panelService: panelService
		});
	}));

	it('should transform methods into display format', function() {
		var jsonData = [{
				id: 23,
				name: 'Cut and Dry',
				description: 'Cut the plant 10cm above the root and air dry in a shadey place.'
			}],
			transformedData = [{
				id: 23,
				Name: 'Cut and Dry',
				Description: 'Cut the plant 10cm above the root and air dry in a shadey place.'
			}];

		deferredGetMethods.resolve(jsonData);
		scope.$apply();
		expect(methodsService.getMethods).toHaveBeenCalled();
		expect(controller.methods).toEqual(transformedData);
	});

	it('should set the selected item to be an object with an id property set to null by default', function() {
		expect(scope.selectedItem).toEqual({id: null});
	});

	it('should set the selected method to be null by default', function() {
		expect(scope.selectedMethod).toEqual(null);
	});

	describe('$scope.showMethodDetails', function() {

		it('should retrieve the selected method and display the panel', function() {

			var selectedId = 123,
				panelName = 'methods',
				method = CUT_AND_DRY;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showMethodDetails();
			deferredGetMethod.resolve(method);
			scope.$apply();

			expect(methodsService.getMethod).toHaveBeenCalledWith(selectedId);
			expect(scope.selectedMethod).toEqual(method);
			expect(panelService.showPanel).toHaveBeenCalledWith(panelName);
		});
	});
});
