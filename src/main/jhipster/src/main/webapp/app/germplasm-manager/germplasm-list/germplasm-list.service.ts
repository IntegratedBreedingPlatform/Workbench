import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { GermplasmListType } from './germplasm-list-type.model';
import { map } from 'rxjs/operators';
import { GermplasmList } from '../../shared/model/germplasm-list';
import { SearchComposite } from '../../shared/model/search-composite';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';

@Injectable()
export class GermplasmListService {
    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    getGermplasmListTypes(): Observable<GermplasmListType[]> {
        return this.http.get<GermplasmListType[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm-list-types?programUUID=` + this.context.programUUID,
            { observe: 'response' }).pipe(map((res: HttpResponse<GermplasmListType[]>) => res.body));
    }

    save(list: GermplasmList): Observable<GermplasmList> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm-lists?programUUID=` + this.context.programUUID;
        return this.http.post<GermplasmList>(url, list);
    }

    addGermplasmEntriesToList(germplasmListId: number, searchComposite: SearchComposite<GermplasmSearchRequest, number>): Observable<void> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm-lists/${germplasmListId}/entries?programUUID=` + this.context.programUUID;
        return this.http.post<void>(url, searchComposite);
    }

}
