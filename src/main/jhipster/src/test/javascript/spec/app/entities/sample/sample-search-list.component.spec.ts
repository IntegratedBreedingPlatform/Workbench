/* tslint:disable max-line-length */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { BmsjHipsterTestModule } from '../../../test.module';
import { SampleList } from '../../../../../../main/webapp/app/entities/sample/sample-list.model';
import { SampleSearchListComponent } from '../../../../../../main/webapp/app/entities/sample/sample-search-list.component';
import { SampleListService } from '../../../../../../main/webapp/app/entities/sample/sample-list.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { ParamContext } from '../../../../../../main/webapp/app/shared/service/param.context';

declare const cropName: string;
declare const currentProgramId: string;

describe('Component Tests', () => {

    describe('Search Sample List Component', () => {

        let comp: SampleSearchListComponent;
        let fixture: ComponentFixture<SampleSearchListComponent>;
        let service: SampleListService;
        let paramContext: ParamContext;
        let router: Router;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule],
                declarations: [SampleSearchListComponent],
                providers: [
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    ParamContext,
                    SampleListService,
                    { provide: Router, useClass: MockRouter }
                ]
            }).overrideTemplate(SampleSearchListComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleSearchListComponent);
            comp = fixture.componentInstance;
            paramContext = fixture.debugElement.injector.get(ParamContext);
            // FIXME see global.js
            paramContext.programUUID = currentProgramId;
            paramContext.cropName = cropName;
            service = fixture.debugElement.injector.get(SampleListService);
            router = fixture.debugElement.injector.get(Router);
        });

        describe('Test Methods', () => {
            it('Should load SampleList on search', () => {

                comp.searchString = 'search term';

                const expectedSampleList = new SampleList(1, 'listName', '', false, []);
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'search').and.returnValue(of(new HttpResponse({
                    body: [expectedSampleList],
                    headers
                })));

                comp.searchList();

                expect(service.search).toHaveBeenCalled();
                expect(comp.sampleListResults).toEqual([expectedSampleList]);
            });

            it('Should load the selected SampleList', () => {

               comp.selectedListId = 1;

               const selectedSampleList = new SampleList(1, 'listName', '', false, []);
               comp.selectList(selectedSampleList);

               expect(router.navigate).toHaveBeenCalledWith([ '/sample-manager' ], Object({ queryParams: Object({ listId: 1 }) }));

            });

            it('Should reset the form', () => {

                comp.searchString = 'MyList';
                comp.exactMatch = true;
                comp.sampleListResults = [new SampleList(1, 'listName', '', false, [])];
                comp.selectedListId = 1;

                comp.reset();

                expect(comp.searchString).toEqual('');
                expect(comp.exactMatch).toEqual(false);
                expect(comp.sampleListResults).toEqual([]);
                expect(comp.selectedListId).toEqual(0);

            });

            it('Should hide the help poup', () => {

                comp.displayHelpPopup = true;
                comp.hideHelpPopup();

                expect(comp.displayHelpPopup).toEqual(false);

            });
        });
    });
});

class MockRouter {
    navigate = jasmine.createSpy('navigate');
}
