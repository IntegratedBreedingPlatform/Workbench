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

	function isLoginDisplayed() {
		return !$loginForm.hasClass(createAccount);
	}

	function toggleLoginCreateAccount() {
		var loginDisplayed = isLoginDisplayed();

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

	function validateSignInInputs() {
		var isUsernameEmpty = !$username.val(),
			errorMessage;

		if (isUsernameEmpty) {
			$username.addClass(validationError);
			errorMessage = 'Please provide a username.';
		}
		if (!$password.val()) {
			$password.addClass(validationError);
			errorMessage = isUsernameEmpty ? 'Please provide a username and password.' : 'Please provide a password.';
		}

		return errorMessage;
	}

	function validateCreateAccountInputs() {
		var errorMessage;

		// Add validation error styling to all empty inputs
		$('.js-login-form input').each(function(index, input) {
			var $input = $(input);
			$input.toggleClass(validationError, !$input.val());
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
			$error.text(errorMessage);
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

	// If browser supports placeholder attribute
	if ($.fn.placeholder.input) {
		$username.focus();
	}

}());
