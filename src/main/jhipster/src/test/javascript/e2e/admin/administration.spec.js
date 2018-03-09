"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var protractor_1 = require("protractor");
var jhi_page_objects_1 = require("./../page-objects/jhi-page-objects");
describe('administration', function () {
    var navBarPage;
    beforeAll(function () {
        protractor_1.browser.get('/');
        protractor_1.browser.waitForAngular();
        navBarPage = new jhi_page_objects_1.NavBarPage(true);
        navBarPage.getSignInPage().loginWithOAuth('admin', 'admin');
        protractor_1.browser.waitForAngular();
    });
    beforeEach(function () {
        navBarPage.clickOnAdminMenu();
    });
    it('should load metrics', function () {
        navBarPage.clickOnAdmin('jhi-metrics');
        var expect1 = /metrics.title/;
        protractor_1.element.all(protractor_1.by.css('h2 span')).first().getAttribute('jhiTranslate').then(function (value) {
            expect(value).toMatch(expect1);
        });
    });
    it('should load health', function () {
        navBarPage.clickOnAdmin('jhi-health');
        var expect1 = /health.title/;
        protractor_1.element.all(protractor_1.by.css('h2 span')).first().getAttribute('jhiTranslate').then(function (value) {
            expect(value).toMatch(expect1);
        });
    });
    it('should load configuration', function () {
        navBarPage.clickOnAdmin('jhi-configuration');
        var expect1 = /configuration.title/;
        protractor_1.element.all(protractor_1.by.css('h2')).first().getAttribute('jhiTranslate').then(function (value) {
            expect(value).toMatch(expect1);
        });
    });
    it('should load audits', function () {
        navBarPage.clickOnAdmin('audits');
        var expect1 = /audits.title/;
        protractor_1.element.all(protractor_1.by.css('h2')).first().getAttribute('jhiTranslate').then(function (value) {
            expect(value).toMatch(expect1);
        });
    });
    it('should load logs', function () {
        navBarPage.clickOnAdmin('logs');
        var expect1 = /logs.title/;
        protractor_1.element.all(protractor_1.by.css('h2')).first().getAttribute('jhiTranslate').then(function (value) {
            expect(value).toMatch(expect1);
        });
    });
    afterAll(function () {
        navBarPage.autoSignOut();
    });
});
