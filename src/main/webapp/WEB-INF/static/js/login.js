/*global Modernizr*/

(function() {
	'use strict';

	var $check = $('.js-login-check'),
		$loginForm = $('.js-login-form'),
		$loginModeToggle = $('.js-login-mode-toggle'),
		$loginSubmit = $('.js-login-submit'),
		$error = $('.js-login-error'),
		$errorText = $('.js-login-error-text'),
		$username = $('.js-login-username'),
		$password = $('.js-login-password'),

		createAccount = 'login-create-account',
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

	function isLoginDisplayed() {
		return !$loginForm.hasClass(createAccount);
	}

	function toggleLoginCreateAccount() {
		var loginDisplayed = isLoginDisplayed();

		// Once we have the create account post, change the form action to be stored on a data attribute, and toggle the URL stored
		// when we toggle.

		$loginForm.toggleClass(createAccount, loginDisplayed);
		$loginModeToggle.text(loginDisplayed ?  signInText : createAccountText);
		$loginSubmit.text(loginDisplayed ? createAccountText : signInText);
	}

	function clearServerErrors() {
		$('.js-th-login-error').empty();
	}

	function clearClientErrors() {
		$('.' + validationError).removeClass(validationError);
		$errorText.empty();
		$error.addClass('login-valid');
	}

	function validateSignInInputs() {
		var isUsernameEmpty = !$username.val(),
			errorMessage;

		if (isUsernameEmpty) {
			$username.parent('.login-form-control').addClass(validationError);
			errorMessage = 'Please provide a username.';
		}
		if (!$password.val()) {
			$password.parent('.login-form-control').addClass(validationError);
			errorMessage = isUsernameEmpty ? 'Please provide a username and password.' : 'Please provide a password.';
		}

		return errorMessage;
	}

	function validateCreateAccountInputs() {
		var errorMessage;

		// Add validation error styling to all empty inputs
		$('.js-login-form .login-form-control input').each(function(index, input) {
			var $input = $(input);
			$input.parent('.login-form-control').toggleClass(validationError, !$input.val());
		});
		// Set the error message if there's a validation error
		if ($('.' + validationError).length > 0) {
			errorMessage = 'Please fill in all required fields.';
		}

		return errorMessage;
	}

	function inputsValidate() {
		var errorMessage = isLoginDisplayed() ? validateSignInInputs() : validateCreateAccountInputs();

		if (errorMessage) {
			$errorText.text(errorMessage);
			$error.removeClass('login-valid');
		}

		// If there is an error message returned then the inputs do not validate
		return !errorMessage;
	}

	$('.js-login-checkbox-control').on('click', '.js-login-remember-me', function(e) {
		e.preventDefault();
		toggleCheckbox();
	});

	$loginForm.on('submit', function(e) {
		e.preventDefault();
		clearServerErrors();

		// Perform validation on input fields
		if (inputsValidate()) {
			$loginSubmit.addClass('loading').delay(200);
			// Continue with form submit
			this.submit();
		}
	});

	$loginForm.on('focusin', '.login-validation-error', function() {
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
