/* tslint:disable max-line-length */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BmsjHipsterTestModule } from '../../../test.module';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { SampleContext } from '../../../../../../main/webapp/app/entities/sample/sample.context';
import { SampleListService } from '../../../../../../main/webapp/app/entities/sample/sample-list.service';
import { SampleImportPlateMappingComponent } from '../../../../../../main/webapp/app/entities/sample/sample-import-plate-mapping.component';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertService } from '../../../../../../main/webapp/app/shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Component } from '@angular/core';
import { SampleImportPlateComponent } from '../../../../../../main/webapp/app/entities/sample/sample-import-plate.component';
import { MockNgbModalRef } from '../../../helpers/mock-ngb-modal-ref';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

describe('Component Tests', () => {

    describe('Sample Import Plate Mapping Component', () => {
        let comp: SampleImportPlateMappingComponent;
        let fixture: ComponentFixture<SampleImportPlateMappingComponent>;
        let sampleListService: SampleListService;
        let modalService: NgbModal;
        let alertService: AlertService;
        let eventManager: JhiEventManager;
        let sampleContext: SampleContext;
        let mockActiveModal: any;
        let translateService: TranslateService;
        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule, TranslateModule.forRoot()],
                declarations: [SampleImportPlateMappingComponent],
                providers: [
                    SampleContext,
                    SampleListService,
                    JhiEventManager,
                    SampleImportPlateComponent
                ]
            }).overrideComponent(SampleImportPlateMappingComponent, {
                set: {
                    styleUrls: []
                }
            }).overrideTemplate(SampleImportPlateMappingComponent, '')
                .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleImportPlateMappingComponent);
            comp = fixture.componentInstance;
            modalService = fixture.debugElement.injector.get(NgbModal);
            sampleContext = fixture.debugElement.injector.get(SampleContext);
            alertService = fixture.debugElement.injector.get(AlertService);
            eventManager = fixture.debugElement.injector.get(JhiEventManager);
            sampleListService = fixture.debugElement.injector.get(SampleListService);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
            translateService = fixture.debugElement.injector.get(TranslateService);
            sampleContext.activeList = { id: 1 };

            spyOn(alertService, 'error').and.callThrough();
            spyOn(alertService, 'success').and.callThrough();
        });

        it('should proceed with import', () => {

            spyOn(comp, 'validate').and.returnValue(true);
            spyOn(comp, 'buildSampleList').and.returnValue([{
                sampleBusinessKey: '3Z62SwmGyn3w3',
                plateId: '1',
                well: '1'
            }]);

            spyOn(comp, 'close').and.callThrough();
            spyOn(eventManager, 'broadcast').and.callThrough();
            spyOn(sampleListService, 'importPlateInfo').and.returnValue(of(''));

            comp.proceed();

            expect(eventManager.broadcast).toHaveBeenCalledWith({ name: 'sampleListModification', content: '' });
            expect(alertService.success).toHaveBeenCalledWith('bmsjHipsterApp.sample.importPlate.success');
            expect(comp.close).toHaveBeenCalled();

        });

        it('should not proceed with import if there is client validation error', () => {

            spyOn(comp, 'validate').and.returnValue(false);
            spyOn(comp, 'close').and.callThrough();
            spyOn(eventManager, 'broadcast').and.callThrough();
            spyOn(sampleListService, 'importPlateInfo').and.callThrough();

            comp.proceed();

            expect(sampleListService.importPlateInfo).toHaveBeenCalledTimes(0);
            expect(comp.close).toHaveBeenCalledTimes(0);
            expect(eventManager.broadcast).toHaveBeenCalledTimes(0);

        });

        it('should not proceed with import if there is server validation error', () => {

            const errorResponse = new HttpErrorResponse({
                status: 400,
                error: {
                    errors: [
                        {
                            message: 'errorMessage'
                        }
                    ]
                }
            });

            spyOn(comp, 'validate').and.returnValue(true);
            spyOn(comp, 'buildSampleList').and.returnValue([{
                sampleBusinessKey: '3Z62SwmGyn3w3',
                plateId: '1',
                well: '1'
            }]);

            spyOn(comp, 'close').and.callThrough();
            spyOn(eventManager, 'broadcast').and.callThrough();
            spyOn(sampleListService, 'importPlateInfo').and.returnValue(throwError(errorResponse));

            comp.proceed();

            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.error', { param: errorResponse.error.errors[0].message });
            expect(comp.close).toHaveBeenCalledTimes(0);
            expect(eventManager.broadcast).toHaveBeenCalledTimes(0);

        });

        it('should not proceed with import if there is Internal Server Error', () => {

            const errorResponse = new HttpErrorResponse({
                status: 500,
                error: {
                    errors: [
                        {
                            message: 'errorMessage'
                        }
                    ]
                }
            });

            spyOn(comp, 'validate').and.returnValue(true);
            spyOn(comp, 'buildSampleList').and.returnValue([{
                sampleBusinessKey: '3Z62SwmGyn3w3',
                plateId: '1',
                well: '1'
            }]);

            spyOn(comp, 'close').and.callThrough();
            spyOn(eventManager, 'broadcast').and.callThrough();
            spyOn(sampleListService, 'importPlateInfo').and.returnValue(throwError(errorResponse));

            comp.proceed();

            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.error', { param: errorResponse.error.errors[0].message });
            expect(comp.close).toHaveBeenCalledTimes(0);
            expect(eventManager.broadcast).toHaveBeenCalledTimes(0);

        });

        it('should close the modal window', () => {

            spyOn(comp, 'reset').and.callThrough();

            comp.close();

            expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
            expect(comp.reset).toHaveBeenCalled();

        });

        it('should validate selected mapping', () => {

            comp.importData = createTestImportData();

            comp.sampleIdMapping = 'Sample_Id';
            comp.plateIdMapping = 'Plate_Id';
            comp.wellMapping = 'Well';
            expect(comp.validate()).toBe(true);

            comp.reset();
            expect(comp.validate()).toBe(false);

            comp.sampleIdMapping = '';
            comp.plateIdMapping = 'Plate_Id';
            comp.wellMapping = 'Well';
            expect(comp.validate()).toBe(false);

            comp.sampleIdMapping = 'Sample_Id';
            comp.plateIdMapping = '';
            comp.wellMapping = 'Well';
            expect(comp.validate()).toBe(false);

            comp.sampleIdMapping = 'Sample_Id';
            comp.plateIdMapping = 'Plate_Id';
            comp.wellMapping = '';
            expect(comp.validate()).toBe(false);

            expect(alertService.error).toHaveBeenCalledTimes(4);
            expect(alertService.error).toHaveBeenCalledWith('error.custom', Object({ param: 'bmsjHipsterApp.sample.importPlate.headersNotMapped' }));

        });

        it('should validate import data if there is a missing value in sample id', () => {

            comp.sampleIdMapping = 'Sample_Id';
            comp.plateIdMapping = 'Plate_Id';
            comp.wellMapping = 'Well';
            comp.importData = createTestImportData();
            // add an empty row
            comp.importData.push(new Array<string>('', '', ''));

            expect(comp.validate()).toBe(false);

            expect(alertService.error).toHaveBeenCalledWith('error.custom', Object({ param: 'bmsjHipsterApp.sample.importPlate.recordHasNoSampleId' }));

        });

        it('should back the previous modal window', () => {

            spyOn(comp, 'reset').and.callThrough();
            const mockNgbModalRef = new MockNgbModalRef();
            spyOn(modalService, 'open').and.returnValue(new MockNgbModalRef());
            comp.back();

            expect(mockActiveModal.dismissSpy).toHaveBeenCalledWith('Back Import');
            expect(modalService.open).toHaveBeenCalledWith(SampleImportPlateComponent as Component, { size: 'lg', backdrop: 'static' });
            expect(comp.reset).toHaveBeenCalled();

        });

        function createTestImportData(): Array<Array<string>> {
            const data = new Array<Array<string>>();
            data.push(new Array<string>('Sample_Id', 'Plate_Id', 'Well'));
            data.push(new Array<string>('hasdjh', 'hsghad', 'hdajskdh'));
            data.push(new Array<string>('gdgjad', 'jdggfk', 'dsdddk'));
            return data;
        }
    });

});
