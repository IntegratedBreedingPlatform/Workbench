(function() {
    var i = 0,
        max = 0,
        o = null,

    // list of stuff to preload
        preload = [
            '/ibpworkbench/VAADIN/themes/vaadin-retro/fonts/font-min.css',
            '/ibpworkbench/VAADIN/themes/vaadin-retro/fonts/styles-min.css'
        ],
        isIE = navigator.appName.indexOf('Microsoft') === 0;

    for (i = 0, max = preload.length; i < max; i += 1) {

        if (isIE) {
            new Image().src = preload[i];
            continue;
        }
        o = document.createElement('object');
        o.data = preload[i];

        // IE stuff, otherwise 0x0 is OK
        //o.width = 1;
        //o.height = 1;
        //o.style.visibility = "hidden";
        //o.type = "text/plain"; // IE
        o.width  = 0;
        o.height = 0;


        // only FF appends to the head
        // all others require body
        document.body.appendChild(o);
    }

})();