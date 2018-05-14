import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import {SERVER_API_URL} from '../../app.constants';
import {SampleList} from './sample-list.model';
import {Observable} from 'rxjs/Observable';
import {Sample} from "./sample.model";
import {EntityResponseType} from "./sample.service";

@Injectable()
export class SampleListService {

    private resourceUrl;

    constructor(
        private http: HttpClient
    ) { }

    setCrop(crop: string) {
        this.resourceUrl = SERVER_API_URL + `sampleLists/${crop}/search`;
    }

    search(searchString: string, exactMatch: boolean): Observable<HttpResponse<SampleList[]>> {
        const params = new HttpParams()
            .append('searchString', searchString)
            .append('exactMatch', 'true');

        return this.http.get<SampleList[]>(this.resourceUrl, {params: params, observe: 'response'})
            .map((res: HttpResponse<SampleList[]>) => this.convertArrayResponse(res));
    }

    private convertArrayResponse(res: HttpResponse<SampleList[]>): HttpResponse<SampleList[]> {
        const jsonResponse: Sample[] = res.body;
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
