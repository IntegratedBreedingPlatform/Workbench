/**
 * Created by cyrus on 4/7/15.
 */
(function() {
    'use strict';

    var $resetForm = $('.js-reset-form'),
        $passwordField = $('.js-reset-password'),
        $passwordConfirmationField = $('.js-reset-forgot-password'),
        $error = $('.js-login-error'),
        $errorText = $('.js-reset-error-text'),

        validationErrorClass = 'login-validation-error',
        formInvalid = 'login-form-invalid',

        resetActionUrl = $resetForm.data('reset-action');

    function isResetFormValid(resetForm) {
        return  resetForm.password === resetForm.passwordConfirmation;
    }

    function validateResetFormFields() {
        var errorMessage;

        if (!isResetFormValid($resetForm.serializeObject())) {
            $passwordConfirmationField.parent('.login-form-control').addClass(validationErrorClass);

            errorMessage = MESSAGE.passwordNotEqual;
        }

        return errorMessage;
    }

    function displayClientError(errorMessage) {
        $errorText.text(errorMessage);
        $error.removeClass('login-valid');
        $resetForm.addClass(formInvalid);
    }

    function doPostResetAction(resetFormData) {
        return $.post(resetActionUrl,resetFormData);
    }

    /* init on document load */
    $(document).ready(function() {
        $passwordField.focus();

        $resetForm.on('submit',function(e) {
            e.preventDefault();

            var resetFormRef = this;
            var errorMessage = validateResetFormFields();

            if (errorMessage) {
                displayClientError(errorMessage);
                return;
            }

            doPostResetAction($resetForm.serialize()).done(function(data) {
               if ('true' === data) {
                   resetFormRef.submit();
               } else {
                   displayClientError('Something Went Wrong :(');
               }
            });

        });

    });


}());