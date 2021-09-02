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
import { GermplasmAttribute, GermplasmBasicDetailsDto, GermplasmDto, GermplasmList, GermplasmProgenitorsDetails, GermplasmStudy } from '../model/germplasm.model';
import { Sample } from '../../../entities/sample';
import { GermplasmNameRequestModel } from '../model/germplasm-name-request.model';
import { GermplasmAttributeRequestModel } from '../model/germplasm-attribute-request.model';
import { GermplasmProgenitorsUpdateRequestModel } from '../model/germplasm-progenitors-update-request.model';
import { GermplasmSearchRequest } from '../../../entities/germplasm/germplasm-search-request.model';
import { map } from 'rxjs/operators';
import { GermplasmCodeNameBatchRequestModel } from '../model/germplasm-code-name-batch-request.model';
import { GermplasmNameSettingModel } from '../model/germplasm-name-setting.model';
import { GermplasmCodeNameBatchResultModel } from '../model/germplasm-code-name-batch-result.model';
import { VariableTypeEnum } from '../../ontology/variable-type.enum';

@Injectable()
export class GermplasmService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getSearchResults(req?: any): Observable<HttpResponse<Germplasm[]>> {
        const options = createRequestOption(req);
        return this.http.get<Germplasm[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/search?programUUID=` + this.context.programUUID,
            { params: options, observe: 'response' });
    }

    search(req?: GermplasmSearchRequest): Observable<string> {
        return this.http.post<any>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/search?programUUID=` + this.context.programUUID, req, { observe: 'response' })
            .pipe(map((res: any) => res.body.result.searchResultDbId));
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

    getGermplasmMatches(germplasmPUIs: string[], names: string[]): Observable<GermplasmDto[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/matches` +
            '?programUUID=' + this.context.programUUID;

        return getAllRecords<GermplasmDto>((page: number, pageSize: number) => {
            return this.http.post<GermplasmDto[]>(url, {
                germplasmPUIs,
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

    getGermplasmAttributesByGidAndType(gid: number, variableTypeId: VariableTypeEnum): Observable<GermplasmAttribute[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/attributes?variableTypeId=${variableTypeId}&programUUID=${this.context.programUUID}`;
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

    updateGermplasmBasicDetails(germplasm: GermplasmDto) {
        const germplasmBasicDetailsDto: GermplasmBasicDetailsDto = new GermplasmBasicDetailsDto(germplasm.creationDate, germplasm.reference, germplasm.breedingLocationId);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${germplasm.gid}/basic-details?programUUID=` + this.context.programUUID;
        return this.http.patch(url, germplasmBasicDetailsDto);
    }

    deleteGermplasm(gids: number[]): Observable<DeleteGermplasmResultType> {
        const params = {};
        params['gids'] = gids;
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm` +
            '?programUUID=' + this.context.programUUID;
        return this.http.delete<DeleteGermplasmResultType>(url, { params });
    }

    createGermplasmCodeNames(germplasmCodeNameBatchRequestModel: GermplasmCodeNameBatchRequestModel): Observable<GermplasmCodeNameBatchResultModel[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/codes` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post<GermplasmCodeNameBatchResultModel[]>(url, germplasmCodeNameBatchRequestModel);
    }

    getNextNameInSequence(germplasmNameSettingModel: GermplasmNameSettingModel) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/names/next-generation` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post(url, germplasmNameSettingModel, { observe: 'response', responseType: 'text' });
    }

    createGermplasmName(gid: number, germplasmNameRequestModel: GermplasmNameRequestModel): Observable<number> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/names` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post<number>(url, germplasmNameRequestModel);
    }

    updateGermplasmName(gid: number, nameId: number, germplasmNameRequestModel: GermplasmNameRequestModel): Observable<any> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/names/${nameId}` +
            '?programUUID=' + this.context.programUUID;
        return this.http.patch<any>(url, germplasmNameRequestModel);
    }

    deleteGermplasmName(gid: number, nameId: any) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/names/${nameId}` +
            '?programUUID=' + this.context.programUUID;
        return this.http.delete<any>(url);
    }

    createGermplasmAttribute(gid: number, germplasmAttributeRequestModel: GermplasmAttributeRequestModel): Observable<number> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/attributes` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post<number>(url, germplasmAttributeRequestModel);
    }

    updateGermplasmAttribute(gid: number, attributeId: number, gernplasmAttributeRequestModel: GermplasmAttributeRequestModel): Observable<any> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/attributes/${attributeId}` +
            '?programUUID=' + this.context.programUUID;
        return this.http.patch<any>(url, gernplasmAttributeRequestModel);
    }

    deleteGermplasmAttribute(gid: number, attributeId: number) {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/attributes/${attributeId}` +
            '?programUUID=' + this.context.programUUID;
        return this.http.delete<any>(url);
    }

    updateGermplasmProgenitors(gid: number, germplasmProgenitorsUpdateRequestModel: GermplasmProgenitorsUpdateRequestModel): Observable<any> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/progenitor-details` +
            '?programUUID=' + this.context.programUUID;
        return this.http.patch<any>(url, germplasmProgenitorsUpdateRequestModel);
    }

    // FIXME backend returns share/ontology/model/variable
    searchAttributes(query): Observable<HttpResponse<Attribute[]>> {
        return this.http.get<Attribute[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/attributes/search?query=` + query
            + '&programUUID=' + this.context.programUUID, { observe: 'response' });
    }

    searchNameTypes(query): Observable<HttpResponse<NameType[]>> {
        return this.http.get<NameType[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/name-types/search?query=` + query, { observe: 'response' });
    }
}

export type ImportGermplasmResultType = { [key: string]: { status: string, gids: number[] } };
export type DeleteGermplasmResultType = { deletedGermplasm: number[], germplasmWithErrors: number[] };
