"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var protractor_1 = require("protractor");
var jhi_page_objects_1 = require("./../page-objects/jhi-page-objects");
describe('SampleList e2e test', function () {
    var navBarPage;
    var sampleListDialogPage;
    var sampleListComponentsPage;
    beforeAll(function () {
        protractor_1.browser.get('/');
        protractor_1.browser.waitForAngular();
        navBarPage = new jhi_page_objects_1.NavBarPage();
        navBarPage.getSignInPage().loginWithOAuth('admin', 'admin');
        protractor_1.browser.waitForAngular();
    });
    it('should load SampleLists', function () {
        navBarPage.goToEntity('sample-list');
        sampleListComponentsPage = new SampleListComponentsPage();
        expect(sampleListComponentsPage.getTitle())
            .toMatch(/bmsjHipsterApp.sampleList.home.title/);
    });
    it('should load create SampleList dialog', function () {
        sampleListComponentsPage.clickOnCreateButton();
        sampleListDialogPage = new SampleListDialogPage();
        expect(sampleListDialogPage.getModalTitle())
            .toMatch(/bmsjHipsterApp.sampleList.home.createOrEditLabel/);
        sampleListDialogPage.close();
    });
    it('should create and save SampleLists', function () {
        sampleListComponentsPage.clickOnCreateButton();
        sampleListDialogPage.setListNameInput('listName');
        expect(sampleListDialogPage.getListNameInput()).toMatch('listName');
        sampleListDialogPage.setDescriptionInput('description');
        expect(sampleListDialogPage.getDescriptionInput()).toMatch('description');
        sampleListDialogPage.setHierarchyInput('5');
        expect(sampleListDialogPage.getHierarchyInput()).toMatch('5');
        sampleListDialogPage.setCreatedDateInput(12310020012301);
        expect(sampleListDialogPage.getCreatedDateInput()).toMatch('2001-12-31T02:30');
        sampleListDialogPage.setNotesInput('notes');
        expect(sampleListDialogPage.getNotesInput()).toMatch('notes');
        sampleListDialogPage.setTypeInput('type');
        expect(sampleListDialogPage.getTypeInput()).toMatch('type');
        sampleListDialogPage.save();
        expect(sampleListDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });
    afterAll(function () {
        navBarPage.autoSignOut();
    });
});
var SampleListComponentsPage = /** @class */ (function () {
    function SampleListComponentsPage() {
        this.createButton = protractor_1.element(protractor_1.by.css('.jh-create-entity'));
        this.title = protractor_1.element.all(protractor_1.by.css('jhi-sample-list div h2 span')).first();
    }
    SampleListComponentsPage.prototype.clickOnCreateButton = function () {
        return this.createButton.click();
    };
    SampleListComponentsPage.prototype.getTitle = function () {
        return this.title.getAttribute('jhiTranslate');
    };
    return SampleListComponentsPage;
}());
exports.SampleListComponentsPage = SampleListComponentsPage;
var SampleListDialogPage = /** @class */ (function () {
    function SampleListDialogPage() {
        this.modalTitle = protractor_1.element(protractor_1.by.css('h4#mySampleListLabel'));
        this.saveButton = protractor_1.element(protractor_1.by.css('.modal-footer .btn.btn-primary'));
        this.closeButton = protractor_1.element(protractor_1.by.css('button.close'));
        this.listNameInput = protractor_1.element(protractor_1.by.css('input#field_listName'));
        this.descriptionInput = protractor_1.element(protractor_1.by.css('input#field_description'));
        this.hierarchyInput = protractor_1.element(protractor_1.by.css('input#field_hierarchy'));
        this.createdDateInput = protractor_1.element(protractor_1.by.css('input#field_createdDate'));
        this.notesInput = protractor_1.element(protractor_1.by.css('input#field_notes'));
        this.typeInput = protractor_1.element(protractor_1.by.css('input#field_type'));
        this.setListNameInput = function (listName) {
            this.listNameInput.sendKeys(listName);
        };
        this.getListNameInput = function () {
            return this.listNameInput.getAttribute('value');
        };
        this.setDescriptionInput = function (description) {
            this.descriptionInput.sendKeys(description);
        };
        this.getDescriptionInput = function () {
            return this.descriptionInput.getAttribute('value');
        };
        this.setHierarchyInput = function (hierarchy) {
            this.hierarchyInput.sendKeys(hierarchy);
        };
        this.getHierarchyInput = function () {
            return this.hierarchyInput.getAttribute('value');
        };
        this.setCreatedDateInput = function (createdDate) {
            this.createdDateInput.sendKeys(createdDate);
        };
        this.getCreatedDateInput = function () {
            return this.createdDateInput.getAttribute('value');
        };
        this.setNotesInput = function (notes) {
            this.notesInput.sendKeys(notes);
        };
        this.getNotesInput = function () {
            return this.notesInput.getAttribute('value');
        };
        this.setTypeInput = function (type) {
            this.typeInput.sendKeys(type);
        };
        this.getTypeInput = function () {
            return this.typeInput.getAttribute('value');
        };
    }
    SampleListDialogPage.prototype.getModalTitle = function () {
        return this.modalTitle.getAttribute('jhiTranslate');
    };
    SampleListDialogPage.prototype.save = function () {
        this.saveButton.click();
    };
    SampleListDialogPage.prototype.close = function () {
        this.closeButton.click();
    };
    SampleListDialogPage.prototype.getSaveButton = function () {
        return this.saveButton;
    };
    return SampleListDialogPage;
}());
exports.SampleListDialogPage = SampleListDialogPage;
