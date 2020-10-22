import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { GermplasmListType } from './germplasm-list-type.model';
import { map } from 'rxjs/operators';

@Injectable()
export class GermplasmListService {
    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    getGermplasmListTypes(): Observable<GermplasmListType[]> {
        return this.http.get<GermplasmListType[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm-list-types?programUUID=` + this.context.programUUID,
            { observe: 'response' }).pipe(map((res: HttpResponse<GermplasmListType[]>) => res.body));
    }
}
