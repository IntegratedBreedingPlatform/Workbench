/*global Modernizr*/

(function () {
	'use strict';

	var $checkButton = $('.js-login-check'),
		$checkInput = $('.js-login-checkbox-input'),
		$loginForm = $('.js-login-form'),
		$authorizeForm = $('#authorize-form'),
		$oneTimePasswordForm = $('#one-time-password-form'),
		$loginModeToggle = $('.js-login-mode-toggle'),
		$loginSubmit = $('.js-login-submit'),
		$authorizeSubmit = $('.js-authorize-submit'),
		$verifyOtpSubmit = $('.js-verify-otp-submit'),
		$otpError = $('.js-login-otp-error'),
		$otpErrorText = $('.js-login-otp-error-text'),
		$otpCode = $('.js-otp-code'),
		$error = $('.js-login-error'),
		$errorText = $('.js-login-error-text'),
		$fakeUsername = $('.js-fake-username'),
		$fakePassword = $('.js-fake-password'),
		$username = $('.js-login-username'),
		$password = $('.js-login-password'),
		$select = $('.login-select'),
		$select2Container = $('.select2-container'),

		createAccount = 'login-create-account',
		forgotPasswordClass = 'login-forgot-password',
		validationError = 'login-validation-error',
		formInvalid = 'login-form-invalid',

		createAccountText = 'Create Account',
		signInText = 'Sign In',
		resetPasswordText = 'Continue',

		loginAction = $loginForm.attr('action'),

		altAction = $loginForm.data('alt-action'),
		forgotPasswordAction = $loginForm.data('forgot-password-action');

	var display_name = getUrlParameter("display_name");
	var return_url = getUrlParameter("return_url");
	// oauth
	var client_id = getUrlParameter("client_id");
	var redirect_uri = getUrlParameter("redirect_uri");

	// Reset the authentication token when login page is loaded.
	// TODO: Handle null bms.xAuthToken in all code that uses this localStorage item, so that we can just remove
	// this item from localStorage rathen than resetting its value.
	localStorage['bms.xAuthToken'] = JSON.stringify({success: false, token: '', expires: 0});


	var failedLoginAttemptCount = 0;

	function toggleCheckbox() {
		var tick = $checkButton.hasClass('fa-check');

		if (!tick) {
			$checkButton.addClass('fa-check');
			$checkInput.prop('checked', true);
		} else {
			$checkButton.removeClass('fa-check');
			$checkInput.prop('checked', false);
		}


	}

	function isLoginDisplayed() {
		return !($loginForm.hasClass(createAccount) || $loginForm.hasClass(forgotPasswordClass));
	}

	function isForgotPasswordScreenDisplayed() {
		return $loginForm.hasClass(forgotPasswordClass);
	}

	/**
	 * dynamic selection of toggle closures
	 * @param toggleFunction
	 */
	function toggleLoginPage(toggleFunction) {

		if ($loginForm.hasClass(createAccount)) {
			toggleLoginCreateAccount();
		} else if ($loginForm.hasClass(forgotPasswordClass)) {
			toggleForgotPasswordScreen();
		} else {
			toggleFunction();
		}
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
		$loginModeToggle.text(switchToCreate ? signInText : createAccountText);
		$loginSubmit.text(switchToCreate ? createAccountText : signInText);

		// Disable / enable inputs as required, to ensure all and only appropriate inputs are submitted
		$createAccountInputs.prop('disabled', !switchToCreate);
		$checkInput.prop('disabled', switchToCreate);
		$select.select2('enable', switchToCreate);
	}

	function toggleForgotPasswordScreen() {
		var switchToCreate = isLoginDisplayed(),
			$forgotPasswordInputs = $('.js-login-forgot-password-input'),
			prevAction = $loginForm.attr('action');

		// Once we have the create account post, change the form action to be stored on a data attribute, and toggle the URL stored
		// when we toggle.
		$loginForm.attr('action', forgotPasswordAction);
		forgotPasswordAction = prevAction;

		$loginForm.toggleClass(forgotPasswordClass, switchToCreate);
		$loginModeToggle.text(switchToCreate ? signInText : createAccountText);
		$loginSubmit.text(switchToCreate ? resetPasswordText : signInText);

		// clear out password field
		$('.js-login-password').val('');

		// Disable / enable inputs as required, to ensure all and only appropriate inputs are submitted
		$forgotPasswordInputs.prop('disabled', !switchToCreate);
		$checkInput.prop('disabled', switchToCreate);
	}

	function toggleVerifyOneTimePasswordScreen() {
		$loginForm.hide();
		$oneTimePasswordForm.show();
	}

	function toggleAuthorizeScreen() {
		$('#displayName').text(display_name || client_id);
		$loginForm.hide();
		$oneTimePasswordForm.hide();
		$authorizeForm.show();
	}

	function displayOTPError(errorMessage) {
		$otpErrorText.text(errorMessage);
		$otpError.removeClass('login-otp-valid');
	}

	function displayClientError(errorMessage) {
		$errorText.text(errorMessage);
		$error.removeClass('login-valid');
		$loginForm.addClass(formInvalid);
	}

	function clearErrors() {
		// Double check if form is actually invalid, as this function may occasionally be called unnecessarily
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
		$('.js-login-form .login-form-control input').each(function (index, input) {
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

	function validateForgotPasswordInputs() {
		var errorMessage;

		// Add validation error styling to all empty inputs
		$('.js-login-username, .js-login-forgot-password-input').each(function (index, input) {
			var $input = $(input);
			$input.parent('.login-form-control').toggleClass(validationError, !$input.val());
		});

		// Set the error message if there's a validation error
		if ($('.' + validationError).length > 0) {
			errorMessage = 'Please fill in all required fields.';
		}

		return errorMessage;
	}

	// Expects an object, the keys of which are names of invalid form inputs
	function applyValidationErrors(errors) {
		var errorMessage = '';

		$.each(errors, function (key, value) {
			$loginForm.find('*[name=' + key + ']').parent('.login-form-control').addClass('login-validation-error');
			errorMessage += errorMessage ? (' ' + value) : value;
		});
		displayClientError(errorMessage);
	}

	function doFormSubmit() {
		$loginForm.submit();
		return false;
	}

	function doAuthorizeSubmit() {

		const token = JSON.parse(localStorage['bms.xAuthToken']).token;
		if (client_id && redirect_uri) {
			window.location.href = redirect_uri + '?access_token=' + token;
		} else {
			// Append status=200 to the query string to notify KSU Fieldbook that the authentication is successful.
			window.location.href = return_url + '?token=' + token + '&status=200';
		}
		return false;
	}

	function doCreateOTP() {

		var request = {
			username: $username.val(),
			password: $password.val()
		};
		// Create one-time password verification code and send
		// it to the user's email
		$.ajax({
			url: '/ibpworkbench/controller/auth/otp/create',
			dataType: 'json',
			type: 'post',
			contentType: 'application/json',
			data: JSON.stringify(request),
			processData: false,
			success: function (data) {
				// Do nothing
				return;
			}
		}).fail((error) => {
			displayOTPError(error.responseJSON.errors);
		});
	}

	function doVerifyOTP() {

		$verifyOtpSubmit.addClass('loading');
		$verifyOtpSubmit.attr("disabled", true);

		var request = {
			username: $username.val(),
			password: $password.val(),
			otpCode: $otpCode.val()
		};
		$.ajax({
			url: '/ibpworkbench/controller/auth/otp/verify',
			dataType: 'json',
			type: 'post',
			contentType: 'application/json',
			data: JSON.stringify(request),
			processData: false,
			success: function (data) {
				$verifyOtpSubmit.removeClass('loading');
				$verifyOtpSubmit.removeAttr("disabled");
				processLoginSuccess(data);
			}
		}).fail((error) => {
			$verifyOtpSubmit.removeClass('loading');
			$verifyOtpSubmit.removeAttr("disabled");
			displayOTPError(error.responseJSON.errors);
		});
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
	$select2Container.on('click', function () {
		$('.select2-container').toggleClass('select2-container-active', true);
		$select.select2('open');
	});

	// Disable the select until create account page is shown
	$select.select2('enable', false);

	// Giving the select container the ability to be focused
	$select2Container.attr('tabindex', 1);

	$loginSubmit.on('click', function () {
		return doFormSubmit();
	});

	$authorizeSubmit.on('click', function () {
		return doAuthorizeSubmit();
	});

	$verifyOtpSubmit.on('click', function () {
		return doVerifyOTP();
	});

	// Hook up our fake (better looking) checkbox with it's real, submit-able counterpart
	$('.js-login-checkbox-control').on('click', '.js-login-remember-me', function (e) {
		e.preventDefault();
		toggleCheckbox();
	});

	// Clear error messages if the user focuses a form input
	$('body').delegate('.login-form-invalid .login-form-control, .login-form-invalid .login-submit', 'focusin click', function () {
		clearErrors();
	});

	// Toggle between forms
	$loginModeToggle.on('click', function (e) {
		e.preventDefault();
		$('.login-forgot-password-email-notify').hide();

		clearErrors();
		toggleLoginPage(toggleLoginCreateAccount);
	});

	$('.ac-login-forgot-password').on('click', function (e) {
		e.preventDefault();
		$('.login-forgot-password-email-notify').hide();

		clearErrors();
		toggleLoginPage(toggleForgotPasswordScreen);

	});

	$('.login-form-control input').on('keypress', function (e) {
		if (e.which === 13) {
			e.preventDefault();
			$loginForm.submit();
		}
	});

	var doSendPasswordRequestEmail = function (userForm) {
		$.post($loginForm.data('reset-password-action'), userForm);
		$('.login-forgot-password-email-notify').show();
	};

	$loginForm.on('submit', function (e) {

		// Prevent default submit behaviour and implement our own post / response handler
		e.preventDefault();

		var loginFormRef = this,
			login = isLoginDisplayed(),
			isPasswordScreen = isForgotPasswordScreenDisplayed(),
			errorMessage = isPasswordScreen ? validateForgotPasswordInputs() :
				(login ? validateSignInInputs() : validateCreateAccountInputs());

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
			$.post($loginForm.data('validate-login-action'), $loginForm.serialize())
				.done(function (data) {
					clearErrors();

					if (data && data.requireOneTimePassword) {
						doCreateOTP();
						toggleVerifyOneTimePasswordScreen();
					} else {
						processLoginSuccess(data);
					}
				})
				.fail(function (jqXHR) {
					applyValidationErrors(jqXHR.responseJSON ? jqXHR.responseJSON.errors : {});

					if (failedLoginAttemptCount < 2) {
						failedLoginAttemptCount++;
					} else {
						toggleForgotPasswordScreen();
					}
				})
				.always(function () {
					$loginSubmit.removeClass('loading');
				});
		} else {
			// Create account or forgot password
			var userForm = $loginForm.serialize();

			$.post($loginForm.attr('action'), userForm)
				.done(function () {
					// Clear form fields and show the login screen
					clearErrors();

					if (isPasswordScreen) {
						// we add notification that the login email has been set
						$('.js-login-forgot-password-input').val('');
						toggleForgotPasswordScreen();

						doSendPasswordRequestEmail(userForm);

					} else {
						// this will automatically login the user.
						$loginForm.attr('action', loginAction);
						loginFormRef.submit();
					}
				})
				.fail(function (jqXHR) {
					applyValidationErrors(jqXHR.responseJSON ? jqXHR.responseJSON.errors : {});
				})
				.always(function () {
					$loginSubmit.removeClass('loading');
				});
		}
	});

	function processLoginSuccess(tokenData) {
		/**
		 * This is crucial for the Ontology Manager UI which retrieves the token from local storage
		 * and uses it to make calls to BMSAPI.
		 * See bmsAuth.js and ontology.js and the AuthenticationController.validateLogin() method on server side.
		 * The prefix "bms" is configured in ontology.js as part of app.config:
		 *     localStorageServiceProvider.setPrefix('bms');
		 */
		localStorage['bms.xAuthToken'] = JSON.stringify(tokenData);
		if ((display_name && return_url) || (client_id && redirect_uri)) {
			toggleAuthorizeScreen();
		} else {
			// Detach validation so we can proceed with submit
			$loginForm.off( "submit" );
			$loginForm.submit();
		}
	}

}());

function numbersOnly(evt) {

	// Only ASCII character in that range allowed
	var ASCIICode = (evt.which) ? evt.which : evt.keyCode
	if (ASCIICode > 31 && (ASCIICode < 48 || ASCIICode > 57))
		return false;
	return true;
}
