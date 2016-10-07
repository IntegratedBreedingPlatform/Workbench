(function (global) {

    var paths = {
        // paths serve as alias
        'lib:': 'pages/angular2/lib/'
    };

    // map tells the System loader where to look for things
    var map = {
        'app': 'pages/angular2/admin/app',

        // angular bundles
        '@angular/core': 'lib:@angular/core/bundles/core.umd.js',
        '@angular/core/testing': 'lib:@angular/core/bundles/core-testing.umd.js',
        '@angular/platform-browser-dynamic/testing': 'lib:@angular/platform-browser-dynamic/bundles/platform-browser-dynamic-testing.umd.js',
        '@angular/platform-browser/testing': 'lib:@angular/platform-browser/bundles/platform-browser-testing.umd.js',
        '@angular/compiler/testing': 'lib:@angular/compiler/bundles/compiler-testing.umd.js',
        '@angular/http/testing': 'lib:@angular/http/bundles/http-testing.umd.js',
        '@angular/common': 'lib:@angular/common/bundles/common.umd.js',
        '@angular/compiler': 'lib:@angular/compiler/bundles/compiler.umd.js',
        '@angular/platform-browser': 'lib:@angular/platform-browser/bundles/platform-browser.umd.js',
        '@angular/platform-browser-dynamic': 'lib:@angular/platform-browser-dynamic/bundles/platform-browser-dynamic.umd.js',
        '@angular/http': 'lib:@angular/http/bundles/http.umd.js',
        '@angular/router': 'lib:@angular/router/bundles/router.umd.js',
        '@angular/forms': 'lib:@angular/forms/bundles/forms.umd.js',
        '@angular/upgrade': 'lib:@angular/upgrade/bundles/upgrade.umd.js',

        // other libraries
        'rxjs': 'lib:rxjs',
        'angular-in-memory-web-api': 'lib:angular-in-memory-web-api',
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
