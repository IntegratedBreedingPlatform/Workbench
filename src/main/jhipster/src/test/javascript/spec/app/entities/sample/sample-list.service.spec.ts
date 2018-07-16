/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SERVER_API_URL } from '../../../../../../main/webapp/app/app.constants';
import {SampleListService} from '../../../../../../main/webapp/app/entities/sample/sample-list.service';
import {SampleList} from '../../../../../../main/webapp/app/entities/sample/sample-list.model';

describe('Service Tests', () => {

    describe('Sample List Service', () => {
        let injector: TestBed;
        let service: SampleListService;
        let httpMock: HttpTestingController;
        const crop = 'maize';

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ],
                providers: [
                    SampleListService
                ]

            });
            injector = getTestBed();
            service = injector.get(SampleListService);
            httpMock = injector.get(HttpTestingController);
            service.setCrop(crop)
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
                const resourceUrl = SERVER_API_URL + `sampleLists/${crop}/search`;

                console.log('myurl:' + req.request.url);
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
                const resourceUrl = SERVER_API_URL + `sampleLists/${crop}/download`;

                console.log('myurl:' + req.request.url);
                expect(req.request.url).toEqual(resourceUrl);

                const expectedParams = 'listId=1&listName=listName';
                expect(req.request.params.toString()).toBe(expectedParams);
            });
        });

        afterEach(() => {
            httpMock.verify();
        });

    });

});
