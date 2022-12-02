import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { DatasetModel } from '../model/dataset.model';

@Injectable()
export class DatasetService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getDataset(studyId: number, datasetId: number): Observable<HttpResponse<DatasetModel>> {
        return this.http.get<any>(SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets/${datasetId}`,
            { observe: 'response' });
    }

}
