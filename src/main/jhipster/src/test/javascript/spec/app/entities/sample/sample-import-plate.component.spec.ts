import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BmsjHipsterTestModule } from '../../../test.module';
import { FileDownloadHelper } from '../../../../../../main/webapp/app/entities/sample/file-download.helper';
import { ExcelService } from '../../../../../../main/webapp/app/entities/sample/excel.service';
import { SampleImportPlateComponent } from '../../../../../../main/webapp/app/entities/sample/sample-import-plate.component';
import { Component, ElementRef } from '@angular/core';
import { AlertService } from '../../../../../../main/webapp/app/shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SampleImportPlateMappingComponent } from '../../../../../../main/webapp/app/entities/sample/sample-import-plate-mapping.component';
import { MockNgbModalRef } from '../../../helpers/mock-ngb-modal-ref';
import { TranslateModule, TranslateService, TranslateLoader, TranslateFakeLoader } from '@ngx-translate/core';

describe('Component Tests', () => {

    describe('Sample Import Plate Component', () => {
        let comp: SampleImportPlateComponent;
        let fixture: ComponentFixture<SampleImportPlateComponent>;
        let excelService: ExcelService;
        let modalService: NgbModal;
        let alertService: AlertService;
        let mockActiveModal: any;
        let translateService: TranslateService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule,
                    TranslateModule.forRoot({
                        loader: {
                            provide: TranslateLoader,
                            useClass: TranslateFakeLoader
                        }
                    })],
                declarations: [SampleImportPlateComponent],
                providers: [
                    FileDownloadHelper,
                    ExcelService,
                    SampleImportPlateMappingComponent
                ]
            }).overrideComponent(SampleImportPlateComponent, {
                set: {
                    styleUrls: []
                }
            }).overrideTemplate(SampleImportPlateComponent, '').compileComponents();

        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleImportPlateComponent);
            comp = fixture.componentInstance;
            modalService = fixture.debugElement.injector.get(NgbModal);
            excelService = fixture.debugElement.injector.get(ExcelService);
            alertService = fixture.debugElement.injector.get(AlertService);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
            translateService = fixture.debugElement.injector.get(TranslateService);

            spyOn(alertService, 'error').and.callThrough();

            comp.fileUpload = new ElementRef({ value: '', accept: '' });
        });

        it('should close the modal window.', () => {
            spyOn(comp, 'clearSelectedFile').and.callThrough();

            comp.close();
            expect(mockActiveModal.dismissSpy).toHaveBeenCalled();

            expect(comp.clearSelectedFile).toHaveBeenCalled();

        });

        it('should not open the import plate mapping modal if file is not valid', () => {
            spyOn(modalService, 'open').and.callThrough();
            spyOn(comp, 'validate').and.returnValue(false);
            comp.import();
            expect(modalService.open).toHaveBeenCalledTimes(0);

        });

        it('should open the import plate mapping modal', () => {
            spyOn(comp, 'validate').and.returnValue(true);
            const mockNgbModalRef = new MockNgbModalRef();
            mockNgbModalRef['importData'] = undefined;
            spyOn(modalService, 'open').and.returnValue(mockNgbModalRef);

            comp.import();
            expect(modalService.open).toHaveBeenCalledWith(SampleImportPlateMappingComponent as Component, { size: 'lg', backdrop: 'static' });
        });

        it('should clear the select file', () => {
            comp.fileUpload.nativeElement.value = 'fileName.csv';
            comp.fileName = 'fileName.csv';
            comp.importData = new Array<Array<string>>();
            comp.importData.push(new Array<any>('item'));

            comp.clearSelectedFile();

            expect(comp.fileUpload.nativeElement.value).toEqual('');
            expect(comp.fileName).toEqual('');
            expect(comp.importData.length).toEqual(0);

        });

        it('should change the file upload accept type', () => {
            spyOn(comp, 'clearSelectedFile').and.callThrough();
            comp.selectedFileType = '.csv';

            comp.onFileTypeChange();

            expect(comp.fileUpload.nativeElement.accept).toEqual(comp.selectedFileType);
            expect(comp.clearSelectedFile).toHaveBeenCalledTimes(0);

        });

        it('should clear file upload if no selected file type', () => {
            spyOn(comp, 'clearSelectedFile').and.callThrough();
            comp.selectedFileType = '';

            comp.onFileTypeChange();

            expect(comp.clearSelectedFile).toHaveBeenCalled();

        });

        it('should display error if no selected file format', () => {
            comp.selectedFileType = '';
            expect(comp.validate()).toEqual(false);
            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.importPlate.noSelectedFormat');

        });

        it('should display error if no file selected', () => {
            comp.selectedFileType = '.csv';
            comp.fileUpload.nativeElement.value = '';
            expect(comp.validate()).toEqual(false);
            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.importPlate.noFileSelected');

        });

        it('should display error if no file content', () => {
            comp.selectedFileType = '.csv';
            comp.fileUpload.nativeElement.value = 'fileName.csv';
            comp.importData.length = 0;
            expect(comp.validate()).toEqual(false);
            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.importPlate.noContent');

        });

        it('should not display error if valid', () => {
            comp.selectedFileType = '.csv';
            comp.fileUpload.nativeElement.value = 'fileName.csv';
            comp.importData.push(new Array<string>(''));
            expect(comp.validate()).toEqual(true);
            expect(alertService.error).toHaveBeenCalledTimes(0);

        });

    });

});
