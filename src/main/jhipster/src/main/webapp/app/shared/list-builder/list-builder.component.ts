import { Component, Input } from '@angular/core';
import { ListBuilderContext } from './list-builder.context';
import { BaseEntity } from '..';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { GermplasmList, GermplasmListEntry } from '../model/germplasm-list';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ListBuilderService } from '../list-creation/service/list-builder.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../alert/format-error-list';
import { AlertService } from '../alert/alert.service';
import { ListCreationComponent } from '../list-creation/list-creation.component';

@Component({
    selector: 'jhi-list-builder',
    templateUrl: './list-builder.component.html'
})
export class ListBuilderComponent {
    data: BaseEntity[] = [];
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
            .map((row, i) => this.pageOffset() + i);
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
