import { Injectable } from '@angular/core';
import { ListModel } from '../../list-builder/model/list.model';
import { Observable } from 'rxjs';
import { ListType } from '../../list-builder/model/list-type.model';

@Injectable()
export abstract class ListService {

    abstract getListTypes(): Observable<ListType[]>;

    abstract getListType(): Observable<string>;

    abstract save(list: ListModel): Observable<ListModel>;

    abstract updateListMetadata(listId: number, list: ListModel);

    abstract getById(listId: number): Observable<ListModel>;
}
