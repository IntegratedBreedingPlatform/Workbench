"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var protractor_1 = require("protractor");
var NavBarPage = /** @class */ (function () {
    function NavBarPage(asAdmin) {
        this.entityMenu = protractor_1.element(protractor_1.by.id('entity-menu'));
        this.accountMenu = protractor_1.element(protractor_1.by.id('account-menu'));
        this.signIn = protractor_1.element(protractor_1.by.id('login'));
        this.register = protractor_1.element(protractor_1.by.css('[routerLink="register"]'));
        this.signOut = protractor_1.element(protractor_1.by.id('logout'));
        this.passwordMenu = protractor_1.element(protractor_1.by.css('[routerLink="password"]'));
        this.settingsMenu = protractor_1.element(protractor_1.by.css('[routerLink="settings"]'));
        if (asAdmin) {
            this.adminMenu = protractor_1.element(protractor_1.by.id('admin-menu'));
        }
    }
    NavBarPage.prototype.clickOnEntityMenu = function () {
        return this.entityMenu.click();
    };
    NavBarPage.prototype.clickOnAccountMenu = function () {
        return this.accountMenu.click();
    };
    NavBarPage.prototype.clickOnAdminMenu = function () {
        return this.adminMenu.click();
    };
    NavBarPage.prototype.clickOnSignIn = function () {
        return this.signIn.click();
    };
    NavBarPage.prototype.clickOnRegister = function () {
        return this.signIn.click();
    };
    NavBarPage.prototype.clickOnSignOut = function () {
        return this.signOut.click();
    };
    NavBarPage.prototype.clickOnPasswordMenu = function () {
        return this.passwordMenu.click();
    };
    NavBarPage.prototype.clickOnSettingsMenu = function () {
        return this.settingsMenu.click();
    };
    NavBarPage.prototype.clickOnEntity = function (entityName) {
        return protractor_1.element(protractor_1.by.css('[routerLink="' + entityName + '"]')).click();
    };
    NavBarPage.prototype.clickOnAdmin = function (entityName) {
        return protractor_1.element(protractor_1.by.css('[routerLink="' + entityName + '"]')).click();
    };
    NavBarPage.prototype.getSignInPage = function () {
        this.clickOnAccountMenu();
        this.clickOnSignIn();
        return new SignInPage();
    };
    NavBarPage.prototype.goToEntity = function (entityName) {
        this.clickOnEntityMenu();
        return this.clickOnEntity(entityName);
    };
    NavBarPage.prototype.goToSignInPage = function () {
        this.clickOnAccountMenu();
        this.clickOnSignIn();
    };
    NavBarPage.prototype.goToPasswordMenu = function () {
        this.clickOnAccountMenu();
        this.clickOnPasswordMenu();
    };
    NavBarPage.prototype.autoSignOut = function () {
        this.clickOnAccountMenu();
        this.clickOnSignOut();
    };
    return NavBarPage;
}());
exports.NavBarPage = NavBarPage;
var SignInPage = /** @class */ (function () {
    function SignInPage() {
        this.username = protractor_1.element(protractor_1.by.name('username'));
        this.password = protractor_1.element(protractor_1.by.name('password'));
        this.loginButton = protractor_1.element(protractor_1.by.css('input[type=submit]'));
    }
    SignInPage.prototype.setUserName = function (username) {
        this.username.sendKeys(username);
    };
    SignInPage.prototype.getUserName = function () {
        return this.username.getAttribute('value');
    };
    SignInPage.prototype.clearUserName = function () {
        this.username.clear();
    };
    SignInPage.prototype.setPassword = function (password) {
        this.password.sendKeys(password);
    };
    SignInPage.prototype.getPassword = function () {
        return this.password.getAttribute('value');
    };
    SignInPage.prototype.clearPassword = function () {
        this.password.clear();
    };
    SignInPage.prototype.loginWithOAuth = function (username, password) {
        var _this = this;
        // Entering non angular site, tell webdriver to switch to synchronous mode.
        protractor_1.browser.waitForAngularEnabled(false);
        this.username.isPresent().then(function () {
            _this.username.sendKeys(username);
            _this.password.sendKeys(password);
            _this.loginButton.click();
        }).catch(function (error) {
            protractor_1.browser.waitForAngularEnabled(true);
        });
    };
    SignInPage.prototype.login = function () {
        return this.loginButton.click();
    };
    return SignInPage;
}());
exports.SignInPage = SignInPage;
