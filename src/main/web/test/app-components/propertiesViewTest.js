/*global angular, expect, inject, spyOn*/
'use strict';

describe('Properties View', function() {
	var BLAST = {
			name: 'Blast',
			description: 'I\'ts a blast',
			classes: ['class', 'anotherClass'],
			cropOntologyId: 'CO_192791864',
			metadata: {
				editableFields: ['name', 'description', 'classes', 'cropOntologyId'],
				deletable: true
			}
		},
		q,
		controller,
		scope,
		timeout,
		deferredGetProperties,
		deferredGetProperty,
		propertiesService,
		panelService;

	beforeEach(function() {
		module('propertiesView');
	});

	beforeEach(inject(function($q, $controller, $rootScope, $timeout) {
		propertiesService = {
			getProperties: function() {
				deferredGetProperties = q.defer();
				return deferredGetProperties.promise;
			},
			getProperty: function() {
				deferredGetProperty = q.defer();
				return deferredGetProperty.promise;
			}
		};

		panelService = {
			showPanel: function() {}
		};

		spyOn(propertiesService, 'getProperties').and.callThrough();
		spyOn(propertiesService, 'getProperty').and.callThrough();
		spyOn(panelService, 'showPanel');

		q = $q;
		scope = $rootScope;
		timeout = $timeout;
		controller = $controller('PropertiesController', {
			$scope: scope,
			propertiesService: propertiesService,
			panelService: panelService
		});
	}));

	it('should transform properties into display format', function() {
		var jsonData = [{
				id: 'prop1',
				name: 'prop1',
				description: 'prop1 description',
				classes: ['class1', 'class2']
			}],
			transformedData = [{
				id: 'prop1',
				name: 'prop1',
				description: 'prop1 description',
				classes: 'class1, class2'
			}];

		deferredGetProperties.resolve(jsonData);
		scope.$apply();
		expect(propertiesService.getProperties).toHaveBeenCalled();
		expect(controller.properties).toEqual(transformedData);
	});

	it('should add an empty description when transforming property into display format if one is missing', function() {
		var jsonData = [{
				id: 'prop1',
				name: 'prop1',
				classes: ['class1', 'class2']
			}],
			transformedData = [{
				id: 'prop1',
				name: 'prop1',
				description: '',
				classes: 'class1, class2'
			}];

		deferredGetProperties.resolve(jsonData);
		scope.$apply();
		expect(propertiesService.getProperties).toHaveBeenCalled();
		expect(controller.properties).toEqual(transformedData);
	});

	it('should show a message if there are no properties returned', function() {
		var jsonData = [];

		deferredGetProperties.resolve(jsonData);
		scope.$apply();

		expect(propertiesService.getProperties).toHaveBeenCalled();
		expect(controller.showNoItemsMessage).toBe(true);
	});

	it('should show an error message if there was an error while returning a list of properties from the server', function() {
		deferredGetProperties.reject();
		scope.$apply();

		expect(controller.problemGettingList).toBe(true);
		expect(controller.showThrobberWrapper).toBe(false);
	});

	it('should set the selected item to be an object with an id property set to null by default', function() {
		expect(scope.selectedItem).toEqual({id: null});
	});

	it('should set the selected property to be null by default', function() {
		expect(scope.selectedProperty).toEqual(null);
	});

	it('should show the throbber after a delay', function() {
		timeout.flush();
		expect(controller.showThrobber).toBe(true);
	});

	describe('$scope.showPropertyDetails', function() {

		it('should set the selected property to null before retrieving the selected property', function() {

			var selectedId = 123,
				panelName = 'properties',
				property = BLAST;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showPropertyDetails();

			expect(scope.selectedProperty).toEqual(null);

			deferredGetProperty.resolve(property);
			scope.$apply();

			expect(scope.selectedProperty).toEqual(property);
		});

		it('should retrieve the selected property and display the panel', function() {

			var selectedId = 123,
				panelName = 'properties',
				property = BLAST;

			scope.selectedItem.id = selectedId;
			scope.panelName = panelName;

			scope.showPropertyDetails();
			deferredGetProperty.resolve(property);
			scope.$apply();

			expect(propertiesService.getProperty).toHaveBeenCalledWith(selectedId);
			expect(scope.selectedProperty).toEqual(property);
			expect(panelService.showPanel).toHaveBeenCalledWith(panelName);
		});
	});

	describe('$scope.updateSelectedProperty', function() {

		it('should sync the updated property in the properties list', function() {

			var propertyToUpdate = angular.copy(BLAST),
				newName = 'Not Blast';

			controller.properties = [{
				id: 1,
				name: propertyToUpdate.name,
				classes: propertyToUpdate.classes.join(', ')
			}];

			// Select our property for editing
			scope.selectedItem.id = 1;

			// "Update" our property
			propertyToUpdate.name = newName;

			scope.updateSelectedProperty(propertyToUpdate);

			expect(controller.properties[0].name).toEqual(newName);
		});

		it('should remove the updated property in the properties list if the property is undefined', function() {

			var id = 1;

			controller.properties = [{
				id: id,
				name: BLAST.name,
				classes: BLAST.classes.join(', ')
			}];

			// Select our variable for editing
			scope.selectedItem.id = id;

			// "Delete" our variable
			scope.updateSelectedProperty();

			expect(controller.properties.length).toEqual(0);
		});

		it('should only update the property in the properties list matched by id', function() {

			var detailedPropertyToUpdate = angular.copy(BLAST),

				displayPropertyToLeaveAlone = {
					id: 2,
					name: 'Another Property',
					classes: 'No classes here'
				},

				displayPropertyToUpdate = {
					id: 1,
					name: detailedPropertyToUpdate.name,
					classes: detailedPropertyToUpdate.classes.join(', ')
				},

				newName = 'Not Blast';

			controller.properties = [displayPropertyToLeaveAlone, displayPropertyToUpdate];

			// Select our property for editing
			scope.selectedItem.id = 1;

			// "Update" our property
			detailedPropertyToUpdate.name = newName;

			scope.updateSelectedProperty(detailedPropertyToUpdate);

			// Ensure non-matching property was left alone
			expect(controller.properties[0]).toEqual(displayPropertyToLeaveAlone);
		});

		it('should not update any properties if there is no property in the list with a matching id', function() {

			var propertyToUpdate = angular.copy(BLAST),

				nonMatchingProperty = {
					id: 1,
					name: 'Non Matching Property',
					classes: 'No classes here'
				},

				anotherNonMatchingProperty = {
					id: 2,
					name: 'Another Non Matching Property',
					classes: 'No classes here'
				};

			controller.properties = [nonMatchingProperty, anotherNonMatchingProperty];

			// Select a property not in the list (shouldn't happen, really)
			scope.selectedItem.id = 3;

			scope.updateSelectedProperty(propertyToUpdate);

			// Ensure no updates happened
			expect(controller.properties[0]).toEqual(nonMatchingProperty);
			expect(controller.properties[1]).toEqual(anotherNonMatchingProperty);
		});
	});
});
