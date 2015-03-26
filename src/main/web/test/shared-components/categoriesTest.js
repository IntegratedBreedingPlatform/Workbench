/*global angular, inject, expect*/
'use strict';

describe('Categories module', function() {
	var fakeEvent = {
			preventDefault: function(){}
		},

		scope,
		isolateScope,
		directiveElement,
		mockTranslateFilter;

	beforeEach(function() {
		module(function($provide) {
			$provide.value('translateFilter', mockTranslateFilter);
		});

		mockTranslateFilter = function(value) {
			return value;
		};
	});

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
			directiveElement = $compile('<om-categories ng-model="model" om-property="validValues"></om-categories>')(scope);
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

		it('should not remove the category if there is only 1 category left in the list', function() {

			var cat1 = {
					label: 'a',
					value: 'value a'
				};

			scope.model = {
				validValues: {
					categories: [cat1]
				}
			};

			compileDirective();

			isolateScope.removeCategory(fakeEvent, cat1.label);

			expect(scope.model.validValues.categories.length).toEqual(1);
		});
	});

	describe('Categories validation', function() {

		function compileForm(extraAttrs) {
			var attrs = extraAttrs || '';

			inject(function($compile) {
				directiveElement = $compile(
					'<form name="testForm" novalidate>' +
						'<om-categories name="omCategories" ng-model="model" om-property="validValues" ' + attrs + '></om-categories>' +
					'</form>'
					)(scope);
			});

			scope.$digest();
		}

		it('should set the widget to be valid if the selected data type is not categorical', function() {

			compileForm('om-categorical="false"');

			expect(scope.testForm.$valid).toBe(true);
		});

		it('should set the emptyValue error to be true if there is a category with no name', function() {

			scope.model = {
				validValues: {
					categories: [{
						description: 'description but no name'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				emptyValue: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the emptyValue error to be true if there is an empty description', function() {

			scope.model = {
				validValues: {
					categories: [{
						name: 'name but no description'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				emptyValue: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the nonUniqueName error to be true if there are two categories with the same name', function() {

			scope.model = {
				validValues: {
					categories: [{
						name: 'name',
						description: 'description 1'
					},
					{
						name: 'name',
						description: 'description 2'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				nonUniqueName: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});

		it('should set the widget to be invalid if there are two categories with the same description', function() {

			scope.model = {
				validValues: {
					categories: [{
						name: 'name 1',
						description: 'description'
					},
					{
						name: 'name 2',
						description: 'description'
					}]
				}
			};

			compileForm('om-categorical="true"');

			expect(scope.testForm.omCategories.$error).toEqual({
				nonUniqueValue: true
			});
			expect(scope.testForm.$valid).toBe(false);
		});
	});
});
