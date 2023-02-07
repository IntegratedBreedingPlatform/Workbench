import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';

// import { JhiDateUtils } from 'ng-jhipster';
import { Sample } from './sample.model';
import { createRequestOption } from '../../shared';
import { map } from 'rxjs/operators';
import { ParamContext } from '../../shared/service/param.context';

export type EntityResponseType = HttpResponse<Sample>;

@Injectable()
export class SampleService {

    private resourceUrl;
    private resourceSearchUrl;

    constructor(
        private http: HttpClient,
        private context: ParamContext
    ) {
        this.setCropAndProgram(this.context.cropName, this.context.programUUID);
    }

    setCropAndProgram(crop: string, programUUID: string) {
        this.resourceUrl =  SERVER_API_URL + `crops/${crop}/programs/${programUUID}/samples`;
        this.resourceSearchUrl = this.resourceUrl;
    }

    create(sample: Sample): Observable<EntityResponseType> {
        const copy = this.convert(sample);
        return this.http.post<Sample>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertResponse(res)));
    }

    update(sample: Sample): Observable<EntityResponseType> {
        const copy = this.convert(sample);
        return this.http.put<Sample>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertResponse(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<Sample>(`${this.resourceUrl}/${id}`, { observe: 'response'})
            .pipe(map((res: EntityResponseType) => this.convertResponse(res)));
    }

    query(req?: any): Observable<HttpResponse<Sample[]>> {
        const options = createRequestOption(req);
        return this.http.get<Sample[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: HttpResponse<Sample[]>) => this.convertArrayResponse(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response'});
    }

    search(req?: any): Observable<HttpResponse<Sample[]>> {
        const options = createRequestOption(req);
        return this.http.get<Sample[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: HttpResponse<Sample[]>) => this.convertArrayResponse(res)));
    }

    removeEntries(sampleListId: number, selectedEntries: any) {
        const params = {};
        params['selectedEntries'] = selectedEntries;
        const url = SERVER_API_URL + `crops/${this.context.cropName}/sample-lists/${sampleListId}/entries?programUUID=` + this.context.programUUID;
        return this.http.delete<any>(url, { params, observe: 'response' });
    }

    private convertResponse(res: EntityResponseType): EntityResponseType {
        const body: Sample = this.convertItemFromServer(res.body);
        return res.clone({body});
    }

    private convertArrayResponse(res: HttpResponse<Sample[]>): HttpResponse<Sample[]> {
        const jsonResponse: Sample[] = res.body;
        const body: Sample[] = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            body.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return res.clone({body});
    }

    /**
     * Convert a returned JSON object to Sample.
     */
    private convertItemFromServer(sample: Sample): Sample {
        const copy: Sample = Object.assign({}, sample);
        // TODO for now it's a string
        /*
        copy.samplingDate = this.dateUtils
            .convertDateTimeFromServer(sample.samplingDate);
            */
        copy.id = sample.sampleId;
        return copy;
    }

    /**
     * Convert a Sample to a JSON which can be sent to the server.
     */
    private convert(sample: Sample): Sample {
        const copy: Sample = Object.assign({}, sample);

        /*
        copy.samplingDate = this.dateUtils.toDate(sample.samplingDate);
        */
        return copy;
    }
}
