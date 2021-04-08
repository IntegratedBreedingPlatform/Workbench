import { Injectable } from '@angular/core';
import { ListEntry } from './model/list.model';

@Injectable()
export class ListBuilderContext {
    visible = false;
    data: ListEntry[] = [];
    pageSize = 20;
}
