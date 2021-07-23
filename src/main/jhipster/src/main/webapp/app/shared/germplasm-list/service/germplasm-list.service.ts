import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { SearchComposite } from '../../model/search-composite';
import { GermplasmSearchRequest } from '../../../entities/germplasm/germplasm-search-request.model';
import { ListType } from '../../list-builder/model/list-type.model';
import { map } from 'rxjs/operators';
import { ListModel } from '../../list-builder/model/list.model';
import { ListService } from '../../list-creation/service/list.service';
import { GermplasmListSearchRequest } from '../model/germplasm-list-search-request.model';
import { GermplasmListSearchResponse } from '../model/germplasm-list-search-response.model';
import { createRequestOption } from '../..';
import { GermplasmListDataSearchRequest } from '../model/germplasm-list-data-search-request.model';
import { GermplasmListDataSearchResponse } from '../model/germplasm-list-data-search-response.model';
import { GermplasmList } from '../../model/germplasm-list';

@Injectable()
export class GermplasmListService implements ListService {

    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    getListTypes(): Observable<ListType[]> {
        return this.http.get<ListType[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm-list-types?programUUID=` + this.context.programUUID,
            { observe: 'response' }).pipe(map((res: HttpResponse<ListType[]>) => res.body));
    }

    getListType(): Observable<string> {
        return of('LST');
    }

    save(list: ListModel): Observable<ListModel> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm-lists?programUUID=` + this.context.programUUID;
        return this.http.post<ListModel>(url, list);
    }

    addGermplasmEntriesToList(germplasmListId: number, searchComposite: SearchComposite<GermplasmSearchRequest, number>): Observable<void> {
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm-lists/${germplasmListId}/entries?programUUID=` + this.context.programUUID;
        return this.http.post<void>(url, searchComposite);
    }

    searchList(req: GermplasmListSearchRequest, pagination: any): Observable<HttpResponse<GermplasmListSearchResponse[]>> {
        const params = createRequestOption(pagination);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm-lists/search?programUUID=` + this.context.programUUID;
        return this.http.post<GermplasmListSearchResponse[]>(url, req, { params, observe: 'response' });
    }

    searchListData(listId: number, req: GermplasmListDataSearchRequest, pagination: any): Observable<HttpResponse<GermplasmListDataSearchResponse[]>> {
        const params = createRequestOption(pagination);
        const url = SERVER_API_URL + `crops/${this.context.cropName}/germplasm-lists/${listId}/data/search?programUUID=` + this.context.programUUID;
        return this.http.post<GermplasmListDataSearchResponse[]>(url, req, { params, observe: 'response' });
    }

    getGermplasmListById(listId: number): Observable<HttpResponse<GermplasmList>> {
        return this.http.get<GermplasmList>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm-lists/${listId}?programUUID=` + this.context.programUUID,
            { observe: 'response' });
    }

}
