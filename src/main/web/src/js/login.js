/*global Modernizr*/

(function() {
	'use strict';

	var $checkButton = $('.js-login-check'),
		$checkInput = $('.js-login-checkbox-input'),
		$loginForm = $('.js-login-form'),
		$loginModeToggle = $('.js-login-mode-toggle'),
		$loginSubmit = $('.js-login-submit'),
		$error = $('.js-login-error'),
		$errorText = $('.js-login-error-text'),
		$fakeUsername = $('.js-fake-username'),
		$fakePassword =  $('.js-fake-password'),
		$username = $('.js-login-username'),
		$password = $('.js-login-password'),
		$select = $('.login-select'),

		createAccount = 'login-create-account',
		validationError = 'login-validation-error',
		formInvalid = 'login-form-invalid',

		createAccountText = 'Create Account',
		signInText = 'Sign In',

		altAction = $loginForm.data('alt-action');

	function toggleCheckbox() {
		var tick = $checkButton.text() !== '';

		$checkButton.text(tick ? '' : '\uF00C');
		$checkInput.prop('checked', !tick);
	}

	function isLoginDisplayed() {
		return !$loginForm.hasClass(createAccount);
	}

	function toggleLoginCreateAccount() {
		var switchToCreate = isLoginDisplayed(),
			$createAccountInputs = $('.js-login-create-account-input'),
			prevAction = $loginForm.attr('action');

		// Once we have the create account post, change the form action to be stored on a data attribute, and toggle the URL stored
		// when we toggle.
		$loginForm.attr('action', altAction);
		altAction = prevAction;

		$loginForm.toggleClass(createAccount, switchToCreate);
		$loginModeToggle.text(switchToCreate ?  signInText : createAccountText);
		$loginSubmit.text(switchToCreate ? createAccountText : signInText);

		// Disable / enable inputs as required, to ensure all and only appropriate inputs are submitted
		$createAccountInputs.prop('disabled', !switchToCreate);
		$checkInput.prop('disabled', switchToCreate);
		$select.select2('enable', switchToCreate);
	}

	function displayClientError(errorMessage) {
		$errorText.text(errorMessage);
		$error.removeClass('login-valid');
		$loginForm.addClass(formInvalid);
	}

	function clearErrors() {
		// Double check if form is actually invalid, as this function may ocassionally be called unnecessarily
		if ($loginForm.hasClass(formInvalid)) {

			// Clear server errors
			$('.js-th-login-error').empty();

			// Clear client errors
			$('.' + validationError).removeClass(validationError);
			$errorText.empty();
			$error.addClass('login-valid');

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

	// Expects an object, the keys of which are names of invalid form inputs
	function applyValidationErrors(errors) {
		var errorMessage = '';

		$.each(errors, function(key, value) {
			$loginForm.find('*[name=' + key + ']').parent('.login-form-control').addClass('login-validation-error');
			errorMessage +=  errorMessage ? (' ' + value) : value;
		});
		displayClientError(errorMessage);
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

	// Disable the select until create account page is shown
	$select.select2('enable', false);

	// Giving the select container the ability to be focused
	$('.select2-container').attr('tabindex', 1);

	// Hook up our fake (better looking) checkbox with it's real, submit-able counterpart
	$('.js-login-checkbox-control').on('click', '.js-login-remember-me', function(e) {
		e.preventDefault();
		toggleCheckbox();
	});

	// Clear error messages if the user focuses a form input
	$('body').delegate('.login-form-invalid .login-form-control, .login-form-invalid .login-submit', 'focusin click', function() {
		clearErrors();
	});

	// Toggle between forms
	$loginModeToggle.on('click', function(e) {
		e.preventDefault();
		clearErrors();
		toggleLoginCreateAccount();
	});

	$loginForm.on('submit', function(e) {
		// Prevent default submit behaviour and implement our own post / response handler
		e.preventDefault();

		var login = isLoginDisplayed(),
			errorMessage = login ? validateSignInInputs() : validateCreateAccountInputs();

		if (errorMessage) {
			displayClientError(errorMessage);
			return;
		}

		// Long story here: we use fake inputs to prevent Chrome's awful yellow autofill styling (see login.html for details). When Chrome
		// offers to remember a password, it will try and take the value of these fake inputs - and because they're empty, it will never be
		// satisfied - asking over and over again. So we set their value on submit, Chrome is happy, and everything is right with the World.
		$fakeUsername.val($username.val());
		$fakePassword.val($password.val());

		$loginSubmit.addClass('loading').delay(200);

		// Continue with form submit - login is currently handled server side
		if (login) {
			this.submit();

		// Create Account
		} else {
			$.post($loginForm.attr('action'), $loginForm.serialize())
				.done(function() {
					//  automatically log in after a successful signup
					clearErrors();
					$loginForm.toggleClass(createAccount, false);
					$loginForm.submit();
				})
				.fail(function(jqXHR) {
					applyValidationErrors(jqXHR.responseJSON ? jqXHR.responseJSON.errors : {});
				})
				.always(function() {
					$loginSubmit.removeClass('loading');
				});
		}
	});
}());
