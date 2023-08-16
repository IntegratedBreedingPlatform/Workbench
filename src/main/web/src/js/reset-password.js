/**
 * Created by cyrus on 4/7/15.
 */
/*global $, MESSAGE*/
(function () {
	'use strict';

	var $resetForm = $('.js-reset-form'),
		$passwordField = $('.js-reset-password'),
		$passwordConfirmationField = $('.js-reset-forgot-password'),
		$error = $('.js-login-error'),
		$errorText = $('.js-reset-error-text'),

		validationErrorClass = 'login-validation-error',
		formInvalid = 'login-form-invalid',

		resetActionUrl = $resetForm.data('reset-action');

	function displayClientError(errorMessage) {
		$errorText.text(errorMessage);
		$error.removeClass('login-valid');
		$resetForm.addClass(formInvalid);
	}

	function doPostResetAction(resetFormData) {
		return $.post(resetActionUrl, resetFormData);
	}

	// Expects an object, the keys of which are names of invalid form inputs
	function applyValidationErrors(errors) {
		var errorMessage = '';

		$.each(errors, function (key, value) {
			$resetForm.find('*[name=' + key + ']').parent('.login-form-control').addClass('login-validation-error');
			errorMessage += errorMessage ? (' ' + value) : value;
		});
		displayClientError(errorMessage);
	}

	function assessPasswordStrength(password, isSubmitting) {
		if (password == '') {
			return {
				valid: false,
				message: 'Password cannot be empty.'
			}
		}

		var result = zxcvbn(password),
			score = result.score,
			message = result.feedback.warning || 'The password is weak';

		var progressBarClass = 'progress-bar-danger', progressWidth = '20%', strengthMsg = 'Weak';

		switch (score) {
			case 0:
				break;
			case 1:
				progressWidth = '35%';
				break;
			case 2:
				progressWidth = '50%';
				break;
			case 3:
				progressWidth = '75%';
				progressBarClass = 'progress-bar-warning';
				strengthMsg = 'Acceptable';
				break;
			case 4:
				progressWidth = '100%';
				progressBarClass = 'progress-bar-success';
				strengthMsg = 'Strong';
				break;
		}

		$('#strengthBar').attr('class', 'progress-bar ' + progressBarClass)
			.css('width', progressWidth);
		$('#strengthMessage').text(strengthMsg);

		// We will treat the password as an invalid one if the score is less than 3
		if (score < 3) {
			displayClientError(message);
		} else {
			$errorText.text('');
			$error.addClass('login-valid');
		}

		return {
			valid: true,
			message: ''
		}
	}

	/* init on document load */
	$(document).ready(function () {
		$passwordField.focus();
		$passwordField.tooltip();

		$passwordField.keyup(function () {
			return assessPasswordStrength($(this).val());
		});

		$resetForm.on('submit', function (e) {
			e.preventDefault();

			var passwordStrength = assessPasswordStrength($passwordField.val());
			if (!passwordStrength.valid) {
				displayClientError(passwordStrength.message);
				return;
			}

			var resetFormRef = this;

			doPostResetAction($resetForm.serialize()).done(function (data) {
				if (data && data.success) {
					resetFormRef.submit();
				} else {
					displayClientError('Something Went Wrong :(');
				}
			})
				.fail(function (jqXHR) {
					applyValidationErrors(jqXHR.responseJSON ? jqXHR.responseJSON.errors : {});
				});

		});

	});

}());
