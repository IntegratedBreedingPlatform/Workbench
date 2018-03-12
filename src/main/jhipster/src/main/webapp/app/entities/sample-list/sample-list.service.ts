import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

import { JhiDateUtils } from 'ng-jhipster';

import { SampleList } from './sample-list.model';
import { createRequestOption } from '../../shared';

export type EntityResponseType = HttpResponse<SampleList>;

@Injectable()
export class SampleListService {

    private resourceUrl =  SERVER_API_URL + 'sample/maize/samples?listId=4';
    private resourceSearchUrl = this.resourceUrl;

    constructor(private http: HttpClient, private dateUtils: JhiDateUtils) { }

    create(sampleList: SampleList): Observable<EntityResponseType> {
        const copy = this.convert(sampleList);
        return this.http.post<SampleList>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    update(sampleList: SampleList): Observable<EntityResponseType> {
        const copy = this.convert(sampleList);
        return this.http.put<SampleList>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<SampleList>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .map((res: EntityResponseType) => this.convertResponse(res));
    }

    query(req?: any): Observable<HttpResponse<SampleList[]>> {
        const options = createRequestOption(req);
        return this.http.get<SampleList[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<SampleList[]>) => this.convertArrayResponse(res));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    search(req?: any): Observable<HttpResponse<SampleList[]>> {
        const options = createRequestOption(req);
        return this.http.get<SampleList[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .map((res: HttpResponse<SampleList[]>) => this.convertArrayResponse(res));
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: SampleList = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<SampleList[]>): HttpResponse<SampleList[]> {
        const jsonResponse: SampleList[] = res.body;
        const body: SampleList[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to SampleList.
     */
    private convertItemFromServer(sampleList: SampleList): SampleList {
        const copy: SampleList = Object.assign({}, sampleList);
        copy.createdDate = this.dateUtils
            .convertDateTimeFromServer(sampleList.createdDate);
        return copy;
    }

    /**
     * Convert a SampleList to a JSON which can be sent to the server.
     */
    private convert(sampleList: SampleList): SampleList {
        const copy: SampleList = Object.assign({}, sampleList);

        copy.createdDate = this.dateUtils.toDate(sampleList.createdDate);
        return copy;
    }
}
