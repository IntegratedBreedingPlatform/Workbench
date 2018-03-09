/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { BmsjHipsterTestModule } from '../../../test.module';
import { SampleListComponent } from '../../../../../../main/webapp/app/entities/sample-list/sample-list.component';
import { SampleListService } from '../../../../../../main/webapp/app/entities/sample-list/sample-list.service';
import { SampleList } from '../../../../../../main/webapp/app/entities/sample-list/sample-list.model';

describe('Component Tests', () => {

    describe('SampleList Management Component', () => {
        let comp: SampleListComponent;
        let fixture: ComponentFixture<SampleListComponent>;
        let service: SampleListService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule],
                declarations: [SampleListComponent],
                providers: [
                    SampleListService
                ]
            })
            .overrideTemplate(SampleListComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleListComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SampleListService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'query').and.returnValue(Observable.of(new HttpResponse({
                    body: [new SampleList(123)],
                    headers
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.query).toHaveBeenCalled();
                expect(comp.sampleLists[0]).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
