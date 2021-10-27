import { Injectable } from '@angular/core';

@Injectable()
export class InlineEditorService {
    // [rowId, columnId]
    _editingEntry: [string, string] = ['', ''];

    set editingEntry(editingEntry) {
        if (!editingEntry || !editingEntry.length) {
            this._editingEntry = ['', ''];
        } else {
            this._editingEntry = editingEntry;
        }
    }

    get editingEntry() {
        return this._editingEntry;
    }
}
