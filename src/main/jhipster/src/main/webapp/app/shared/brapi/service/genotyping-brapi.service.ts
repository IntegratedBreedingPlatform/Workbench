import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SearchStudiesRequest } from '../model/study/search-studies-request';
import { SearchGermplasmRequest } from '../model/germplasm/search-germplasm-request';
import { SearchVariantsetRequest } from '../model/variantsets/search-variantset-request';
import { SearchCallsetsRequest } from '../model/callsets/search-callsets-request';
import { SearchCallsRequest } from '../model/calls/search-calls-request';
import { BrapiResponse } from '../model/common/brapi-response';
import { Germplasm } from '../model/germplasm/germplasm';
import { Study } from '../model/study/study';
import { VariantSet } from '../model/variantsets/variantset';
import { CallSet } from '../model/callsets/callset';
import { Call } from '../model/calls/call';
import { SearchSamplesRequest } from '../model/samples/search-samples-request';
import { Sample } from '../model/samples/sample';
import { ExportFlapjackRequest } from '../model/export/export-flapjack-request';

@Injectable()
export class GenotypingBrapiService {

    brapiEndpoint: string;
    accessToken: string;
    baseUrl: string;

    constructor(private http: HttpClient) {
    }

    searchStudies(searchStudiesRequest: SearchStudiesRequest): Observable<BrapiResponse<Study>> {
        return this.http.post<BrapiResponse<Study>>(`${this.brapiEndpoint}/search/studies`, searchStudiesRequest, { headers: this.createHeader() });
    }

    searchGermplasm(searchGermplasmRequest: SearchGermplasmRequest): Observable<BrapiResponse<Germplasm>> {
        return this.http.post<BrapiResponse<Germplasm>>(`${this.brapiEndpoint}/search/germplasm`, searchGermplasmRequest, { headers: this.createHeader() });
    }

    searchVariantsets(searchVariantsetRequest: SearchVariantsetRequest): Observable<BrapiResponse<VariantSet>> {
        return this.http.post<BrapiResponse<VariantSet>>(`${this.brapiEndpoint}/search/variantsets`, searchVariantsetRequest, { headers: this.createHeader() });
    }

    searchCallsets(searchCallsetRequest: SearchCallsetsRequest): Observable<BrapiResponse<CallSet>> {
        return this.http.post<BrapiResponse<CallSet>>(`${this.brapiEndpoint}/search/callsets`, searchCallsetRequest, { headers: this.createHeader() });
    }

    searchCalls(searchCallsRequest: SearchCallsRequest): Observable<BrapiResponse<Call>> {
        return this.http.post<BrapiResponse<Call>>(`${this.brapiEndpoint}/search/calls`, searchCallsRequest, { headers: this.createHeader() });
    }

    searchSamples(searchSamplesRequest: SearchSamplesRequest): Observable<BrapiResponse<Sample>> {
        return this.http.post<BrapiResponse<Germplasm>>(`${this.brapiEndpoint}/search/samples`, searchSamplesRequest, { headers: this.createHeader() });
    }

    exportFlapjack(exportRequest: ExportFlapjackRequest): Observable<string> {
        return this.http.post<string>(`${this.baseUrl}/rest/gigwa/exportData`, exportRequest, {headers: this.createHeader(), responseType: 'text' as 'json'});
    }

    // FIXME: Find a way to have a separate instance of HttpClient with its own HttpInterceptor
    private createHeader() {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json; charset=UTF-8',
            'Authorization': `Bearer ${this.accessToken}`
        });
        return headers;
    }
}
