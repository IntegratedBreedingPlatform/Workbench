import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from '../..';
import { Transaction } from '../model/transaction.model';

@Injectable()
export class TransactionService {

    constructor(private context: ParamContext,
                private http: HttpClient) {
    }

    createConfirmedDeposits(lotDepositRequest: any) {
        return this.http.post<any>(
            SERVER_API_URL + `crops/${this.context.cropName}/transactions/confirmed-deposits/generation?programUUID=` + this.context.programUUID,
            lotDepositRequest);
    }

    createPendingDeposits(lotDepositRequest: any) {
        return this.http.post<any>(
            SERVER_API_URL + `crops/${this.context.cropName}/transactions/pending-deposits/generation?programUUID=` + this.context.programUUID,
            lotDepositRequest);
    }

    getTransactionsByGermplasmId(gid: number, lotId: number, request: any): Observable<HttpResponse<Transaction[]>> {
        if (this.context.programUUID) {
            request['programUUID'] = this.context.programUUID
        }
        if (lotId) {
            request['lotId'] = lotId;
        }
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/transactions`;
        const params = createRequestOption(request);
        return this.http.get<Transaction[]>(url, { params, observe: 'response' });

    }

}
