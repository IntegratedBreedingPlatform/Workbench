/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { BmsjHipsterTestModule } from '../../../test.module';
import { SampleListDetailComponent } from '../../../../../../main/webapp/app/entities/sample-list/sample-list-detail.component';
import { SampleListService } from '../../../../../../main/webapp/app/entities/sample-list/sample-list.service';
import { SampleList } from '../../../../../../main/webapp/app/entities/sample-list/sample-list.model';

describe('Component Tests', () => {

    describe('SampleList Management Detail Component', () => {
        let comp: SampleListDetailComponent;
        let fixture: ComponentFixture<SampleListDetailComponent>;
        let service: SampleListService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule],
                declarations: [SampleListDetailComponent],
                providers: [
                    SampleListService
                ]
            })
            .overrideTemplate(SampleListDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleListDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SampleListService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(Observable.of(new HttpResponse({
                    body: new SampleList(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.sampleList).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
