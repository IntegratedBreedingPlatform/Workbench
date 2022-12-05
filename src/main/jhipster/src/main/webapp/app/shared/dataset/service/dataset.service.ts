import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { DatasetModel } from '../model/dataset.model';
import { DatasetTypeEnum } from '../model/dataset-type.enum';
import { ObservationVariable } from '../../model/observation-variable.model';

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

    getVariablesByVariableType(studyId: number, datasetId: number, variableTypeIds: number[]): Observable<HttpResponse<ObservationVariable[]>> {
        let url: string = SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/variables/types/${variableTypeIds}`;
        if (datasetId != null) {
            url += `?datasetId=${datasetId}`;
        }
        return this.http.get<any>(url, { observe: 'response' });
    }

}
