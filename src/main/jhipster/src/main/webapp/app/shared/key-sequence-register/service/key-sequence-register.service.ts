import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { map } from 'rxjs/operators';

@Injectable()
export class KeySequenceRegisterService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    deleteKeySequencePrefixes(gids: number[], prefixes: string[]): Observable<DeleteKeySequencePrefixesResultType> {
        const params = {};
        params['gids'] = gids;
        params['prefixes'] = prefixes;
        return this.http.delete<DeleteKeySequencePrefixesResultType>(SERVER_API_URL + `crops/${this.context.cropName}/programs/${this.context.programUUID}/key-sequences`,
            { params, observe: 'response' }).pipe(map((res: HttpResponse<DeleteKeySequencePrefixesResultType>) => res.body));
    }
}

export type DeleteKeySequencePrefixesResultType = { deletedPrefixes: number[], undeletedPrefixes: number[] };
