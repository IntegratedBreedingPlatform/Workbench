import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { SampleList } from './sample-list.model';
import { Observable } from 'rxjs';
import { createRequestOption } from '../../shared';
import { map } from 'rxjs/operators';

@Injectable()
export class SampleListService {

    private resourceUrl;
    private programUUID: string;

    constructor(
        private http: HttpClient
    ) { }

    setCropAndProgram(crop: string, programUUID: string) {
        this.resourceUrl = SERVER_API_URL + `crops/${crop}/sample-lists`;
        this.programUUID = programUUID;
    }

    getById(sampleListId: number): Observable<HttpResponse<SampleList>> {
        return this.http.get<SampleList>(`${this.resourceUrl}/${sampleListId}`, { observe: 'response' })
            .pipe(map((res) => this.convertResponse(res)));
    }

    search(params: any): Observable<HttpResponse<SampleList[]>> {
        const options = createRequestOption(params);

        return this.http.get<SampleList[]>(`${this.resourceUrl}/search`, {params: options, observe: 'response'})
            .pipe(map((res: HttpResponse<SampleList[]>) => this.convertArrayResponse(res)));
    }

    download(listId: number, listName: string): Observable<HttpResponse<Blob>> {
        const options: HttpParams = new HttpParams()
            .append('programUUID', this.programUUID)
            .append('listName', listName);
        return this.http
            .get(`${this.resourceUrl}/${listId}/download`, {
                params: options,
                responseType: 'blob',
                observe: 'response'
            });
    }

    importPlateInfo(listId: number, sampleList: any) {
        const options: HttpParams = new HttpParams()
            .append('programUUID', this.programUUID);
        return this.http.patch(`${this.resourceUrl}/${listId}/samples`, sampleList, {
            params: options});
    }

    submitToGOBii(sampleListId: number) {
        return this.http.post(`${this.resourceUrl}/${sampleListId}/submitToGOBii`, null);
    }

    private convertResponse(res: HttpResponse<SampleList>): HttpResponse<SampleList> {
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
    private convertItemFromServer(sampleList: any): SampleList {
        const copy: SampleList = Object.assign({}, sampleList);
        return copy;
    }
}
