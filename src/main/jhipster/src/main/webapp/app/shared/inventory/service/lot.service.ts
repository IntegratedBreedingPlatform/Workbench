import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ParamContext } from '../../service/param.context';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';
import { map } from 'rxjs/operators';
import { Lot } from '../model/lot.model';

@Injectable()
export class LotService {

    constructor(private context: ParamContext,
                private http: HttpClient) {
    }

    createLots(lotGeneratorBatchRequest): Observable<string[]> {
        return this.http.post<any>(SERVER_API_URL + `crops/${this.context.cropName}/lots/generation`, lotGeneratorBatchRequest);
    }
}
