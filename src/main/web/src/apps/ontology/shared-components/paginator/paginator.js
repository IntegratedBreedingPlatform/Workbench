/*global angular*/
'use strict';

/*
A client side pagination module adapted from Guido Krömer <mail@cacodaemon.de>
http://cacodaemon.de/index.php?id=50
*/
(function() {
	var paginatorModule = angular.module('paginator', []);

	paginatorModule.filter('paginate', ['paginatorService', function(paginatorService) {
		return function(input, rowsPerPage) {

			var page,
				result;

			// The rowsPerPage will be -1 when we don't want to apply this filter
			if (!input || rowsPerPage === -1) {
				return input;
			}

			if (rowsPerPage) {
				paginatorService.rowsPerPage = rowsPerPage;
			}

			paginatorService.itemCount = input.length;

			page = paginatorService.page;

			result = input.slice(page * rowsPerPage, (page + 1) * rowsPerPage);

			if (result.length === 0) {
				paginatorService.setPage(0);
			}

			return result;
		};
	}]);

	paginatorModule.filter('getPageNumbers', function() {
		return function(input, start, end) {
			input = [];

			for (var i = 0; start < end; start++, i++) {
				input[i] = start;
			}

			return input;
		};
	});

	paginatorModule.service('paginatorService', function() {
		this.page = 0;
		this.rowsPerPage = 50;
		this.itemCount = 0;
		this.limitPerPage = 5;

		this.setPage = function(page) {
			if (page > this.pageCount()) {
				return;
			}

			this.page = page;
		};

		this.nextPage = function() {
			if (this.isLastPage()) {
				return;
			}

			this.page++;
		};

		this.previousPage = function() {
			if (this.isFirstPage()) {
				return;
			}

			this.page--;
		};

		this.firstPage = function() {
			this.page = 0;
		};

		this.lastPage = function() {
			this.page = this.pageCount() - 1;
		};

		this.isFirstPage = function() {
			return this.page === 0;
		};

		this.isLastPage = function() {
			return this.page === this.pageCount() - 1;
		};

		this.pageCount = function() {
			var count = Math.ceil(this.itemCount / this.rowsPerPage);
			if (count === 1) {
				this.page = 0;
			}
			return count;
		};

		this.lowerLimit = function() {
			var pageCountLimitPerPageDiff = this.pageCount() - this.limitPerPage,
				low;

			if (pageCountLimitPerPageDiff < 0) {
				return 0;
			}

			if (this.page > pageCountLimitPerPageDiff + 1) {
				return pageCountLimitPerPageDiff;
			}

			low = this.page - (Math.ceil(this.limitPerPage / 2) - 1);

			return Math.max(low, 0);
		};

	});

	paginatorModule.directive('omPaginator', ['paginatorService', function(paginatorService) {
		return {
			restrict:'E',
			controller: function($scope) {
				$scope.paginator = paginatorService;
			},
			templateUrl: 'static/views/ontology/paginator.html'
		};
	}]);
}());
