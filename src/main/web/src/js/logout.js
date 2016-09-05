(function() {
    'use strict';

    /* init on document load */
    $(document).ready(function() {

        var isInFrame = window.location !== window.parent.location;
        var parentUrl = isInFrame ? document.referrer : document.location.href;
        var pathArray = parentUrl.split('/');
        var protocol = pathArray[0];
        var host = pathArray[2];
        var baseUrl = protocol + '//' + host;

        var fieldBookLogoutUrl = baseUrl + '/Fieldbook/logout';
        var breedingManagerLogoutUrl = baseUrl + '/BreedingManager/logout';
        var workbenchLogoutUrl = baseUrl + '/ibpworkbench/logout';

         $.ajax({
                url: fieldBookLogoutUrl,
                type: 'GET',
                data: '',
                cache: false,
                async: false,
                success: function(data) {
                }
            });

        $.ajax({
            url: breedingManagerLogoutUrl,
            type: 'GET',
            data: '',
            cache: false,
            async: false,
            success: function(data) {
            }
        });

        //alert('You have logged out of all application');
        window.top.location.href = workbenchLogoutUrl;

    });

}());
