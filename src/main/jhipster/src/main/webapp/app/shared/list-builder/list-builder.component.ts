import { Component, Input } from '@angular/core';
import { ListBuilderContext } from './list-builder.context';
import { BaseEntity } from '..';
import { CdkDragDrop } from '@angular/cdk/drag-drop';

@Component({
    selector: 'jhi-list-builder',
    templateUrl: './list-builder.component.html'
})
export class ListBuilderComponent {

    @Input()
    visible: boolean;

    data: BaseEntity[] = [];
    page = 1;
    pageSize = 20;

    selectedItems = {};
    isSelectAllPages = false;

    templateColumnCount = 1;

    constructor(
        public context: ListBuilderContext
    ) {
    }

    isSelected(row: BaseEntity) {
        return row && this.selectedItems[row.id];
    }

    toggleSelect(row: BaseEntity) {
        if (this.selectedItems[row.id]) {
            delete this.selectedItems[row.id];
        } else {
            this.selectedItems[row.id] = true;
        }
    }

    isPageSelected() {
        const pageItemIds = this.getPageItemIds();
        return this.size(this.selectedItems) && pageItemIds.every((itemId) => this.selectedItems[itemId]);
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

    getPageItemIds(): any[] {
        if (!(this.data && this.data.length)) {
            return [];
        }
        return this.data.slice((this.page - 1) * this.pageSize, this.page * this.pageSize)
            .map((row) => row.id);
    }

    onSelectAllPages() {
        this.isSelectAllPages = !this.isSelectAllPages;
        if (this.isSelectAllPages) {
            this.data.forEach((row) => this.selectedItems[row.id] = true);
        } else {
            this.selectedItems = {};
        }
    }

    headers() {
        if (!(this.data && this.data.length)) {
            return [];
        }
        return Object.keys(this.data[0]);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    drop($event: CdkDragDrop<any>) {
        if (this.data.length > 0) {
            this.data.push(...this.context.data);
        } else {
            this.data = this.context.data;
        }
    }
}
