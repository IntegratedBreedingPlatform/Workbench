/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { BmsjHipsterTestModule } from '../../../test.module';
import { SampleComponent } from '../../../../../../main/webapp/app/entities/sample/sample.component';
import { SampleService } from '../../../../../../main/webapp/app/entities/sample/sample.service';
import { Sample } from '../../../../../../main/webapp/app/entities/sample/sample.model';
import { SampleList } from '../../../../../../main/webapp/app/entities/sample/sample-list.model';
import { SampleListService } from '../../../../../../main/webapp/app/entities/sample/sample-list.service';
import { FileDownloadHelper } from '../../../../../../main/webapp/app/entities/sample/file-download.helper';

describe('Component Tests', () => {

    describe('Sample Management Component', () => {
        let comp: SampleComponent;
        let fixture: ComponentFixture<SampleComponent>;
        let sampleService: SampleService;
        let sampleListService: SampleListService;
        let fileDownloadHelper: FileDownloadHelper;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule],
                declarations: [SampleComponent],
                providers: [
                    SampleService,
                    SampleListService,
                    FileDownloadHelper
                ]
            })
            .overrideTemplate(SampleComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleComponent);
            comp = fixture.componentInstance;
            sampleService = fixture.debugElement.injector.get(SampleService);
            sampleListService = fixture.debugElement.injector.get(SampleListService);
            fileDownloadHelper = fixture.debugElement.injector.get(FileDownloadHelper);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                comp.sampleList = new SampleList(1, 'name', '', true, null);
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(sampleService, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new Sample(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(sampleService.query).toHaveBeenCalled();
                expect(comp.sampleList.samples[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });

        describe('When exporting', () => {

            it('Should download the csv file', () => {

                const listId = 1;
                const listName = 'name';
                const fileName = 'name.csv';

                comp.sampleList = new SampleList(listId, listName, '', true, null);

                const httpResponse = new HttpResponse({
                    body: [new Blob()],
                    headers: new HttpHeaders()
                });
                spyOn(sampleListService, 'download').and.callFake(function(arg) {
                    if (arg === 'listId') {
                        return listId;
                    } else if (arg === 'anotherValue') {
                        return listName;
                    }
                }).and.returnValue(Observable.of(httpResponse));

                spyOn(fileDownloadHelper, 'getFileNameFromResponseContentDisposition').and.returnValue(fileName);
                spyOn(fileDownloadHelper, 'save').and.callThrough();

                comp.export();

                expect(sampleListService.download).toHaveBeenCalled();
                expect(fileDownloadHelper.getFileNameFromResponseContentDisposition).toHaveBeenCalled();
                expect(fileDownloadHelper.save).toHaveBeenCalled();

            });

        });
    });

});
