import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { DatasetModel } from '../model/dataset.model';
import { createRequestOption } from '../..';
import { ObservationUnitsSearchRequest } from '../model/observation-units-search-request.model';
import { ObservationUnitsSearchResponse } from '../model/observation-units-search-response.model';
import { ObservationVariable } from '../../model/observation-variable.model';

@Injectable()
export class DatasetService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getDataset(studyId: number, datasetId: number): Observable<HttpResponse<DatasetModel>> {
        return this.http.get<DatasetModel>(SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets/${datasetId}`,
            { observe: 'response' });
    }

    getDatasets(studyId: number, datasetIds: number[]): Observable<HttpResponse<DatasetModel[]>> {
        let url = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets`;
        if (datasetIds) {
            url += `?datasetTypeIds=${datasetIds}`;
        }
        return this.http.get<DatasetModel[]>(url, { observe: 'response' });
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

}
