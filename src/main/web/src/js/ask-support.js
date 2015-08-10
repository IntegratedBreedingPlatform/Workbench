/**
 * Created by cyrus on 7/21/15.
 *
 * TODO: follow OM scaffold after OM branch is merged
 */

/*global angular*/
(function() {
	'use strict';
	var app = angular.module('AskSupportApp', ['ngToast', 'ngUpload']);

	app.config(['ngToastProvider', function(ngToastProvider) {
		ngToastProvider.configure({
			horizontalPosition: 'center',
			animation: 'fade'
		});
	}]);

	app.controller('AskSupportFormController', ['$scope', 'ngToast', 'AskSupportService', function($scope, ngToast, AskSupportService) {
		$scope.form = AskSupportService.getAskSupportForm();
		$scope.requestCategories = AskSupportService.getRequestCategoryOpts();

		$scope.uploadComplete = function(content) {
			$scope.response = content;

			ngToast.create({
				className: 'ask-support-toast-submit',
				content: '<div class="col-xs-2"><span class="fa fa-thumbs-up ask-support-info-icon"></span></div>' +
				'<div class="col-xs-10">' +
				'<h2 class="ask-support-no-margin">Thank you for your feedback</h2>' +
				'<div>Your feedback has been sent to IBP</div>' +
				'</div>',
				dismissOnTimeout: false
			});
		};

		$scope.validate = function() {
			return !$scope.askSupportForm.$invalid;
		};
	}]);

	app.service('AskSupportService', function() {
		var requestCategoryOpts = [
			'Breeding Processes',
			'Data Management',
			'Templates',
			'Documentation',
			'Feature Improvement'
		];

		var askSupportForm = {
			name: null,
			email: null,
			summary: null,
			description: null,
			requestCategory: '0'
		};

		this.getAskSupportForm = function() {
			return askSupportForm;
		};

		this.getRequestCategoryOpts = function() {
			return requestCategoryOpts;
		};

	});

	app.directive('showErrors', function() {
		return {
			restrict: 'A',
			require:  '^form',
			link: function(scope, el, attrs, formCtrl) {
				// find the text box element, which has the 'name' attribute
				var inputEl   = el[0].querySelector('[name]');
				// convert the native text box element to an angular element
				var inputNgEl = angular.element(inputEl);
				// get the name on the text box so we know the property to check
				// on the form controller
				var inputName = inputNgEl.attr('name');

				// only apply the has-error class after the user leaves the text box
				inputNgEl.bind('blur', function() {
					el.toggleClass('has-error', formCtrl[inputName].$invalid);
				});

				scope.$on('show-errors-check-validity', function() {
					el.toggleClass('has-error', formCtrl[inputName].$invalid);
				});
			}
		};
	});

})();
