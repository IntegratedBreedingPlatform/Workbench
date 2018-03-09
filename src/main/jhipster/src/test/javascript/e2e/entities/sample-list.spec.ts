import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('SampleList e2e test', () => {

    let navBarPage: NavBarPage;
    let sampleListDialogPage: SampleListDialogPage;
    let sampleListComponentsPage: SampleListComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().loginWithOAuth('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load SampleLists', () => {
        navBarPage.goToEntity('sample-list');
        sampleListComponentsPage = new SampleListComponentsPage();
        expect(sampleListComponentsPage.getTitle())
            .toMatch(/bmsjHipsterApp.sampleList.home.title/);

    });

    it('should load create SampleList dialog', () => {
        sampleListComponentsPage.clickOnCreateButton();
        sampleListDialogPage = new SampleListDialogPage();
        expect(sampleListDialogPage.getModalTitle())
            .toMatch(/bmsjHipsterApp.sampleList.home.createOrEditLabel/);
        sampleListDialogPage.close();
    });

    it('should create and save SampleLists', () => {
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

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class SampleListComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-sample-list div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class SampleListDialogPage {
    modalTitle = element(by.css('h4#mySampleListLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    listNameInput = element(by.css('input#field_listName'));
    descriptionInput = element(by.css('input#field_description'));
    hierarchyInput = element(by.css('input#field_hierarchy'));
    createdDateInput = element(by.css('input#field_createdDate'));
    notesInput = element(by.css('input#field_notes'));
    typeInput = element(by.css('input#field_type'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setListNameInput = function(listName) {
        this.listNameInput.sendKeys(listName);
    };

    getListNameInput = function() {
        return this.listNameInput.getAttribute('value');
    };

    setDescriptionInput = function(description) {
        this.descriptionInput.sendKeys(description);
    };

    getDescriptionInput = function() {
        return this.descriptionInput.getAttribute('value');
    };

    setHierarchyInput = function(hierarchy) {
        this.hierarchyInput.sendKeys(hierarchy);
    };

    getHierarchyInput = function() {
        return this.hierarchyInput.getAttribute('value');
    };

    setCreatedDateInput = function(createdDate) {
        this.createdDateInput.sendKeys(createdDate);
    };

    getCreatedDateInput = function() {
        return this.createdDateInput.getAttribute('value');
    };

    setNotesInput = function(notes) {
        this.notesInput.sendKeys(notes);
    };

    getNotesInput = function() {
        return this.notesInput.getAttribute('value');
    };

    setTypeInput = function(type) {
        this.typeInput.sendKeys(type);
    };

    getTypeInput = function() {
        return this.typeInput.getAttribute('value');
    };

    save() {
        this.saveButton.click();
    }

    close() {
        this.closeButton.click();
    }

    getSaveButton() {
        return this.saveButton;
    }
}
