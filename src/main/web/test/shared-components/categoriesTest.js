/*global angular, inject, expect*/
'use strict';

describe('Categories module', function() {
	var fakeEvent = {
			preventDefault: function(){}
		},

		scope,
		isolateScope,
		directiveElement;

	beforeEach(function() {
		angular.mock.module('templates');
	});

	beforeEach(module('templates'));
	beforeEach(module('categories'));

	beforeEach(inject(function($rootScope) {
		scope = $rootScope.$new();
	}));

	function compileDirective() {
		inject(function($compile) {
			directiveElement = $compile('<om-categories om-model="model" om-property="validValues"></om-categories>')(scope);
		});

		scope.$digest();

		isolateScope = directiveElement.isolateScope();
	}

	it('should instantiate the specified property on the model if not otherwise provided', function() {

		scope.model = {};
		expect(scope.model.validValues).toBeUndefined();

		compileDirective();

		expect(scope.model.validValues).not.toBeUndefined();
	});

	describe('$scope.addCategory', function() {

		it('should add an empty category to the categories array on the scale object', function() {

			scope.model = {
				validValues: {}
			};

			compileDirective();

			isolateScope.addCategory(fakeEvent);

			expect(scope.model.validValues.categories.length).toEqual(2);
			expect(scope.model.validValues.categories[1]).toEqual({});
		});
	});

	describe('$scope.removeCategory', function() {

		it('should remove the category with the specified label', function() {

			var cat1 = {
					label: 'a',
					value: 'value a'
				},
				cat2 = {
					label: 'b',
					value: 'value b'
				};

			scope.model = {
				validValues: {
					categories: [cat1, cat2]
				}
			};

			compileDirective();

			isolateScope.removeCategory(fakeEvent, cat1.label);

			expect(scope.model.validValues.categories.length).toEqual(1);
			expect(scope.model.validValues.categories[0]).toEqual(cat2);
		});
	});
});
