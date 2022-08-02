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

@Injectable()
export class GenotypingBrapiService {

    baseUrl: string;
    accessToken: string;

    constructor(private http: HttpClient) {
    }

    searchStudies(searchStudiesRequest: SearchStudiesRequest): Observable<BrapiResponse<Study>> {
        return this.http.post<BrapiResponse<Study>>(`${this.baseUrl}/search/studies`, searchStudiesRequest, { headers: this.createHeader() });
    }

    searchGermplasm(searchGermplasmRequest: SearchGermplasmRequest): Observable<BrapiResponse<Germplasm>> {
        return this.http.post<BrapiResponse<Germplasm>>(`${this.baseUrl}/search/germplasm`, searchGermplasmRequest, { headers: this.createHeader() });
    }

    searchVariantsets(searchVariantsetRequest: SearchVariantsetRequest): Observable<BrapiResponse<VariantSet>> {
        return this.http.post<BrapiResponse<VariantSet>>(`${this.baseUrl}/search/variantsets`, searchVariantsetRequest, { headers: this.createHeader() });
    }

    searchCallsets(searchCallsetRequest: SearchCallsetsRequest): Observable<BrapiResponse<CallSet>> {
        return this.http.post<BrapiResponse<CallSet>>(`${this.baseUrl}/search/callsets`, searchCallsetRequest, { headers: this.createHeader() });
    }

    searchCalls(searchCallsRequest: SearchCallsRequest): Observable<BrapiResponse<Call>> {
        return this.http.post<BrapiResponse<Call>>(`${this.baseUrl}/search/calls`, searchCallsRequest, { headers: this.createHeader() });
    }

    searchSamples(searchSamplesRequest: SearchSamplesRequest): Observable<BrapiResponse<Sample>> {
        return this.http.post<BrapiResponse<Germplasm>>(`${this.baseUrl}/search/samples`, searchSamplesRequest, { headers: this.createHeader() });
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
