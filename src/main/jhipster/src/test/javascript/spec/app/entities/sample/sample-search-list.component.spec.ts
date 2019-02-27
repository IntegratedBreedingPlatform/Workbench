/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { Observable } from 'rxjs/Observable';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { BmsjHipsterTestModule } from '../../../test.module';
import { SampleList } from '../../../../../../main/webapp/app/entities/sample/sample-list.model';
import {SampleSearchListComponent} from '../../../../../../main/webapp/app/entities/sample/sample-search-list.component';
import {SampleListService} from '../../../../../../main/webapp/app/entities/sample/sample-list.service';
import {Router} from '@angular/router';

declare const cropName: string;

describe('Component Tests', () => {

    describe('Search Sample List Component', () => {

        let comp: SampleSearchListComponent;
        let fixture: ComponentFixture<SampleSearchListComponent>;
        let service: SampleListService;
        let router: Router;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BmsjHipsterTestModule],
                declarations: [SampleSearchListComponent],
                providers: [
                    SampleListService,
                    { provide: Router, useClass: MockRouter }
                ]
            }).overrideTemplate(SampleSearchListComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SampleSearchListComponent);
            comp = fixture.componentInstance;
            comp.setCrop(cropName);
            service = fixture.debugElement.injector.get(SampleListService);
            router = fixture.debugElement.injector.get(Router);
        });

        describe('Test Methods', () => {
            it('Should load SampleList on search', () => {

                comp.searchString = 'search term';

                const expectedSampleList = new SampleList(1, 'listName', '', false, []);
                const headers = new HttpHeaders().append('link', 'link;link');
                spyOn(service, 'search').and.returnValue(Observable.of(new HttpResponse({
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
