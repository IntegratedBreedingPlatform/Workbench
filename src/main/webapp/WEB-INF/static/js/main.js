/*global Modernizr*/

(function() {
	'use strict';

	var $check = $('.js-login-check'),
		$loginForm = $('.js-login-form'),
		$loginModeToggle = $('.js-login-mode-toggle'),
		$loginSubmit = $('.js-login-submit'),

		createAccount = 'create-account',
		tick = 'fa fa-check',

		createAccountText = 'Create Account',
		signInText = 'Sign In';

	function toggleCheckbox() {
		if ($check.hasClass(tick)) {
			$check.removeClass(tick);
		} else {
			$check.addClass(tick);
		}
	}

	function toggleLoginCreateAccount() {
		var loginDisplayed = !$loginForm.hasClass(createAccount);

		$loginForm.toggleClass(createAccount, loginDisplayed);
		$loginModeToggle.text(loginDisplayed ?  signInText : createAccountText);
		$loginSubmit.text(loginDisplayed ? createAccountText : signInText);
	}

	$('.js-login-checkbox-control').on('click', '.js-login-remember-me', function(e) {
		e.preventDefault();
		toggleCheckbox();
	});

	$loginModeToggle.on('click', function(e) {
		e.preventDefault();
		toggleLoginCreateAccount();
	});

	// Record whether media queries are supported in this browser as a class
	if (!Modernizr.mq('only all')) {
		$('html').addClass('no-mq');
	}

	// Initialise placeholder polyfill plugin
	$('input, textarea').placeholder();
}());
