import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {SERVER_API_URL} from '../../app.constants';
import {SampleList} from './sample-list.model';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';

@Injectable()
export class SampleListService {

    private resourceUrl;

    constructor(
        private http: HttpClient
    ) { }

    setCrop(crop: string) {
        this.resourceUrl = SERVER_API_URL + `sampleLists/${crop}/search`;
    }

    search(params: any): Observable<HttpResponse<SampleList[]>> {
        const options = createRequestOption(params);

        return this.http.get<SampleList[]>(this.resourceUrl, {params: options, observe: 'response'})
            .map((res: HttpResponse<SampleList[]>) => this.convertArrayResponse(res));
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
