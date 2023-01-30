import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Role } from '../../models/role.model';
import { Permission } from '../../models/permission.model';
import { RoleType } from '../../models/role-type.model';
import { OnPermissionSelectedType, RoleService, setParent, visitPermissions } from '../../services/role.service';
import { PopupService } from '../../../shared/modal/popup.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { SiteAdminContext } from '../../site-admin-context';
import { finalize } from 'rxjs/internal/operators/finalize';
import { AlertService } from '../../../shared/alert/alert.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';

@Component({
    selector: 'jhi-role-edit-dialog',
    templateUrl: 'role-edit-dialog.component.html',
    styleUrls: ['role-edit-dialog.component.css']
})

export class RoleEditDialogComponent implements OnInit {
    roleTypeDisabled: boolean;
    // keep permissions selected when changing role types. value can be anything (is there other data structure that fits better?)
    permissionSelectedIdMap: { [id: string]: any } = {};

    model: Role;
    roleTypeId = '';
    roleTypes: RoleType[];
    permissions: Permission[] = [];
    confirmMessages: any[] = [];
    isLoading: boolean;

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private roleService: RoleService,
                private alertService: AlertService,
                private modalService: NgbModal,
                private context: SiteAdminContext) {
    }

    ngOnInit() {
        if (this.context.role && this.context.role.id) {
            this.model = this.context.role;
            // role type cannot be changed if there are users assigned to them
            this.roleTypeDisabled = Boolean(this.context.role.userRoles && this.context.role.userRoles.length);

            this.permissionSelectedIdMap = this.context.role.permissions.reduce((map, permission: Permission) => {
                map[permission.id] = true;
                return map;
            }, {});

            this.roleTypeId = this.model.roleType.id;
            this.drawTree();
        } else {
            this.model = new Role();
        }

        this.roleService.getRoleTypes().subscribe((roleTypes) => {
            this.roleTypes = roleTypes;
        });

        this.roleService.onPermissionSelected.subscribe((selected: OnPermissionSelectedType) => {
            if (selected.selected) {
                this.permissionSelectedIdMap[selected.id] = selected.selected;
            } else {
                delete this.permissionSelectedIdMap[selected.id];
            }
        });
    }

    isFormValid(form: NgForm) {
        return form.valid && this.hasPermissionsSelected();
    }

    private hasPermissionsSelected(): boolean {
        const permissions: Permission[] = Object.assign([], this.permissions);
        while (permissions.length) {
            const permission = permissions.pop();
            if (permission.selected) {
                return true;
            }
            if (permission.children) {
                permissions.push.apply(permissions, permission.children);
            }
        }
        return false;
    }

    private prepareModelForSaving() {
        this.model.name = this.model.name.trim();
        this.model.description = this.model.description && this.model.description.trim();

        this.model.permissions = [];
        const permissions: Permission[] = Object.assign([], this.permissions);
        // flatten permission structure and set it to model
        while (permissions.length) {
            const permission = permissions.pop();
            if (permission.selected) {
                this.model.permissions.push(permission);
            }
            if (permission.children) {
                permissions.push.apply(permissions, permission.children);
            }
        }

        this.model.type = this.roleTypeId;
    }

    addRole(form: NgForm) {
        this.prepareModelForSaving();
        this.roleService.createRole(this.model).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe(() => {
            this.alertService.success('site-admin.role.modal.create.success');
            this.notifyChanges();
            this.isLoading = false;
        }, (error) => this.onError(error));
    }

    updateRole(form: NgForm, showWarnings: boolean) {
        this.prepareModelForSaving();
        this.roleService.updateRole(this.model, showWarnings).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe(() => {
            this.alertService.success('site-admin.role.modal.edit.success');
            this.notifyChanges();
            this.isLoading = false;
        }, (response) => {
            if (response.status === 409) {
                this.confirmMessages = response.error.errors;
                const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
                confirmModalRef.componentInstance.title = 'Confirmation';
                confirmModalRef.componentInstance.message = this.confirmMessages[0].message;
                confirmModalRef.result.then(() => {
                    this.updateRole(form, false);
                }, () => confirmModalRef.dismiss());

            } else {
                this.onError(response);
            }
        });
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    onChangeRoleType() {
        this.drawTree();
    }

    drawTree() {
        this.roleService.getPermissionsTree(Number(this.roleTypeId)).subscribe((root: Permission) => {
            this.permissions = setParent([root], null);
            visitPermissions(this.permissions, (permission) => {
                if (this.permissionSelectedIdMap[permission.id] && permission.selectable) {
                    permission.selected = true;
                }
                if (permission.parent && permission.parent.selected) {
                    permission.selected = true;
                    permission.disabled = true;
                }
            });
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'onRoleViewChanged' });
        this.clear();
    }
}

/**
 * Recursive component to display a tree of Permissions
 *
 * Note: If the requirement evolves beyond this basic prototype
 *  or gets too complex (shift-select), consider looking for a library
 */
@Component({
    selector: 'jhi-permission-tree',
    template: `
		<ul class="ul-tree" [class.ul-tree-level-zero]="isLevelZero">
			<li *ngFor="let permission of permissions">
				<ng-container>
					<div [class.checkbox]="permission.selectable">
						<label>
							<input type="checkbox" *ngIf="isShowCheckbox(permission)" (click)="onPermissionClick($event, permission)"
								   [disabled]="permission.disabled"
								   [(ngModel)]="permission.selected">
							{{permission.description}}
						</label>
					</div>
				</ng-container>
				<jhi-permission-tree *ngIf="permission.children" [permissions]="permission.children"></jhi-permission-tree>
			</li>
		</ul>
    `
})
export class PermissionTreeComponent {
    @Input() permissions: Permission [];
    @Input() isLevelZero: boolean;

    constructor(private roleService: RoleService) {
    }

    isShowCheckbox(permission: Permission) {
        return permission.selectable;
    }

    onPermissionClick(event: any, permission: Permission) {
        const checked = event.currentTarget.checked;
        this.roleService.onPermissionSelected.next({ id: permission.id, selected: checked });
        if (permission.children) {
            const permissions = Object.assign([], permission.children);
            while (permissions.length) {
                const descendant: Permission = permissions.pop();
                descendant.selected = checked;
                descendant.disabled = checked;
                this.roleService.onPermissionSelected.next({ id: descendant.id, selected: checked });
                if (descendant.children) {
                    permissions.push.apply(permissions, descendant.children);
                }
            }
        }
    }
}

@Component({
    selector: 'jhi-role-edit-popup',
    template: ''
})
export class RoleEditPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        this.popupService.open(RoleEditDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }
}
