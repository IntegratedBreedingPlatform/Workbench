import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient } from '@angular/common/http';
import { ParamContext } from '../service/param.context';

declare const cropName: string;
declare const currentProgramId: string;

@Injectable()
export class AccountService  {
    constructor(private http: HttpClient,
                private paramContext: ParamContext) { }

    get(cropNameParam?, programUUIDParam?): Observable<Account> {
        const cropNameContext = this.paramContext.cropName;
        const programUUIDContext = this.paramContext.programUUID;

        return this.http.get<Account>(SERVER_API_URL + '/account', {
            params: {
                cropName: cropNameParam || cropNameContext || cropName,
                programUUID: programUUIDParam || programUUIDContext || currentProgramId
            }
        });
    }
}
