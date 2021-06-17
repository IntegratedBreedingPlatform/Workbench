import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ParamContext } from '../../service/param.context';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { GermplasmGroupingRequestModel } from '../model/germplasm-grouping-request.model';
import { GermplasmGroup } from '../model/germplasm-group.model';

@Injectable()
export class GermplasmGroupingService {
    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    group(germplasmGroupingRequest: GermplasmGroupingRequestModel): Observable<GermplasmGroup[]> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/grouping` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post<GermplasmGroup[]>(url, germplasmGroupingRequest);
    }

    ungroup(gids: number[]): Observable<GermplasmUngroupingResultType> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm/ungrouping` +
            '?programUUID=' + this.context.programUUID;
        return this.http.post<GermplasmUngroupingResultType>(url, gids);

    }

}

export type GermplasmUngroupingResultType = { unfixedGids: number[], numberOfGermplasmWithoutGroup: number };
