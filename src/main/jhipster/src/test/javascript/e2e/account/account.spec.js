"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var protractor_1 = require("protractor");
var jhi_page_objects_1 = require("./../page-objects/jhi-page-objects");
describe('account', function () {
    var navBarPage;
    var signInPage;
    beforeAll(function () {
        protractor_1.browser.get('/');
        protractor_1.browser.waitForAngular();
        navBarPage = new jhi_page_objects_1.NavBarPage(true);
        protractor_1.browser.waitForAngular();
    });
    it('should fail to login with bad password', function () {
        var expect1 = /home.title/;
        protractor_1.element.all(protractor_1.by.css('h1')).first().getAttribute('jhiTranslate').then(function (value) {
            expect(value).toMatch(expect1);
        });
        signInPage = navBarPage.getSignInPage();
        signInPage.loginWithOAuth('admin', 'foo');
        // Keycloak
        var alert = protractor_1.element.all(protractor_1.by.css('.alert-error'));
        alert.isPresent().then(function (result) {
            if (result) {
                expect(alert.first().getText()).toMatch('Invalid username or password.');
            }
            else {
                // Okta
                var error_1 = protractor_1.element.all(protractor_1.by.css('.infobox-error')).first();
                protractor_1.browser.wait(protractor_1.ExpectedConditions.visibilityOf(error_1), 2000).then(function () {
                    expect(error_1.getText()).toMatch('Sign in failed!');
                });
            }
        });
    });
    it('should login successfully with admin account', function () {
        signInPage.clearUserName();
        signInPage.setUserName('admin');
        signInPage.clearPassword();
        signInPage.setPassword('admin');
        signInPage.login();
        protractor_1.browser.waitForAngular();
        var expect2 = /home.logged.message/;
        var success = protractor_1.element.all(protractor_1.by.css('.alert-success span')).first();
        protractor_1.browser.wait(protractor_1.ExpectedConditions.visibilityOf(success), 5000).then(function () {
            success.getAttribute('jhiTranslate').then(function (value) {
                expect(value).toMatch(expect2);
            });
        });
        navBarPage.autoSignOut();
    });
});
