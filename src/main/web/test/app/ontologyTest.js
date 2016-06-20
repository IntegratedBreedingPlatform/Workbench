/*global expect, inject, spyOn*/
'use strict';

describe('Ontology Controller', function() {

	var fakeEvent = {
			preventDefault: function() {}
		},
		doNothingFunction =  function() {},
		controller,
		deferred,
		httpBackend,
		location,
		panelService,
		q,
		scope,
		timeout,
		window;

	beforeEach(module('ontology'));

	beforeEach(inject(function($rootScope, $location, $controller, $q, $window, $timeout, $httpBackend) {
		panelService = {
			hidePanel: function() {
				deferred = q.defer();
				return deferred.promise;
			},
			showPanel: function() {
				deferred = q.defer();
				return deferred.promise;
			}
		};

		controller = $controller('OntologyController', {
			$scope: $rootScope,
			$location: $location,
			$window: $window,
			panelService: panelService
		});

		location = $location;
		q = $q;
		scope = $rootScope;
		window = $window;
		timeout = $timeout;
		httpBackend = $httpBackend;
	}));

	it('should store the previous location when the location changes', function() {
		var oldUrl = '/oldUrl',
			newUrl = '/newUrl';

		expect(scope.previousUrl).toBeUndefined();
		scope.$broadcast('$locationChangeStart', newUrl, oldUrl);
		expect(scope.previousUrl).toEqual(oldUrl);
		scope.$broadcast('$locationChangeStart', '/newNewUrl', newUrl);
		expect(scope.previousUrl).toEqual(newUrl);
	});

	it('should set the active tab if the location changes to one of our known tab urls', function() {
		var oldUrl = '/variables',
			newUrl = '/methods';

		expect(scope.activeTab).toEqual('variables');
		scope.$broadcast('$locationChangeStart', newUrl, oldUrl);
		expect(scope.activeTab).toEqual('methods');
	});

	describe('addNew', function() {
		it('should route and close the panel', function() {
			var path = 'myPath';

			spyOn(location, 'path').and.callThrough();
			spyOn(panelService, 'hidePanel').and.callThrough();

			scope.addNew(fakeEvent, path);

			expect(location.path).toHaveBeenCalledWith('/add/' + path);
			expect(panelService.hidePanel).toHaveBeenCalled();
		});
	});

	describe('addNewSelection', function() {
		it('should show the panel', function() {

			spyOn(panelService, 'showPanel').and.callThrough();
			scope.addNewSelection();
			expect(panelService.showPanel).toHaveBeenCalledWith(scope.panelName);
		});
	});

	describe('setAsActive', function() {
		it('should set the active tab', function() {
			var testValue = 'value';
			scope.setAsActive(testValue);

			expect(scope.activeTab).toEqual(testValue);
		});
	});

	describe('redirectToLoginPage', function() {
		it('should redirect to login page if not in frame', function() {
			var fakeWindow = {
					location: 'mypage',
					parent: {location: 'mypage'},
					top: {
						location: {href: 'thisShouldChange'}
					}
				},
				fakeDocument = {
					referrer: 'http://localhost:8080/topTestPage/nestedTestPage',
					location: {href: 'http://localhost:8080/topTestPage/nestedTestPage'}
				};
			scope.redirectToLoginPage(fakeWindow, fakeDocument);
			expect(fakeWindow.top.location.href).toBe('http://localhost:8080/ibpworkbench/logout');
		});
		it('should redirect to login page in frame', function() {
			var fakeWindow = {
					location: 'mypage1',
					parent: {location: 'mypage2'},
					top: {
						location: {href: 'thisShouldChange'}
					}
				},
				fakeDocument = {
					referrer: 'http://localhost:8080/topTestPage/nestedTestPage',
					location: {href: 'http://localhost:8080/topTestPage/nestedTestPage'}
				};
			scope.redirectToLoginPage(fakeWindow, fakeDocument);
			expect(fakeWindow.top.location.href).toBe('http://localhost:8080/ibpworkbench/logout');
		});
	});

	it('should dislay the error message when the the authentication error was returned from the server', function() {
		spyOn(scope, 'redirectToLoginPage').and.callFake(doNothingFunction);
		expect(scope.hasAuthError).toBe(false);
		scope.$broadcast('authenticationError');
		expect(scope.hasAuthError).toBe(true);
	});
});
