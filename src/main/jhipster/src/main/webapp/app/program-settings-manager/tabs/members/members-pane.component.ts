import { Component } from '@angular/core';
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

class UserTable {
    page = 1;
    pageSize = DEFAULT_PAGE_SIZE;
    // { <id>: UserDetail }
    selectedItems: { [key: number]: UserDetail | ProgramMember } = {};
    isSelectAll = false;
    lastClickIndex?: any;
    users: (UserDetail | ProgramMember)[];
    totalCount: any;

    toggleSelect($event, index, user: UserDetail | ProgramMember, checkbox = false) {
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
}

/**
 * TODO:
 *  - filters
 *  - empty table height
 *  - remove link
 *  - add count in select role
 *  - select all, selected count
 */
@Component({
    selector: 'jhi-members-pane',
    templateUrl: 'members-pane.component.html'
})
export class MembersPaneComponent {

    readonly MEMBERSDROPLIST = 'membersDropList';
    readonly AVAILABLEDROPLIST = 'availableDropList';

    left: UserTable;
    right: UserTable;
    draggedItems = {}

    isAvailableVisible = false;

    constructor(
        private membersService: MembersService,
        private alertService: AlertService,
        private modalService: NgbModal,
        private translateService: TranslateService
    ) {
        this.load();
    }

    load() {
        this.left = new UserTable();
        this.right = new UserTable();
        this.loadEligibleUsers();
        this.loadMembers();
    }

    loadEligibleUsers() {
        this.membersService.getMembersEligibleUsers(
            <Pageable>({
                page: this.left.page - 1,
                size: this.left.pageSize,
                sort: null
            })
        ).pipe(map((resp) => {
            this.left.totalCount = resp.headers.get('X-Total-Count')
            return resp;
        })).subscribe((resp) => {
            this.left.users = resp.body
        });
    }

    loadMembers() {
        this.membersService.getMembers(
            <Pageable>({
                page: this.right.page - 1,
                size: this.right.pageSize,
                sort: null
            })
        ).pipe(map((resp) => {
            this.right.totalCount = resp.headers.get('X-Total-Count')
            return resp;
        })).subscribe((resp) => {
            this.right.users = resp.body
        });
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

    async dropMembers(event: CdkDragDrop<any>) {
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
            roleId = await this.modalService.open(SelectRoleComponent as Component).result;
        } catch (e) {
            return;
        }
        this.membersService.addProgramRoleToUsers(roleId, ids).subscribe(
            () => {
                this.alertService.success('program-settings-manager.members.add.success');
                this.load()
            },
            (error) => this.onError(error)
        );
    }

    isRemovable(user: ProgramMember) {
        return user.role.type === RoleTypeEnum.PROGRAM.name;
    }

    async removeMember(event: CdkDragDrop<any>) {
        if (event.previousContainer.connectedTo !== this.AVAILABLEDROPLIST) {
            return;
        }
        const ids = Object.keys(this.draggedItems).map((key) => Number(key));
        if (!ids.length) {
            return;
        }

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
                this.load()
            },
            (error) => this.onError(error)
        );
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
					<div class="col" jhiTranslate="program-settings-manager.members.select.role.message"></div>
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
