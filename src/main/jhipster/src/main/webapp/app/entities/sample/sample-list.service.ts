import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import {SERVER_API_URL} from '../../app.constants';
import {SampleList} from './sample-list.model';
import {Observable} from 'rxjs';
import {createRequestOption} from '../../shared';
import { map } from 'rxjs/operators';
import {ParamContext} from "../../shared/service/param.context";

@Injectable()
export class SampleListService {

    private resourceUrl;
    private programUUID: string;

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);
    }

    setCropAndProgram(crop: string, programUUID: string) {
        this.resourceUrl = SERVER_API_URL + `crops/${crop}/sample-lists`;
        this.programUUID = programUUID;
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
    private convertItemFromServer(sample: any): SampleList {
        const copy: SampleList = Object.assign({}, sample);
        return copy;
    }
}
