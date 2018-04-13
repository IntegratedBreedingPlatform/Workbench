import { browser, element, by } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';

describe('Sample e2e test', () => {

    let navBarPage: NavBarPage;
    let sampleDialogPage: SampleDialogPage;
    let sampleComponentsPage: SampleComponentsPage;

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Samples', () => {
        navBarPage.goToEntity('sample');
        sampleComponentsPage = new SampleComponentsPage();
        expect(sampleComponentsPage.getTitle())
            .toMatch(/bmsjHipsterApp.sample.home.title/);

    });

    it('should load create Sample dialog', () => {
        sampleComponentsPage.clickOnCreateButton();
        sampleDialogPage = new SampleDialogPage();
        expect(sampleDialogPage.getModalTitle())
            .toMatch(/bmsjHipsterApp.sample.home.createOrEditLabel/);
        sampleDialogPage.close();
    });

    it('should create and save Samples', () => {
        sampleComponentsPage.clickOnCreateButton();
        sampleDialogPage.setSampleNameInput('sampleName');
        expect(sampleDialogPage.getSampleNameInput()).toMatch('sampleName');
        sampleDialogPage.setSampleBusinessKeyInput('sampleBusinessKey');
        expect(sampleDialogPage.getSampleBusinessKeyInput()).toMatch('sampleBusinessKey');
        sampleDialogPage.setTakenByInput('takenBy');
        expect(sampleDialogPage.getTakenByInput()).toMatch('takenBy');
        sampleDialogPage.setPlantNumberInput('5');
        expect(sampleDialogPage.getPlantNumberInput()).toMatch('5');
        sampleDialogPage.setPlantBusinessKeyInput('plantBusinessKey');
        expect(sampleDialogPage.getPlantBusinessKeyInput()).toMatch('plantBusinessKey');
        sampleDialogPage.setSamplingDateInput(12310020012301);
        expect(sampleDialogPage.getSamplingDateInput()).toMatch('2001-12-31T02:30');
        sampleDialogPage.save();
        expect(sampleDialogPage.getSaveButton().isPresent()).toBeFalsy();
    });

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class SampleComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-sample div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class SampleDialogPage {
    modalTitle = element(by.css('h4#mySampleLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    sampleNameInput = element(by.css('input#field_sampleName'));
    sampleBusinessKeyInput = element(by.css('input#field_sampleBusinessKey'));
    takenByInput = element(by.css('input#field_takenBy'));
    plantNumberInput = element(by.css('input#field_plantNumber'));
    plantBusinessKeyInput = element(by.css('input#field_plantBusinessKey'));
    samplingDateInput = element(by.css('input#field_samplingDate'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setSampleNameInput = function(sampleName) {
        this.sampleNameInput.sendKeys(sampleName);
    };

    getSampleNameInput = function() {
        return this.sampleNameInput.getAttribute('value');
    };

    setSampleBusinessKeyInput = function(sampleBusinessKey) {
        this.sampleBusinessKeyInput.sendKeys(sampleBusinessKey);
    };

    getSampleBusinessKeyInput = function() {
        return this.sampleBusinessKeyInput.getAttribute('value');
    };

    setTakenByInput = function(takenBy) {
        this.takenByInput.sendKeys(takenBy);
    };

    getTakenByInput = function() {
        return this.takenByInput.getAttribute('value');
    };

    setPlantNumberInput = function(plantNumber) {
        this.plantNumberInput.sendKeys(plantNumber);
    };

    getPlantNumberInput = function() {
        return this.plantNumberInput.getAttribute('value');
    };

    setPlantBusinessKeyInput = function(plantBusinessKey) {
        this.plantBusinessKeyInput.sendKeys(plantBusinessKey);
    };

    getPlantBusinessKeyInput = function() {
        return this.plantBusinessKeyInput.getAttribute('value');
    };

    setSamplingDateInput = function(samplingDate) {
        this.samplingDateInput.sendKeys(samplingDate);
    };

    getSamplingDateInput = function() {
        return this.samplingDateInput.getAttribute('value');
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
