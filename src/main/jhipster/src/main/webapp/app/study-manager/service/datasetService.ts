import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../shared/service/param.context';
import { Observable } from 'rxjs';
import { Location } from '../../shared/location/model/location';
import { SERVER_API_URL } from '../../app.constants';
import { map } from 'rxjs/operators';

@Injectable()
export class DatasetService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getDataset(studyId: number, datasetId: number): Observable<any> {
        return this.http.get<any>(SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/studies/${studyId}/datasets/${datasetId}`,
            { observe: 'response' }).pipe(map((res: HttpResponse<Location>) => res.body));
    }
}
