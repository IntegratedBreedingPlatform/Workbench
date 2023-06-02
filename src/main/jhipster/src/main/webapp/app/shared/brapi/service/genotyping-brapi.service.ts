import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SearchStudiesRequest } from '../model/study/search-studies-request';
import { SearchGermplasmRequest } from '../model/germplasm/search-germplasm-request';
import { SearchVariantsetRequest } from '../model/variantsets/search-variantset-request';
import { SearchCallsetsRequest } from '../model/callsets/search-callsets-request';
import { SearchCallsRequest } from '../model/calls/search-calls-request';
import { BrapiListResponse } from '../model/common/brapi-list-response';
import { Germplasm } from '../model/germplasm/germplasm';
import { Study } from '../model/study/study';
import { VariantSet } from '../model/variantsets/variantset';
import { CallSet } from '../model/callsets/callset';
import { Call } from '../model/calls/call';
import { SearchSamplesRequest } from '../model/samples/search-samples-request';
import { Sample } from '../model/samples/sample';
import { GigwaExportRequest } from '../model/export/gigwa-export-request';
import { SearchVariantRequest } from '../model/variants/search-variant-request';
import { Variant } from '../model/variants/variant';
import { getBrapiAllRecords } from '../get-brapi-all-records';
import { BrapiListResponseTokenBasedPagination } from '../model/common/brapi-list-response-token-based-pagination';
import { getBrapiAllRecordsTokenBased } from '../get-brapi-all-records-token-based';
import { GA4GHSearchRequest } from '../model/export/ga4gh-search-request';

@Injectable()
export class GenotypingBrapiService {

    brapiEndpoint: string;
    accessToken: string;
    baseUrl: string;

    constructor(private http: HttpClient) {
    }

    searchStudies(searchStudiesRequest: SearchStudiesRequest): Observable<BrapiListResponse<Study>> {
        return this.http.post<BrapiListResponse<Study>>(`${this.brapiEndpoint}/search/studies`, searchStudiesRequest, { headers: this.createHeader() });
    }

    searchGermplasm(searchGermplasmRequest: SearchGermplasmRequest): Observable<BrapiListResponse<Germplasm>> {
        return this.http.post<BrapiListResponse<Germplasm>>(`${this.brapiEndpoint}/search/germplasm`, searchGermplasmRequest, { headers: this.createHeader() });
    }

    searchVariantsets(searchVariantsetRequest: SearchVariantsetRequest): Observable<BrapiListResponse<VariantSet>> {
        return this.http.post<BrapiListResponse<VariantSet>>(`${this.brapiEndpoint}/search/variantsets`, searchVariantsetRequest, { headers: this.createHeader() });
    }

    searchVariantsetsGetAll(searchVariantsetRequest: SearchVariantsetRequest): Observable<VariantSet[]> {
        return getBrapiAllRecords<VariantSet>((page, pageSize) => {
            searchVariantsetRequest.pageSize = pageSize;
            searchVariantsetRequest.page = page
            return this.searchVariantsets(searchVariantsetRequest);
        });
    }

    searchVariants(searchVariantRequest: SearchVariantRequest): Observable<BrapiListResponseTokenBasedPagination<Variant>> {
        return this.http.post<BrapiListResponseTokenBasedPagination<Variant>>(`${this.brapiEndpoint}/search/variants`, searchVariantRequest, { headers: this.createHeader() });
    }

    searchVariantsGetAll(searchVariantRequest: SearchVariantRequest): Observable<Variant[]> {
        return getBrapiAllRecordsTokenBased<Variant>((pageToken, pageSize) => {
            searchVariantRequest.pageSize = pageSize;
            searchVariantRequest.pageToken = String(pageToken);
            return this.searchVariants(searchVariantRequest);
        });
    }

    searchCallsets(searchCallsetRequest: SearchCallsetsRequest): Observable<BrapiListResponse<CallSet>> {
        return this.http.post<BrapiListResponse<CallSet>>(`${this.brapiEndpoint}/search/callsets`, searchCallsetRequest, { headers: this.createHeader() });
    }

    searchCallsetsGetAll(searchCallsetRequest: SearchCallsetsRequest): Observable<CallSet[]> {
        return getBrapiAllRecords<CallSet>((page, pageSize) => {
            searchCallsetRequest.pageSize = pageSize;
            searchCallsetRequest.page = page
            return this.searchCallsets(searchCallsetRequest);
        });
    }

    searchCalls(searchCallsRequest: SearchCallsRequest): Observable<BrapiListResponse<Call>> {
        return this.http.post<BrapiListResponse<Call>>(`${this.brapiEndpoint}/search/calls`, searchCallsRequest, { headers: this.createHeader() });
    }

    searchCallsGetAll(searchCallsRequest: SearchCallsRequest): Observable<Call[]> {
        return getBrapiAllRecords<Call>((page, pageSize) => {
            searchCallsRequest.pageSize = pageSize;
            searchCallsRequest.page = page
            return this.searchCalls(searchCallsRequest);
        });
    }

    searchSamples(searchSamplesRequest: SearchSamplesRequest): Observable<BrapiListResponse<Sample>> {
        return this.http.post<BrapiListResponse<Germplasm>>(`${this.brapiEndpoint}/search/samples`, searchSamplesRequest, { headers: this.createHeader() });
    }

    searchSamplesGetAll(searchSamplesRequest: SearchSamplesRequest): Observable<Sample[]> {
        return getBrapiAllRecords<Sample>((page, pageSize) => {
            searchSamplesRequest.pageSize = pageSize;
            searchSamplesRequest.page = page
            return this.searchSamples(searchSamplesRequest);
        });
    }

    gigwaExportData(exportRequest: GigwaExportRequest): Observable<string> {
        return this.http.post<string>(`${this.baseUrl}/rest/gigwa/exportData`, exportRequest, { headers: this.createHeader(), responseType: 'text' as 'json' });
    }

    // FIXME: Find a way to have a separate instance of HttpClient with its own HttpInterceptor
    private createHeader() {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json; charset=UTF-8',
            'Authorization': `Bearer ${this.accessToken}`
        });
        return headers;
    }

    ga4GhVariantsSearch(ga4GHSearchRequest: GA4GHSearchRequest) {
        return this.http.post<any>(`${this.baseUrl}/rest/ga4gh/variants/search`, ga4GHSearchRequest, { headers: this.createHeader()});
    }
}
