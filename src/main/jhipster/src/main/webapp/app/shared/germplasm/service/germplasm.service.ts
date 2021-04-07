import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { createRequestOption } from '../..';
import { Germplasm } from '../../../entities/germplasm/germplasm.model';
import { Attribute } from '../../attributes/model/attribute.model';
import { NameType } from '../model/name-type.model';
import { GermplasmImportRequest, GermplasmImportValidationPayload } from '../model/germplasm-import-request.model';
import { getAllRecords } from '../../util/get-all-records';
import { GermplasmAttribute, GermplasmDto, GermplasmList, GermplasmProgenitorsDetails, GermplasmStudy } from '../model/germplasm.model';
import { Sample } from '../../../entities/sample';

@Injectable()
export class GermplasmService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    searchGermplasm(germplasmSearchRequest, pageable): Observable<HttpResponse<Germplasm[]>> {
        const options = createRequestOption(pageable);
        return this.http.post<Germplasm[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/search?programUUID=` + this.context.programUUID,
            germplasmSearchRequest, { params: options, observe: 'response' });
    }

    downloadGermplasmTemplate(isGermplasmUpdateFormat: boolean): Observable<HttpResponse<Blob>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/templates/xls/${isGermplasmUpdateFormat}`
            + '?programUUID=' + this.context.programUUID;
        return this.http.get(url, { observe: 'response', responseType: 'blob' });
    }

    importGermplasmUpdates(germplasmUpdates: any): Observable<HttpResponse<Germplasm[]>> {
        return this.http.patch<any>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm?programUUID=` + this.context.programUUID,
            germplasmUpdates, { observe: 'response' });
    }

    getGermplasmMatches(germplasmUUIDs: string[], names: string[]): Observable<GermplasmDto[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/matches` +
            '?programUUID=' + this.context.programUUID;

        return getAllRecords<GermplasmDto>((page: number, pageSize: number) => {
            return this.http.post<GermplasmDto[]>(url, {
                germplasmUUIDs,
                names
            }, {
                params: createRequestOption({ page, size: pageSize })
            });
        });
    }

    getGermplasmById(gid: number): Observable<HttpResponse<GermplasmDto>> {
        const params = {};
        if (this.context.programUUID) {
            params['programUUID'] = this.context.programUUID;
        }
        return this.http.get<GermplasmDto>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}`,
            { params, observe: 'response' });
    }

    getGermplasmAttributes(codes: string[]): Observable<Attribute[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/attributes` +
            '?programUUID=' + this.context.programUUID + '&codes=' + codes;
        return this.http.get<Attribute[]>(url);
    }

    getGermplasmAttributesByGidAndType(gid: number, type: string): Observable<GermplasmAttribute[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/attributes?type=${type}`;
        return this.http.get<GermplasmAttribute[]>(url);
    }

    getGermplasmListsByGid(gid: number): Observable<GermplasmList[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/lists`;
        return this.http.get<GermplasmList[]>(url);
    }

    getGermplasmSamplesByGid(gid: number): Observable<Sample[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/samples`;
        return this.http.get<Sample[]>(url);
    }

    getGermplasmStudiesByGid(gid: number): Observable<GermplasmStudy[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/studies`;
        return this.http.get<GermplasmStudy[]>(url);
    }

    getGermplasmProgenitorsDetails(gid: number): Observable<GermplasmProgenitorsDetails> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/progenitor-details`;
        return this.http.get<GermplasmProgenitorsDetails>(url);
    }

    getGermplasmNameTypes(codes: string[]): Observable<NameType[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/name-types` +
            '?programUUID=' + this.context.programUUID + '&codes=' + codes;
        return this.http.get<NameType[]>(url);
    }

    validateImportGermplasmData(data: GermplasmImportValidationPayload[]) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/validation` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post(url, data);
    }

    importGermplasm(germplasmList: GermplasmImportRequest): Observable<ImportGermplasmResultType> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post<ImportGermplasmResultType>(url, germplasmList);
    }

    deleteGermplasm(gids: number[]): Observable<DeleteGermplasmResultType> {
        const params = {};
        params['gids'] = gids;
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm` +
            '?programUUID=' + this.context.programUUID;
        return this.http.delete<DeleteGermplasmResultType>(url, { params });
    }

    getGermplasmPresentInOtherLists(gids: number[], listId: number): Observable<number[]> {
        const params = {};
        params['gids'] = gids;
        var listIdUrl = listId !== null ? '&listId='+listId : '';
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/lists` +
            '?programUUID=' + this.context.programUUID + listIdUrl;
        return this.http.get<number[]>(url, { params });
    }
}

export type ImportGermplasmResultType = { [key: string]: { status: string, gids: number[] } };
export type DeleteGermplasmResultType = { deletedGermplasm: number[], germplasmWithErrors: number[] };
