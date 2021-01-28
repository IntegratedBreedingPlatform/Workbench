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
import { GermplasmDto } from '../model/germplasm.model';

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
            '?programUUID=' + this.context.programUUID

        return getAllRecords<GermplasmDto>((page: number, pageSize: number) => {
            return this.http.post<GermplasmDto[]>(url, {
                germplasmUUIDs,
                names
            }, {
                params: createRequestOption({page, size: pageSize})
            })
        });
    }

    getGermplasmById(gid: number): Observable<HttpResponse<Germplasm>> {
        const params = {};
        if (this.context.programUUID) {
            params['programUUID'] = this.context.programUUID
        }
        return this.http.get<Germplasm>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}`,
            { params, observe: 'response' });
    }

    getGermplasmAttributes(codes: string[]): Observable<Attribute[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/attributes` +
            '?programUUID=' + this.context.programUUID + '&codes=' + codes;
        return this.http.get<Attribute[]>(url);
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

}

export type ImportGermplasmResultType = { [key: string]: { status: string, gids: number[] } };
