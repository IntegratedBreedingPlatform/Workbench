import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { SERVER_API_URL } from '../../../app.constants';
import { ParamContext } from '../../service/param.context';
import { ListType } from '../../list-builder/model/list-type.model';
import { ListModel } from '../../list-builder/model/list.model';
import { ListService } from './list.service';

@Injectable()
export class SampleListService implements ListService {

    constructor(private http: HttpClient,
                private context: ParamContext) {

    }

    getListTypes(): Observable<ListType[]> {
        return of([{code: 'SAMPLE LIST', name: 'SAMPLE LIST'}]);
    }

    getListType(): Observable<string> {
        return of('SAMPLE LIST');
    }

    save(list: ListModel): Observable<ListModel> {
        const sampleList = {
            listName: list.name,
            description: list.description,
            notes: list.notes,
            createdDate: list.date,
            parentId: list.parentFolderId,
            entries: list.entries
        };
        const url = SERVER_API_URL + `crops/${this.context.cropName}/sample-lists?programUUID=` + this.context.programUUID;
        return this.http.post<any>(url, sampleList);
    }

    updateListMetadata(listId: number, list: ListModel) {
        throw new Error('Method not implemented.');
    }

    getById(listId: number): Observable<ListModel> {
        throw new Error('Method not implemented.');
    }

}
