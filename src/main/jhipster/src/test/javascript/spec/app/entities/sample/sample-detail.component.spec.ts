/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { BmsjHipsterTestModule } from '../../../test.module';
import { SampleDetailComponent } from '../../../../../../main/webapp/app/entities/sample/sample-detail.component';
import { SampleService } from '../../../../../../main/webapp/app/entities/sample/sample.service';
import { Sample } from '../../../../../../main/webapp/app/entities/sample/sample.model';

describe('Component Tests', () => {

    describe('Sample Management Detail Component', () => {
        let comp: SampleDetailComponent;
        let fixture: ComponentFixture<SampleDetailComponent>;
        let service: SampleService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule],
                declarations: [SampleDetailComponent],
                providers: [
                    SampleService
                ]
            })
            .overrideTemplate(SampleDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SampleService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                spyOn(service, 'find').and.returnValue(of(new HttpResponse({
                    body: new Sample(123)
                })));

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(service.find).toHaveBeenCalledWith(123);
                expect(comp.sample).toEqual(jasmine.objectContaining({id: 123}));
            });
        });
    });

});
