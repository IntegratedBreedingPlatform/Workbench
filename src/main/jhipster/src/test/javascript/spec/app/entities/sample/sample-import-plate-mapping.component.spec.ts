import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BmsjHipsterTestModule } from '../../../test.module';
import { AppModalService } from '../../../../../../main/webapp/app/shared/modal/app-modal.service';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { SampleContext } from '../../../../../../main/webapp/app/entities/sample/sample.context';
import { SampleListService } from '../../../../../../main/webapp/app/entities/sample/sample-list.service';
import { SampleImportPlateMappingComponent } from '../../../../../../main/webapp/app/entities/sample/sample-import-plate-mapping.component';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertService } from '../../../../../../main/webapp/app/shared/alert/alert.service';

describe('Component Tests', () => {

    describe('Sample Import Plate Mapping Component', () => {
        let comp: SampleImportPlateMappingComponent;
        let fixture: ComponentFixture<SampleImportPlateMappingComponent>;
        let sampleListService: SampleListService;
        let modalService: AppModalService;
        let jhiAlertService: JhiAlertService;
        let alertService: AlertService;
        let eventManager: JhiEventManager;
        let sampleContext: SampleContext;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule],
                declarations: [SampleImportPlateMappingComponent],
                providers: [
                    AppModalService,
                    SampleContext,
                    SampleListService,
                    JhiEventManager
                ]
            }).overrideComponent(SampleImportPlateMappingComponent, {
                set: {
                    styleUrls: []
                }
            }).overrideTemplate(SampleImportPlateMappingComponent, '').compileComponents();

        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleImportPlateMappingComponent);
            comp = fixture.componentInstance;
            modalService = fixture.debugElement.injector.get(AppModalService);
            sampleContext = fixture.debugElement.injector.get(SampleContext);
            jhiAlertService = fixture.debugElement.injector.get(JhiAlertService);
            alertService = fixture.debugElement.injector.get(AlertService);
            eventManager = fixture.debugElement.injector.get(JhiEventManager);
            sampleListService = fixture.debugElement.injector.get(SampleListService);

            sampleContext.activeList = { id: 1 };

            spyOn(modalService, 'close').and.callThrough();
            spyOn(modalService, 'open').and.callThrough();
            spyOn(alertService, 'error').and.callThrough();
            spyOn(alertService, 'success').and.callThrough();
            spyOn(jhiAlertService, 'error').and.callThrough();
            spyOn(jhiAlertService, 'success').and.callThrough();
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

            expect(eventManager.broadcast).toHaveBeenCalledWith({name: 'sampleListModification', content: ''});
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

            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.error', { param : errorResponse.error.errors[0].message});
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

            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.error', { param : errorResponse.error.errors[0].message});
            expect(comp.close).toHaveBeenCalledTimes(0);
            expect(eventManager.broadcast).toHaveBeenCalledTimes(0);

        });

        it('should close the modal window', () => {

            spyOn(comp, 'reset').and.callThrough();
            spyOn(comp.onClose, 'emit').and.callThrough();

            comp.close();

            expect(modalService.close).toHaveBeenCalledWith(comp.modalId);
            expect(comp.reset).toHaveBeenCalled();
            expect(comp.onClose.emit).toHaveBeenCalled();

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
            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.importPlate.headersNotMapped');

        });

        it('should validate import data if there is a missing value in sample id', () => {

            comp.sampleIdMapping = 'Sample_Id';
            comp.plateIdMapping = 'Plate_Id';
            comp.wellMapping = 'Well';
            comp.importData = createTestImportData();
            // add an empty row
            comp.importData.push(new Array<string>('', '', ''));

            expect(comp.validate()).toBe(false);

            expect(alertService.error).toHaveBeenCalledWith('bmsjHipsterApp.sample.importPlate.recordHasNoSampleId');

        });

        function createTestImportData(): Array<Array<string>> {
            const data = new Array<Array<string>>();
            data.push(new Array<string>('Sample_Id', 'Plate_Id', 'Well'));
            data.push(new Array<string>('hasdjh', 'hsghad', 'hdajskdh'));
            data.push(new Array<string>('gdgjad', 'jdggfk', 'dsdddk'));
            return data;
        }

        it('should back the previous modal window', () => {

            spyOn(comp, 'reset').and.callThrough();
            spyOn(comp.onBack, 'emit').and.callThrough();

            comp.back();

            expect(modalService.close).toHaveBeenCalledWith(comp.modalId);
            expect(modalService.open).toHaveBeenCalledWith('import-plate-modal');

            expect(comp.reset).toHaveBeenCalled();
            expect(comp.onBack.emit).toHaveBeenCalled();

        });
    });

});
