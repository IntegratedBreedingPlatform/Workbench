import { Component } from '@angular/core';
import { ListBuilderContext } from './list-builder.context';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ListBuilderService } from '../list-creation/service/list-builder.service';
import { AlertService } from '../alert/alert.service';
import { ListEntry } from './model/list.model';
import { ModalConfirmComponent } from '../modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-list-builder',
    templateUrl: './list-builder.component.html'
})
export class ListBuilderComponent {

    HIDDEN_COLUMNS = ['_internal_id', 'entryNo'];
    data: ListEntry[] = [];
    page = 1;

    // { <data-index>: boolean }
    selectedItems = {};
    isSelectAllPages = false;
    lastClickIndex: any;

    templateColumnCount = 1;

    constructor(
        public context: ListBuilderContext,
        private modalService: NgbModal,
        private listBuilderService: ListBuilderService,
        private alertService: AlertService,
        private translateService: TranslateService,
    ) {
    }

    isSelected(index) {
        return this.selectedItems[index];
    }

    toggleSelect($event, index, internal_id, checkbox = false) {
        if (!$event.ctrlKey && !checkbox) {
            this.selectedItems = {};
        }
        let ids;
        if ($event.shiftKey) {
            const max = Math.max(this.lastClickIndex, index) + 1 + this.pageOffset(),
                min = Math.min(this.lastClickIndex, index) + this.pageOffset();
            ids = this.data.slice(min, max).map((g) => g.internal_id);
        } else {
            ids = [internal_id];
            this.lastClickIndex = index;
        }
        const isClickedItemSelected = this.selectedItems[internal_id];
        for (const id of ids) {
            if (isClickedItemSelected) {
                delete this.selectedItems[id];
            } else {
                this.selectedItems[id] = true;
            }
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
        return this.data.slice(this.pageOffset(), this.page * this.context.pageSize)
            .map((row) => row.internal_id);
    }

    pageOffset() {
        return (this.page - 1) * this.context.pageSize;
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
            moveItemInArray(this.data,
                event.previousIndex + this.pageOffset(),
                event.currentIndex + this.pageOffset());
        } else if (this.data.length > 0) {
            this.data.push(...this.context.data);
        } else {
            this.data = this.context.data;
        }
    }

    async deleteSelected() {
        const modalRef = this.modalService.open(ModalConfirmComponent);
        modalRef.componentInstance.message = this.translateService.instant('list-builder.delete.confirm');
        try {
            await modalRef.result;
        } catch (e) {
            return;
        }
        if (this.isSelectAllPages) {
            this.data = [];
        } else {
            this.data = this.data.filter((row) => !this.selectedItems[row.internal_id]);
        }
        this.selectedItems = {};
    }

    async deleteDuplicates() {
        const modalRef = this.modalService.open(ModalConfirmComponent);
        modalRef.componentInstance.message = this.translateService.instant('list-builder.delete.duplicates.confirm');
        try {
            await modalRef.result;
        } catch (e) {
            return;
        }
        const map = this.data.reduce((_map, row) => {
            _map[row[this.listBuilderService.getIdColumnName()]] = row;
            return _map;
        }, {})
        this.data = Object.values(map);
        this.selectedItems = {};
    }

    async reset() {
        const modalRef = this.modalService.open(ModalConfirmComponent);
        modalRef.componentInstance.message = this.translateService.instant('list-builder.reset.confirm');
        try {
            await modalRef.result;
        } catch (e) {
            return;
        }
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
        this.data = [];
    }
}
