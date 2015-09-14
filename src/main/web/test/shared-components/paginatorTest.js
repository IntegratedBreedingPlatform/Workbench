/*global angular, inject, expect, spyOn*/
'use strict';

describe('Paginator module', function() {

	describe('paginator filters', function() {
		var scope;

		beforeEach(function() {
			angular.mock.module('templates');
			module('paginator');
		});

		beforeEach(inject(function($rootScope) {
			scope = $rootScope;
		}));

		describe('paginate filter', function() {
			var $filter,
				paginatorService;

			beforeEach(function() {
				inject(function(_$filter_, _paginatorService_) {
					$filter = _$filter_;
					paginatorService = _paginatorService_;
				});
			});

			it('should return back the input when the input is falsey', function() {
				var testData,
					result = $filter('paginate')(testData, -1);

				expect(result).toEqual(testData);
			});

			it('should return back the input when the rowsPerPage is falsey', function() {
				var testData = [1, 2, 3],
					result = $filter('paginate')(testData, null);

				expect(result).toEqual(testData);
			});

			it('should return back the original array when the number of rows is -1', function() {
				var testData = [1, 2, 3],
					result = $filter('paginate')(testData, -1);

				expect(result).toEqual(testData);
			});

			it('should return back a subset of the array if the array is larger than the number of rows', function() {
				var result = $filter('paginate')([1, 2, 3], 2);

				expect(result).toEqual([1, 2]);
			});

			it('should return a subset that is determined from the page stored in the paginator service', function() {
				var result;

				paginatorService.page = 1;
				result = $filter('paginate')([1, 2, 3], 2);

				expect(result).toEqual([3]);
			});

			it('should set the page back to the first page if there are no items on the page', function() {
				var result;

				paginatorService.page = 2;
				result = $filter('paginate')([1, 2, 3], 2);

				expect(paginatorService.page).toEqual(0);
			});

		});

		describe('getPageNumbers filter', function() {
			var $filter;

			beforeEach(function() {
				inject(function(_$filter_) {
					$filter = _$filter_;
				});
			});

			it('should return back an array containing numbers between the start and end including the start', function() {
				var result = $filter('getPageNumbers')([], 2, 6);

				expect(result).toEqual([2, 3, 4, 5]);
			});
		});
	});

	describe('paginator service', function() {
		var scope,
			paginatorService;

		beforeEach(function() {
			angular.mock.module('templates');
		});

		beforeEach(module('paginator'));

		beforeEach(inject(function($rootScope) {
			scope = $rootScope;
		}));

		beforeEach(function() {
			inject(function(_paginatorService_) {
				paginatorService = _paginatorService_;
			});
		});

		it('should initially point to the first page', function() {
			expect(paginatorService.page).toBe(0);
		});

		it('should default to 50 rows per page', function() {
			expect(paginatorService.rowsPerPage).toBe(50);
		});

		describe('setPage', function() {

			it('should not change the page if the requested page is greater than the total number of pages', function() {
				spyOn(paginatorService, 'pageCount').and.returnValue('1');
				paginatorService.setPage(2);
				expect(paginatorService.page).toBe(0);
			});

			it('should change the page if the requested page is less than the total number of pages', function() {
				spyOn(paginatorService, 'pageCount').and.returnValue('5');
				paginatorService.setPage(2);
				expect(paginatorService.page).toBe(2);
			});
		});

		describe('nextPage', function() {

			it('should not change the page if already at the last page', function() {
				spyOn(paginatorService, 'isLastPage').and.returnValue(true);
				paginatorService.nextPage();
				expect(paginatorService.page).toBe(0);
			});

			it('should increase the page by one if not at the last page', function() {
				spyOn(paginatorService, 'isLastPage').and.returnValue(false);
				paginatorService.nextPage();
				expect(paginatorService.page).toBe(1);
			});
		});

		describe('previousPage', function() {

			it('should not change the page if already at the first page', function() {
				spyOn(paginatorService, 'isFirstPage').and.returnValue(true);
				paginatorService.previousPage();
				expect(paginatorService.page).toBe(0);
			});

			it('should decrease the page by one if not at the first page', function() {
				spyOn(paginatorService, 'isFirstPage').and.returnValue(false);
				paginatorService.previousPage();
				expect(paginatorService.page).toBe(-1);
			});
		});

		describe('firstPage', function() {

			it('should set the page to the first page', function() {
				paginatorService.page = 5;
				paginatorService.firstPage();
				expect(paginatorService.page).toBe(0);
			});
		});

		describe('lastPage', function() {

			it('should set the page to the last page', function() {
				spyOn(paginatorService, 'pageCount').and.returnValue(6);
				paginatorService.lastPage();
				expect(paginatorService.page).toBe(5);
			});
		});

		describe('isFirstPage', function() {

			it('should return true if currently on the first page', function() {
				expect(paginatorService.isFirstPage()).toBe(true);
			});

			it('should return false if not currently on the first page', function() {
				paginatorService.page = 5;
				expect(paginatorService.isFirstPage()).toBe(false);
			});
		});

		describe('isLastPage', function() {

			it('should return true if currently on the last page', function() {
				spyOn(paginatorService, 'pageCount').and.returnValue(1);
				expect(paginatorService.isLastPage()).toBe(true);
			});

			it('should return false if not currently on the first page', function() {
				spyOn(paginatorService, 'pageCount').and.returnValue(6);
				expect(paginatorService.isLastPage()).toBe(false);
			});
		});

		describe('pageCount', function() {

			it('should return the number of pages based on the number of items and row count, rounded up', function() {
				paginatorService.itemCount = 16;
				paginatorService.rowsPerPage = 5;
				expect(paginatorService.pageCount()).toBe(4);
			});
		});

		describe('lowerLimit', function() {

			it('should return 0 if the lowest page number to show is less than 0', function() {
				spyOn(paginatorService, 'pageCount').and.returnValue(4);
				expect(paginatorService.lowerLimit()).toBe(0);
			});

			it('should return the last page no minus the no of pages to make available if the current page is in the last few pages',
				function() {
					spyOn(paginatorService, 'pageCount').and.returnValue(21);
					paginatorService.page = 19;
					expect(paginatorService.lowerLimit()).toBe(16);
				}
			);

			it('should return the lowest page no where the current page is in the middle of the range determined by the limit per page',
				function() {
					spyOn(paginatorService, 'pageCount').and.returnValue(21);
					paginatorService.page = 10;
					expect(paginatorService.lowerLimit()).toBe(8);
				}
			);
		});
	});

	describe('paginator directive', function() {
		var scope,
			directiveElement;

		function compileDirective() {

			inject(function($compile) {
				directiveElement = $compile('<om-paginator></om-paginator>')(scope);
			});

			scope.$digest();
		}

		beforeEach(function() {
			angular.mock.module('templates');
		});

		beforeEach(module('paginator', function($provide) {
			// Provide mock for the directive controller
			$provide.value('paginatorService', {page: 0});
		}));

		beforeEach(inject(function($rootScope) {
			scope = $rootScope;
		}));

		it('should make the paginatorService available to the template', function() {
			compileDirective();
			expect(scope.paginator).toEqual({page: 0});
		});

	});

});
