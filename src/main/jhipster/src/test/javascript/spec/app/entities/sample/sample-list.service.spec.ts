/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SERVER_API_URL } from '../../../../../../main/webapp/app/app.constants';
import {SampleListService} from '../../../../../../main/webapp/app/entities/sample/sample-list.service';
import {SampleList} from '../../../../../../main/webapp/app/entities/sample/sample-list.model';
import {ParamContext} from '../../../../../../main/webapp/app/shared/service/param.context';
import { ActivatedRoute, Router } from '@angular/router';
import { MockActivatedRoute, MockRouter } from '../../../helpers/mock-route.service';

declare const cropName: string;
declare const currentProgramId: string;

describe('Service Tests', () => {

    describe('Sample List Service', () => {
        let injector: TestBed;
        let paramContext: ParamContext;
        let service: SampleListService;
        let httpMock: HttpTestingController;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ],
                providers: [
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    ParamContext,
                    SampleListService,
                    { provide: Router, useClass: MockRouter }
                ]

            });
            injector = getTestBed();
            paramContext = injector.get(ParamContext);
            service = injector.get(SampleListService);
            httpMock = injector.get(HttpTestingController);
            paramContext.programUUID = currentProgramId;
            paramContext.cropName = cropName;
            service.setCropAndProgram(cropName, currentProgramId);
        });

        describe('Service methods', () => {

            const dummyParams = {
                searchString: 'Name',
                exactMatch: true,
                sort: []
            };

            it('should call correct URL', () => {

                service.search(dummyParams).subscribe(() => {});

                const req = httpMock.expectOne({ method: 'GET' });
                const resourceUrl = SERVER_API_URL + `crops/${cropName}/sample-lists/search`;

                expect(req.request.url).toEqual(resourceUrl);

                const expectedParams = 'searchString=Name&exactMatch=true';
                expect(req.request.params.toString()).toBe(expectedParams);
            });

            it('should return an array of SampleList', () => {

                const sampleLists = [];
                sampleLists.push(new SampleList(1, 'Name1', '', false, []));
                sampleLists.push(new SampleList(2, 'Name2', '', false, []));

                service.search(dummyParams).subscribe((received) => {
                    expect(received.body.length).toEqual(2);
                    expect(JSON.stringify(received.body) === JSON.stringify(sampleLists)).toBe(true);
                });

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush(sampleLists);
            });

            it('should handle error', () => {

                service.search(dummyParams).subscribe(null, (error: any) => {
                    expect(error.status).toEqual(404);
                });

                const req  = httpMock.expectOne({ method: 'GET' });
                req.flush('Invalid request parameters', {
                    status: 404, statusText: 'Bad Request'
                });

            });

            it('should call correct URL for download', () => {

                const listId = 1;
                const listName = 'listName';

                service.download(listId, listName).subscribe(() => {});

                const req = httpMock.expectOne({ method: 'GET' });
                const resourceUrl = SERVER_API_URL + `crops/${cropName}/sample-lists/${listId}/download`;

                expect(req.request.url).toEqual(resourceUrl);

                const expectedParams = `programUUID=${currentProgramId}&listName=${listName}`;
                expect(req.request.params.toString()).toBe(expectedParams);
            });
        });

        afterEach(() => {
            httpMock.verify();
        });

    });

});
