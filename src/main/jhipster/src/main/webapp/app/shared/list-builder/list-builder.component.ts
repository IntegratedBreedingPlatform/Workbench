import { Component } from '@angular/core';
import { ListBuilderContext } from './list-builder.context';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ListBuilderService } from '../list-creation/service/list-builder.service';
import { AlertService } from '../alert/alert.service';
import { ListEntry } from './model/list.model';

@Component({
    selector: 'jhi-list-builder',
    templateUrl: './list-builder.component.html'
})
export class ListBuilderComponent {

    HIDDEN_COLUMNS = ['_internal_id', 'entryNo'];
    data: ListEntry[] = [];
    page = 1;
    pageSize = 20;

    // { <data-index>: boolean }
    selectedItems = {};
    isSelectAllPages = false;

    templateColumnCount = 1;

    constructor(
        public context: ListBuilderContext,
        private modalService: NgbModal,
        private listBuilderService: ListBuilderService,
        private alertService: AlertService
    ) {
    }

    isSelected(index) {
        return this.selectedItems[index];
    }

    toggleSelect(index) {
        if (this.selectedItems[index]) {
            delete this.selectedItems[index];
        } else {
            this.selectedItems[index] = true;
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
        return this.data.slice(this.pageOffset(), this.page * this.pageSize)
            .map((row) => row.internal_id);
    }

    pageOffset() {
        return (this.page - 1) * this.pageSize;
    }

    onSelectAllPages() {
        this.isSelectAllPages = !this.isSelectAllPages;
        this.selectedItems = {};
    }

    headers() {
        if (!(this.data && this.data.length)) {
            return [];
        }
        return Object.keys(this.data[0]).filter((header) => this.HIDDEN_COLUMNS.indexOf(header) === -1);
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    drop(event: CdkDragDrop<any>) {
        if (event.previousContainer === event.container) {
            moveItemInArray(this.data, event.previousIndex, event.currentIndex);
            // if (this.selectedItems[event.previousIndex]) {
            //     this.selectedItems[event.currentIndex] = true;
            //     // delete this.selectedItems[event.previousIndex];
            // }
        } else if (this.data.length > 0) {
            this.data.push(...this.context.data);
        } else {
            this.data = this.context.data;
        }
    }

    reset() {
        this.data = [];
        this.selectedItems = {};
    }

    save() {
        this.listBuilderService.openSaveModal(this.data)
            .then(() => this.onSuccess(),
                () => {
                    // on reject - noop
                }
            );
    }

    private onSuccess() {
        this.context.visible = false;
    }
}
