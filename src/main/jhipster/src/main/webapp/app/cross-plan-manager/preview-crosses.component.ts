import {Component, Input, OnInit} from '@angular/core';
import {ListEntry} from '../shared/list-builder/model/list.model';

@Component({
    selector: 'jhi-preview-crosses',
    templateUrl: './preview-crosses.component.html'
})
export class PreviewCrossesComponent implements OnInit {

    ColumnLabels = ColumnLabels;
    error: any;
    currentSearch: string;
    routeData: any;
    itemsPerPage: any = 10;
    page: any = 1;
    pageSize = 20;

    predicate: any;
    previousPage: any;
    reverse: boolean;

    isLoading: boolean;

    selectedItems = {};
    isSelectAll = false;
    lastClickIndex: any;
    isSelectAllPages = false;

    @Input() previewCrosses: ListEntry[] = [];
    isPreviewCrossesSelectAll = false;

    HIDDEN_COLUMNS = ['_internal_id', 'entryNo'];
    constructor() {
    }

    ngOnInit(): void {
    }

    onSelectAllPages() {
        this.isSelectAllPages = !this.isSelectAllPages;
        this.selectedItems = {};
    }

    onSelectPage() {
        const pageItemIds = this.getPageItemIds();
        if (this.isPageSelected()) {
            // remove all items
            pageItemIds.forEach((itemId) => delete this.selectedItems[itemId]);
        } else {
            // check remaining items
            pageItemIds.forEach((itemId) => this.selectedItems[itemId] = true);
        }
    }

    toggleSelect($event, index, listEntry: ListEntry, checkbox = false) {
        if (this.isSelectAll) {
            return;
        }
        if (!$event.ctrlKey && !checkbox) {
            this.selectedItems = {};
        }
        let items;
        if ($event.shiftKey) {
            const max = Math.max(this.lastClickIndex, index) + 1,
                min = Math.min(this.lastClickIndex, index);
            items = this.previewCrosses.slice(min, max);
        } else {
            items = [listEntry];
            this.lastClickIndex = index;
        }
        const isClickedItemSelected = this.selectedItems[listEntry.internal_id];
        for (const item of items) {
            if (isClickedItemSelected) {
                delete this.selectedItems[item.internal_id];
            } else {
                this.selectedItems[item.internal_id] = item;
            }
        }
    }

    isPageSelected() {
        const pageItemIds = this.getPageItemIds();
        return this.size(this.selectedItems) && pageItemIds.every((itemId) => this.selectedItems[itemId]);
    }

    getPageItemIds(): any[] {
        if (!(this.previewCrosses && this.previewCrosses.length)) {
            return [];
        }
        return this.previewCrosses.slice(this.pageOffset(), this.page * this.itemsPerPage)
            .map((row) => row.internal_id);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    isSelected(listEntry: ListEntry) {
        return this.selectedItems[listEntry.internal_id];
    }

    pageOffset() {
        return (this.page - 1) * this.itemsPerPage;
    }

}

export enum ColumnLabels {
    'FEMALE_PARENT' = 'FEMALE PARENT',
    'MALE_PARENT' = 'MALE PARENT',
    'FEMALE_CROSS' = 'FEMALE CROSS',
    'MALE_CROSS' = 'MALE CROSS',
    'GERMPLASM_ORIGIN' = 'GERMPLASM ORIGIN'
}
