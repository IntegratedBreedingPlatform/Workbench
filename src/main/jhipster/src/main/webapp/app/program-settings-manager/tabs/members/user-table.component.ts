import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DEFAULT_PAGE_SIZE } from '../../../shared';
import { UserDetail } from '../../../shared/user/model/user-detail.model';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { AlertService } from '../../../shared/alert/alert.service';

@Component({
    selector: 'jhi-user-table',
    templateUrl: './user-table.component.html'
})
export class UserTableComponent {

    @Input()
    users: UserDetail[];

    page: number;
    totalCount: any;
    pageSize = DEFAULT_PAGE_SIZE;
    // { <id>: UserDetail }
    selectedItems: { [key: number]: UserDetail } = {};
    isSelectAll = false;
    lastClickIndex: any;
    isLoading = false;

    @Output()
    onLoad = new EventEmitter<{ page: number, pageSize: number }>();

    constructor(
        private alertService: AlertService
    ) {
    }

    load() {
        this.onLoad.emit({ page: this.page, pageSize: this.pageSize });
    }

    toggleSelect($event, index, user: UserDetail, checkbox = false) {
        if (!$event.ctrlKey && !checkbox) {
            this.selectedItems = {};
        }
        let items;
        if ($event.shiftKey) {
            const max = Math.max(this.lastClickIndex, index) + 1,
                min = Math.min(this.lastClickIndex, index);
            items = this.users.slice(min, max);
        } else {
            items = [user];
            this.lastClickIndex = index;
        }
        const isClickedItemSelected = this.selectedItems[user.id];
        for (const item of items) {
            if (isClickedItemSelected) {
                delete this.selectedItems[item.gid];
            } else {
                this.selectedItems[item.gid] = item;
            }
        }
    }

    isSelected(user: UserDetail) {
        return this.selectedItems[user.id];
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }
}
