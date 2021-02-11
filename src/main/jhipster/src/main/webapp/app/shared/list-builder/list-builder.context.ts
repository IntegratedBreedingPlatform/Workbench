import { Injectable } from '@angular/core';
import { BaseEntity } from '..';

@Injectable()
export class ListBuilderContext {
    isListBuilderVisible: boolean = true;

    data: BaseEntity[] = [];

    // get data() {
    //     if (!(this._data && this._data.length)) {
    //         this._data = [];
    //     }
    //     return this._data;
    // }
    //
    // set data(data: any[]) {
    //     this.data.push(...data);
    // }
}
