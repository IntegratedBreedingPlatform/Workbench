import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable()
export class TransactionService {

    constructor(private context: ParamContext,
                private http: HttpClient) {
    }

    createConfirmedDeposits(lotDepositRequest: any) {
        return this.http.post<any>(
            SERVER_API_URL + `crops/${this.context.cropName}/transactions/confirmed-deposits-lists`, lotDepositRequest, { observe: 'response' })
            .pipe(map((res) => res.body));
    }
}
