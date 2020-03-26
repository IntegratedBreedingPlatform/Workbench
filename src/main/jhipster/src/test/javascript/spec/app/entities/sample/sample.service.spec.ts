/* tslint:disable max-line-length */
import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { JhiDateUtils } from 'ng-jhipster';

import { SampleService } from '../../../../../../main/webapp/app/entities/sample/sample.service';
import { SERVER_API_URL } from '../../../../../../main/webapp/app/app.constants';

declare const cropName: string;
declare const currentProgramId: string;

describe('Service Tests', () => {

    describe('Sample Service', () => {
        let injector: TestBed;
        let service: SampleService;
        let httpMock: HttpTestingController;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    HttpClientTestingModule
                ],
                providers: [
                    JhiDateUtils,
                    SampleService
                ]
            });
            injector = getTestBed();
            service = injector.get(SampleService);
            httpMock = injector.get(HttpTestingController);
            service.setCropAndProgram(cropName, currentProgramId);
        });

        describe('Service methods', () => {
            it('should call correct URL', () => {
                service.find(123).subscribe(() => {});

                const req  = httpMock.expectOne({ method: 'GET' });

                const resourceUrl = SERVER_API_URL + `crops/${cropName}/programs/${currentProgramId}/samples`;
                expect(req.request.url).toEqual(resourceUrl + '/' + 123);
            });
            it('should return Sample', () => {

                service.find(123).subscribe((received) => {
                    expect(received.body.sampleId).toEqual(123);
                });

                const req = httpMock.expectOne({ method: 'GET' });
                req.flush({sampleId: 123});
            });

            it('should propagate not found response', () => {

                service.find(123).subscribe(null, (_error: any) => {
                    expect(_error.status).toEqual(404);
                });

                const req  = httpMock.expectOne({ method: 'GET' });
                req.flush('Invalid request parameters', {
                    status: 404, statusText: 'Bad Request'
                });

            });
        });

        afterEach(() => {
            httpMock.verify();
        });

    });

});
