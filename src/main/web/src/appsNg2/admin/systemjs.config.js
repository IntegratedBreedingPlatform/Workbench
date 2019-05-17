(function (global) {

    var paths = {
        // paths serve as alias
        'lib:': 'pages/angular2/lib/'
    };

    // map tells the System loader where to look for things
    var map = {
        'app': 'pages/angular2/admin/app',

        // angular bundles
        '@angular/core': 'lib:@angular/core/bundles/core.umd.min.js',
        '@angular/common': 'lib:@angular/common/bundles/common.umd.min.js',
        '@angular/compiler': 'lib:@angular/compiler/bundles/compiler.umd.min.js',
        '@angular/platform-browser': 'lib:@angular/platform-browser/bundles/platform-browser.umd.min.js',
        '@angular/platform-browser-dynamic': 'lib:@angular/platform-browser-dynamic/bundles/platform-browser-dynamic.umd.min.js',
        '@angular/http': 'lib:@angular/http/bundles/http.umd.min.js',
        '@angular/router': 'lib:@angular/router/bundles/router.umd.min.js',
        '@angular/forms': 'lib:@angular/forms/bundles/forms.umd.min.js',
        '@angular/upgrade': 'lib:@angular/upgrade/bundles/upgrade.umd.min.js',

        // other libraries
        'rxjs': 'lib:rxjs',
        'angular-in-memory-web-api': 'lib:angular-in-memory-web-api',
        'ng2-select2': 'lib:ng2-select2/ng2-select2.bundle.js'
    };

    // packages tells the System loader how to load when no filename and/or no extension
    var packages = {
        'app': { main: 'main.js', defaultExtension: 'js' },
        'rxjs': { defaultExtension: 'js' }
    };

    var config = {
        map: map,
        packages: packages,
        paths: paths
    };

    // filterSystemConfig - index.html's chance to modify config before we register it.
    if (global.filterSystemConfig) {
        global.filterSystemConfig(config);
    }

    System.config(config);

})(this);
