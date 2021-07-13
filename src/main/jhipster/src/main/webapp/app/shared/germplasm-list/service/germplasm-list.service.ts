import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { SearchComposite } from '../../model/search-composite';
import { GermplasmSearchRequest } from '../../../entities/germplasm/germplasm-search-request.model';
import { ListType } from '../../list-builder/model/list-type.model';
import { map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { ListModel } from '../../list-builder/model/list.model';
import { ListService } from '../../list-creation/service/list.service';

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

}
