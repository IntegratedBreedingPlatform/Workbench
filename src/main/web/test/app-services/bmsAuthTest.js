/*global expect, inject, spyOn*/
'use strict';

describe('BMS Auth Module Tests', function() {

	beforeEach(module('bmsAuth'));

	describe('Auth Interceptor Test', function() {

		var authInterceptor, localStorageService;

		beforeEach(function() {
			inject(function(_authInterceptor_, _localStorageService_) {
				authInterceptor = _authInterceptor_;
				localStorageService = _localStorageService_;
			});
			spyOn(localStorageService, 'get');
		});

		it('Should add x-auth-token to the header when present in local storage and is not expired.', function() {
			var config = {};

			var oneMinuteFromNow = new Date().getTime() + 1000 * 60;
			var testToken = { token: 'naymesh:1447734506586:3a7e599e28efc35a2d53e62715ffd3cb', expires: oneMinuteFromNow };

			localStorageService.get.and.callFake(function() {
				return testToken;
			});

			authInterceptor.request(config);
			expect(localStorageService.get).toHaveBeenCalledWith('xAuthToken');
			expect(config.headers['x-auth-token']).toBeDefined();
			expect(config.headers['x-auth-token']).toBe(testToken.token);
		});

		it('Should not add x-auth-token to the header when present in local storage but is expired.', function() {
			var config = {};

			var oneMinuteAgo = new Date().getTime() - 1000 * 60;
			var testToken = { token: 'naymesh:1447734506586:3a7e599e28efc35a2d53e62715ffd3cb', expires: oneMinuteAgo };

			localStorageService.get.and.callFake(function() {
				return testToken;
			});

			authInterceptor.request(config);
			expect(localStorageService.get).toHaveBeenCalledWith('xAuthToken');
			expect(config.headers['x-auth-token']).toBeUndefined();
		});

		it('Should not add x-auth-token to the header when it is not present in local storage.', function() {
			var config = {};

			localStorageService.get.and.callFake(function() {
				return null;
			});

			authInterceptor.request(config);
			expect(localStorageService.get).toHaveBeenCalledWith('xAuthToken');
			expect(config.headers['x-auth-token']).toBeUndefined();
		});
	});

	describe('Auth Expired Interceptor Tests', function() {

		var authExpiredInterceptor, localStorageService, q, rootScope;

		beforeEach(function() {
			inject(function(_authExpiredInterceptor_, _localStorageService_, $q, $rootScope) {
				authExpiredInterceptor = _authExpiredInterceptor_;
				localStorageService = _localStorageService_;
				q = $q;
				rootScope = $rootScope;
			});
			spyOn(localStorageService, 'remove');
			spyOn(q, 'reject');
			spyOn(rootScope, '$broadcast');
		});

		it('Should remove x-auth-token from local storage and call re-authentication service when response status code is 401 ' +
		'with error Unauthorized', function() {

			var response = { status: 401, data: { error: 'Unauthorized'} };

			authExpiredInterceptor.responseError(response);
			expect(localStorageService.remove).toHaveBeenCalledWith('xAuthToken');
			expect(rootScope.$broadcast).toHaveBeenCalledWith('authenticationError');
			expect(q.reject).toHaveBeenCalledWith(response);
		});

		it('Should remove x-auth-token from local storage and call re-authentication service when response status code is 401 ' +
		'with error invalid_token', function() {

			var response = { status: 401, data: { error: 'invalid_token'} };

			authExpiredInterceptor.responseError(response);
			expect(localStorageService.remove).toHaveBeenCalledWith('xAuthToken');
			expect(rootScope.$broadcast).toHaveBeenCalledWith('authenticationError');
			expect(q.reject).toHaveBeenCalledWith(response);
		});

		it('Should not remove x-auth-token from local storage or call re-authentication service when error is not related to ' +
		'authentication.', function() {

			var response = { status: 500, data: { error: 'Some error that is not related to authentication.'} };

			authExpiredInterceptor.responseError(response);
			expect(localStorageService.remove).not.toHaveBeenCalled();
			expect(rootScope.$broadcast).not.toHaveBeenCalledWith('authenticationError');
			expect(q.reject).toHaveBeenCalledWith(response);
		});
	});

});
