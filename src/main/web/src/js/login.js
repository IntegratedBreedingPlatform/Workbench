/*global Modernizr*/

(function() {
	'use strict';

	var $checkButton = $('.js-login-check'),
		$checkInput = $('.js-login-checkbox-input'),
		$createAccountInputs = $('.js-login-create-account-input'),
		$loginForm = $('.js-login-form'),
		$loginModeToggle = $('.js-login-mode-toggle'),
		$loginSubmit = $('.js-login-submit'),
		$error = $('.js-login-error'),
		$errorText = $('.js-login-error-text'),
		$username = $('.js-login-username'),
		$password = $('.js-login-password'),
		$select = $('.login-select'),

		createAccount = 'login-create-account',
		tick = 'fa fa-check',
		validationError = 'login-validation-error',
		formInvalid = 'login-form-invalid',

		createAccountText = 'Create Account',
		signInText = 'Sign In';

	function isFormInvalid() {
		return $loginForm.hasClass(formInvalid);
	}

	function toggleCheckbox() {
		if ($checkButton.hasClass(tick)) {
			$checkButton.removeClass(tick);
			$checkInput.prop('checked', false);
		} else {
			$checkButton.addClass(tick);
			$checkInput.prop('checked', true);
		}
	}

	function isLoginDisplayed() {
		return !$loginForm.hasClass(createAccount);
	}

	function toggleDisabledInputs(loginDisplayed) {
		if (loginDisplayed)  {
			$createAccountInputs.prop('disabled', false);
			$checkInput.prop('disabled', true);
		} else {
			$createAccountInputs.prop('disabled', true);
			$checkInput.prop('disabled', false);
		}
	}

	function toggleLoginCreateAccount() {
		var loginDisplayed = isLoginDisplayed(),
			currentFormAction = $loginForm.attr('action');

		// Once we have the create account post, change the form action to be stored on a data attribute, and toggle the URL stored
		// when we toggle.

		$loginForm.attr('action', $loginForm.data('alt-action'));
		$loginForm.data('alt-action', currentFormAction);

		$loginForm.toggleClass(createAccount, loginDisplayed);
		$loginModeToggle.text(loginDisplayed ?  signInText : createAccountText);
		$loginSubmit.text(loginDisplayed ? createAccountText : signInText);
		toggleDisabledInputs(loginDisplayed);
	}

	function displayClientError(errorMessage) {
		$errorText.text(errorMessage);
		$error.removeClass('login-valid');
		$loginForm.addClass(formInvalid);
	}

	function clearServerErrors() {
		$('.js-th-login-error').empty();
	}

	function clearClientErrors() {
		$('.' + validationError).removeClass(validationError);
		$errorText.empty();
		$error.addClass('login-valid');
	}

	function clearErrors() {
		// Double check, as this function may ocassionally be called unnecessarily
		if (isFormInvalid()) {
			clearServerErrors();
			clearClientErrors();
			$loginForm.removeClass(formInvalid);
		}
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

		$('.select2-container').parent('.login-form-control').toggleClass(validationError, !$select.select2('val'));

		// Set the error message if there's a validation error
		if ($('.' + validationError).length > 0) {
			errorMessage = 'Please fill in all required fields.';
		}

		return errorMessage;
	}

	// Record whether media queries are supported in this browser as a class
	if (!Modernizr.mq('only all')) {
		$('html').addClass('no-mq');
	}

	// Initialise placeholder polyfill plugin
	$('input, textarea').placeholder();

	// TODO: Remove this dropdown and associated code once proper role management is in place.
	// Initialise role dropdown. Hook in special handling to clear errors on focus, as it captures the form-control click event
	$select.select2({
		placeholder: 'Role',
		minimumResultsForSearch: -1
	}).on('select2-focus', clearErrors);

	// Making the select container act as though it was all one
	$('.select2-container').on('click', function() {
		$('.select2-container').toggleClass('select2-container-active', true);
		$select.select2('open');
	});

	// Giving the select container the ability to be focused
	$('.select2-container').attr('tabindex', 1);

	// Hook up our fake (better looking) checkbox with it's real, submit-able counterpart
	$('.js-login-checkbox-control').on('click', '.js-login-remember-me', function(e) {
		e.preventDefault();
		toggleCheckbox();
	});

	// Clear error messages if the user focuses a form input
	$('body').delegate('.login-form-invalid .login-form-control', 'focusin click', function() {
		clearErrors();
	});

	// Toggle between forms
	$loginModeToggle.on('click', function(e) {
		e.preventDefault();
		clearErrors();
		toggleLoginCreateAccount();
	});

	// Expects an object, the keys of which are names of invalid form inputs
	function applyValidationErrors(errors) {
		var errorMessage = '';

		$.each(errors, function(key, value) {
			$loginForm.find('*[name=' + key + ']').parent('.login-form-control').addClass('login-validation-error');
			errorMessage +=  errorMessage ? (', ' + value) : value;
		});
		displayClientError(errorMessage);
	}

	$loginForm.on('submit', function(e) {
		// Prevent default submit behaviour and implement our own post / response handler
		e.preventDefault();

		var login = isLoginDisplayed(),
			errorMessage = login ? validateSignInInputs() : validateCreateAccountInputs();

		if (errorMessage) {
			displayClientError(errorMessage);
			return;
		}

		// Continue with form submit - login is currently handled server side
		if (login) {
			this.submit();

		// Create Account
		} else {

			// FIXME: This service needs tweaking - it should return a 400 if there is a client error, and a success should be handled
			// server side, to log the user in.
			$.post($loginForm.attr('action'), $loginForm.serialize())
				.done(function(data) {

					// FIXME: This error handling should happen in a 'fail' handler (see comment above)
					if (data.errors) {
						applyValidationErrors(data.errors);
					} else {
						// FIXME: User should be automatically logged in after a successful signup (see comment above)
						// Clear form fields and show the login screen
						clearErrors();
						$loginForm.find('input').val('');
						$select.select2('val', 'Role');
						toggleLoginCreateAccount();
					}
				})
				.always(function() {
					$loginSubmit.removeClass('loading');
				});
		}
	});

}());
