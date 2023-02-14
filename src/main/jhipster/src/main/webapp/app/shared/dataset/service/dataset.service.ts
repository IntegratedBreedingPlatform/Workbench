import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { DatasetModel } from '../model/dataset.model';
import { DatasetTypeEnum } from '../model/dataset-type.enum';
import { createRequestOption } from '../..';
import { ObservationUnitsSearchRequest } from '../model/observation-units-search-request.model';
import { ObservationUnitsSearchResponse } from '../model/observation-units-search-response.model';
import { ObservationVariable } from '../../model/observation-variable.model';
import { PhenotypeAudit } from '../../model/phenotype-audit.model';

@Injectable()
export class DatasetService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getDataset(studyId: number, datasetId: number): Observable<HttpResponse<DatasetModel>> {
        return this.http.get<any>(SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets/${datasetId}`,
            { observe: 'response' });
    }

    getDatasetsByTypeIds(studyId: number, datasetTypeIds: DatasetTypeEnum[]): Observable<HttpResponse<DatasetModel[]>> {
        const url: string = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets?datasetTypeIds=${datasetTypeIds}`;
        return this.http.get<any>(url, { observe: 'response' });
    }

    getVariablesByVariableType(studyId: number, variableTypeIds: number[]): Observable<HttpResponse<ObservationVariable[]>> {
        const params = createRequestOption(Object.assign({ variableTypeIds }));
        const url: string = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/variables/types`;
        return this.http.get<any>(url, { params, observe: 'response' });
    }

    getDatasets(studyId: number, datasetIds: number[]): Observable<HttpResponse<DatasetModel[]>> {
        const params = {};
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets`;
        if (datasetIds) {
            params['datasetTypeIds'] = datasetIds;
        }
        return this.http.get<DatasetModel[]>(url, { params, observe: 'response' });
    }

    getObservationSetColumns(studyId: number, datasetId: number): Observable<HttpResponse<ObservationVariable[]>> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets/${datasetId}/observationUnits/table/columns`;
        return this.http.get<ObservationVariable[]>(url, { observe: 'response' });
    }

    getObservationUnitTable(studyId: number,
                            datasetId: number,
                            request: ObservationUnitsSearchRequest,
                            pagination: any): Observable<HttpResponse<ObservationUnitsSearchResponse[]>> {
        const params = createRequestOption(pagination);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets/${datasetId}/observationUnits/table`;
        return this.http.post<ObservationUnitsSearchResponse[]>(url, request, { params, observe: 'response' });
    }

    getPhenotypeAuditRecords(studyId: number,
                             datasetId: number,
                             observationUnitId: string, variableId: number,
                             pagination: any): Observable<HttpResponse<PhenotypeAudit[]>> {
        const params = createRequestOption(pagination);
        const url: string = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets/${datasetId}`
            + `/observationUnits/${observationUnitId}/variable/${variableId}/phenotype-audit`;
        return this.http.get<PhenotypeAudit[]>(url, { params, observe: 'response' });
    }
}
