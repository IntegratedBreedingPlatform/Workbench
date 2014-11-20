/*global Modernizr*/

(function() {
	'use strict';

	var $check = $('.js-login-check'),
		$loginForm = $('.js-login-form'),
		$loginModeToggle = $('.js-login-mode-toggle'),
		$loginSubmit = $('.js-login-submit'),
		$error = $('.js-login-error'),
		$username = $('.js-login-username'),
		$password = $('.js-login-password'),

		createAccount = 'create-account',
		tick = 'fa fa-check',
		validationError = 'login-validation-error',

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

	function clearServerErrors() {
		$('.js-th-login-error').empty();
	}

	function clearClientErrors() {
		$('.' + validationError).removeClass(validationError);
		$error.empty();
	}

	function inputsValidate() {
		var isUsernameEmpty = !$username.val(),
			validates = !isUsernameEmpty,
			errorMessage;

		if (isUsernameEmpty) {
			$username.addClass(validationError);
			errorMessage = 'Please provide a username.';
		}
		if (!$password.val()) {
			$password.addClass(validationError);
			errorMessage = isUsernameEmpty ? 'Please provide a username and password.' : 'Please provide a password.';
			validates = false;
		}

		$error.text(errorMessage);

		return validates;
	}

	$('.js-login-checkbox-control').on('click', '.js-login-remember-me', function(e) {
		e.preventDefault();
		toggleCheckbox();
	});

	$('.js-login-form').on('submit', function(e) {
		e.preventDefault();
		clearServerErrors();

		// Perform validation on input fields
		if (inputsValidate()) {
			// Continue with form submit
			this.submit();
		}
	});

	$('.js-login-form').on('focusin', '.login-validation-error', function() {
		clearClientErrors();
	});

	$loginModeToggle.on('click', function(e) {
		e.preventDefault();
		clearClientErrors();
		clearServerErrors();
		toggleLoginCreateAccount();
	});

	// Record whether media queries are supported in this browser as a class
	if (!Modernizr.mq('only all')) {
		$('html').addClass('no-mq');
	}

	// Initialise placeholder polyfill plugin
	$('input, textarea').placeholder();
}());
