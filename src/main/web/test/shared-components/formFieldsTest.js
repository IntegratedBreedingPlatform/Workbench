/*global expect, inject*/
'use strict';

describe('formFields Module', function() {
	var editable;

	beforeEach(function() {
		module('formFields');
	});

	beforeEach(function() {
		inject(function(_editable_) {
			editable = _editable_;
		});
	});

	describe('editable', function() {

		it('should return a function', function() {
			var result = editable();
			expect(typeof result).toEqual('function');
		});

		it('should return a function whose result is falsy if $scope.editing is falsy',
			function() {

				var falseEditableScope = {
						editing: false
					},
					nullEditableScope = {
						editing: null
					};

				expect(editable(falseEditableScope)()).toBeFalsy();
				expect(editable(nullEditableScope)()).toBeFalsy();
			}
		);

		it('should return a function that is falsy if there is no $scope.model or if $scope.model has no editableFields property',
			function() {

				var noModelScope = {
						editing: true
					},
					noEditableFieldsScope = {
						editing: true
					};

				expect(editable(noModelScope)()).toBeFalsy();
				expect(editable(noEditableFieldsScope)()).toBeFalsy();
			}
		);

		it('should return a function that is falsy if the selected $scope.property is not in the editableFields list on the $scope.model',
			function() {

				var nonEditablePropertyScope = {
						editing: true,
						model: {
							editableFields: ['description']
						},
						property: 'name'
					},
					noPropertyScope = {
						editing: true,
						model: {
							editableFields: ['description']
						}
					};

				expect(editable(nonEditablePropertyScope)()).toBeFalsy();
				expect(editable(noPropertyScope)()).toBeFalsy();
			}
		);

		it('should return a function that is truthy if the scope and the selected property on the selected model are editable',
			function() {

				var editablePropertyScope = {
						editing: true,
						model: {
							editableFields: ['name']
						},
						property: 'name'
					};

				expect(editable(editablePropertyScope)()).toBeTruthy();
			}
		);
	});
});
