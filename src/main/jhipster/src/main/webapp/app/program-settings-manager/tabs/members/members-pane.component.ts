import { Component, Input } from '@angular/core';
import { UserDetail } from '../../../shared/user/model/user-detail.model';
import { MembersService } from '../../../shared/user/service/members.service';
import { AlertService } from '../../../shared/alert/alert.service';
import { Pageable } from '../../../shared/model/pageable';
import { DEFAULT_PAGE_SIZE } from '../../../shared';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { map } from 'rxjs/operators';
import { ProgramMember } from '../../../shared/user/model/program-member.model';
import { RoleTypeEnum } from '../../../shared/user/model/role-type.enum';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { Role } from '../../../shared/user/model/role.model';
import { RoleService } from '../../../shared/user/service/role.service';
import { RoleFilter } from '../../../shared/user/model/role-filter.model';
import { FilterType } from '../../../shared/column-filter/column-filter.component';
import { UserSearchRequest } from '../../../shared/user/model/user-search-request.model';
import { ParamContext } from '../../../shared/service/param.context';

class UserTable {
    page = 1;
    pageSize = DEFAULT_PAGE_SIZE;
    // { <id>: UserDetail }
    selectedItems: { [key: number]: UserDetail | ProgramMember } = {};
    isSelectAll = false;
    lastClickIndex?: any;
    users: (UserDetail | ProgramMember)[];
    totalCount: any;
    predicate: any;
    reverse: boolean;

    toggleSelect($event, index, user: UserDetail | ProgramMember, checkbox = false) {
        /*
         * in this module the select all action works a little differently:
         * select all retrieves all items and and put them in the selectedItems map,
         * For this reason, to simplify, select all checkbox will not necessarily be in sync
         * with whats selected in the table. E.g, selecting all, then ctrl-click twice
         * over an item will not return the select all checkbox to the selected state.
         */
        this.isSelectAll = false;

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
        const id = 'id' in user ? user.id : user.userId;
        const isClickedItemSelected = this.selectedItems[id];
        for (const item of items) {
            const itemId = 'id' in item ? item.id : item.userId;
            if (isClickedItemSelected) {
                delete this.selectedItems[itemId];
            } else {
                this.selectedItems[itemId] = item;
            }
        }
    }

    isSelected(user: UserDetail | ProgramMember) {
        const id = 'id' in user ? user.id : user.userId;
        return this.selectedItems[id];
    }

    getSort() {
        if (!this.predicate) {
            return '';
        }
        const order = this.reverse ? 'asc' : 'desc';
        if (this.predicate === Columns.FULLNAME) {
            return ['firstName,' + order, 'lastName,' + order]
        }
        return [this.predicate + ',' + order];
    }
}

@Component({
    selector: 'jhi-members-pane',
    templateUrl: 'members-pane.component.html'
})
export class MembersPaneComponent {

    readonly MEMBERSDROPLIST = 'membersDropList';
    readonly AVAILABLEDROPLIST = 'availableDropList';
    readonly Columns = Columns;

    left: UserTable;
    right: UserTable;
    draggedItems = {}
    filtersLeft = {
        username: {
            key: 'username',
            type: FilterType.TEXT,
            value: ''
        },
        fullName: {
            key: 'fullName',
            type: FilterType.TEXT,
            value: ''
        },
        email: {
            key: 'email',
            type: FilterType.TEXT,
            value: ''
        },
    }
    filtersRight = {
        username: {
            key: 'username',
            type: FilterType.TEXT,
            value: ''
        },
        fullName: {
            key: 'fullName',
            type: FilterType.TEXT,
            value: ''
        },
        roleName: {
            key: 'roleName',
            type: FilterType.TEXT,
            value: ''
        },
    }

    isAvailableVisible = false;

    constructor(
        private membersService: MembersService,
        private alertService: AlertService,
        private modalService: NgbModal,
        private translateService: TranslateService,
        private context: ParamContext
    ) {
        this.reset();
    }

    reset() {
        this.resetEligibleUsers();
        this.resetMembers();
    }

    resetEligibleUsers() {
        this.left = new UserTable();
        this.left.predicate = Columns.FULLNAME;
        this.loadEligibleUsers();
    }

    resetMembers() {
        this.right = new UserTable();
        this.right.predicate = Columns.FULLNAME;
        this.loadMembers();
    }

    loadEligibleUsers() {
        this.membersService.getEligibleUsers(<UserSearchRequest>({
                username: this.filtersLeft.username.value,
                fullName: this.filtersLeft.fullName.value,
                email: this.filtersLeft.email.value
            }),
            <Pageable>({
                page: this.left.page - 1,
                size: this.left.pageSize,
                sort: this.left.getSort()
            })
        ).pipe(map((resp) => {
            this.left.totalCount = resp.headers.get('X-Total-Count')
            return resp;
        })).subscribe((resp) => {
            this.left.users = resp.body
        }, (error) => this.onError(error));
    }

    loadMembers() {
        this.membersService.getMembers(<UserSearchRequest>({
                username: this.filtersRight.username.value,
                fullName: this.filtersRight.fullName.value,
                roleName: this.filtersRight.roleName.value
            }),
            <Pageable>({
                page: this.right.page - 1,
                size: this.right.pageSize,
                sort: this.right.getSort(),
            })
        ).pipe(map((resp) => {
            this.right.totalCount = resp.headers.get('X-Total-Count')
            return resp;
        })).subscribe((resp) => {
            this.right.users = resp.body
        }, (error) => this.onError(error));
    }

    dragStart($event, dragged: UserDetail | ProgramMember, table: UserTable) {
        this.draggedItems = {};
        const id = 'id' in dragged ? dragged.id : dragged.userId;
        // if dragging the selected region, drag them all, otherwise just the item
        if (table.selectedItems[id]) {
            this.draggedItems = table.selectedItems;
        } else {
            this.draggedItems[id] = dragged;
        }
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }

    size(obj) {
        return Object.keys(obj).length;
    }

    async dropAvailableUsers(event: CdkDragDrop<any>) {
        // See also https://github.com/angular/components/issues/13938
        if (event.previousContainer.connectedTo !== this.MEMBERSDROPLIST) {
            return;
        }
        const ids = Object.keys(this.draggedItems).map((key) => Number(key));
        if (!ids.length) {
            return;
        }

        let roleId: number;
        try {
            const modal = this.modalService.open(SelectRoleComponent as Component);
            modal.componentInstance.count = ids.length;
            roleId = await modal.result;
        } catch (e) {
            return;
        }
        this.membersService.addProgramRoleToUsers(roleId, ids).subscribe(
            () => {
                this.alertService.success('program-settings-manager.members.add.success');
                this.reset()
            },
            (error) => this.onError(error)
        );
    }

    isRemovable(user: ProgramMember) {
        return user.role.roleType.name === RoleTypeEnum.PROGRAM.name && user.userId !== this.context.loggedInUserId;
    }

    dropMembers(event: CdkDragDrop<any>) {
        if (event.previousContainer.connectedTo !== this.AVAILABLEDROPLIST) {
            return;
        }
        const ids = Object.keys(this.draggedItems).map((key) => Number(key));
        if (!ids.length) {
            return;
        }
        this.removeMembers(ids);
    }

    removeSelected() {
        const ids = Object.keys(this.right.selectedItems).map((key) => Number(key));
        if (!ids.length) {
            return;
        }
        this.removeMembers(ids);
    }

    private async removeMembers(ids) {

        const modal = this.modalService.open(ModalConfirmComponent as Component);
        modal.componentInstance.message = this.translateService.instant('program-settings-manager.members.delete.confirm', { param: ids.length });
        try {
            await modal.result;
        } catch (e) {
            return;
        }
        this.membersService.removeMembers(ids).subscribe(
            () => {
                this.alertService.success('program-settings-manager.members.delete.success');
                this.reset()
            },
            (error) => this.onError(error)
        );
    }

    applyFiltersLeft() {
        this.resetEligibleUsers();
    }

    applyFiltersRight() {
        this.resetMembers();
    }

    sortLeft() {
        this.loadEligibleUsers();
    }

    sortRight() {
        this.loadMembers();
    }

    selectAllRight() {
        if (!this.right.isSelectAll) {
            this.right.selectedItems = {};
            return;
        }
        this.membersService.getAllMembers(<UserSearchRequest>({
            username: this.filtersRight.username.value,
            fullName: this.filtersRight.fullName.value,
            roleName: this.filtersRight.roleName.value
        })).subscribe((allMembers) => {
            this.right.selectedItems = allMembers.reduce((obj, member) => {
                if (member.role.roleType.name === RoleTypeEnum.PROGRAM.name) {
                    obj[member.userId] = member;
                }
                return obj;
            }, {});
        }, (error) => this.onError(error));
    }

    selectAllLeft() {
        if (!this.left.isSelectAll) {
            this.left.selectedItems = {};
            return;
        }
        this.membersService.getAllEligibleUsers(<UserSearchRequest>({
            username: this.filtersLeft.username.value,
            fullName: this.filtersLeft.fullName.value,
            email: this.filtersLeft.email.value
        })).subscribe((users) => {
            this.left.selectedItems = users.reduce((obj, user) => {
                obj[user.id] = user;
                return obj;
            }, {});
        }, (error) => this.onError(error));
    }
}

@Component({
    selector: 'jhi-select-role-component',
    template: `
		<div class="modal-header">
			<h4 class="modal-title font-weight-bold" jhiTranslate="program-settings-manager.members.select.role.header"></h4>
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true"
					(click)="dismiss()">&times;
			</button>
		</div>
		<div class="modal-body word-wrap">
			<form>
				<div class="form-group row">
					<div class="col" jhiTranslate="program-settings-manager.members.select.role.message" [translateValues]="{count: count}"></div>
				</div>
				<div class="form-group row required">
					<label class="col-sm-2 col-form-label font-weight-bold" jhiTranslate="program-settings-manager.members.select.role.label"></label>
					<div class="col-sm-8">
						<ng-select name="role" [(ngModel)]="selectedRole">
							<ng-option *ngFor="let role of roles" [value]="role.id">{{role.name}}</ng-option>
						</ng-select>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-secondary" data-dismiss="modal" (click)="dismiss()">
				<span class="fa fa-ban"></span>&nbsp;<span jhiTranslate="cancel"></span>
			</button>
			<button (click)="save()" class="btn btn-primary" data-test="saveMembersButton" [disabled]="!selectedRole">
				<span class="fa fa-save"></span>&nbsp;<span jhiTranslate="save"></span>
			</button>
		</div>
    `
})
export class SelectRoleComponent {
    selectedRole: number;
    roles: Role[] = [];
    @Input() count: number;

    constructor(
        private modal: NgbActiveModal,
        private translateService: TranslateService,
        private roleService: RoleService
    ) {
        const roleFilter = <RoleFilter>({ roleTypeId: RoleTypeEnum.PROGRAM.id });
        this.roleService.getFilteredRoles(roleFilter).subscribe((roles) => this.roles = roles);
    }

    save() {
        this.modal.close(this.selectedRole);
    }

    dismiss() {
        this.modal.dismiss();
    }
}

export enum Columns {
    USERNAME = 'userName',
    FULLNAME = 'fullName',
    EMAIL = 'email',
    ROLENAME = 'roleName'
}
