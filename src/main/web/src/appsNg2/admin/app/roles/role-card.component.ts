import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '../shared/models/role.model';
import { RoleType } from '../shared/models/role-type.model';
import { OnPermissionSelectedType, RoleService, setParent, visitPermissions } from '../shared/services/role.service';
import { Permission } from '../shared/models/permission.model';
import { ErrorResponseInterface } from '../shared/services/error-response.interface';

@Component({
    selector: 'role-card',
    templateUrl: './role-card.component.html',
    moduleId: module.id
})

export class RoleCardComponent implements OnInit {
    isEditing: boolean;
    isVisible = true;
    roleId: number;
    roleTypeDisabled: boolean;
    // keep permissions selected when changing role types. value can be anything (is there other data structure that fits better?)
    permissionSelectedIdMap: { [id: string]: any } = {};

    model: Role;
    roleTypeId: any = "";
    roleTypes: RoleType[];
    permissions: Permission[] = [];

    errors: ErrorResponseInterface[];

    constructor(private route: ActivatedRoute,
                private router: Router,
                private roleService: RoleService) {
    }

    ngOnInit() {
        this.model = new Role();
        this.errors = [];

        this.route.params.subscribe(params => {
            this.roleId = Number(params['id']);
            if (this.roleId) {
                this.roleService.getRole(this.roleId).subscribe((role: Role) => {
                    this.model = <Role>({
                        id: role.id,
                        name: role.name,
                        description: role.description,
                        roleType: role.roleType,
                        editable: role.editable,
                        assignable: role.assignable,
                        active: role.active,
                    });

                    // role type cannot be changed if there are users assigned to them
                    this.roleTypeDisabled = Boolean(role.userRoles && role.userRoles.length);

                    this.permissionSelectedIdMap = role.permissions.reduce((map, permission: Permission) => {
                        map[permission.id] = true;
                        return map;
                    }, {});

                    this.roleTypeId = this.model.roleType.id;
                    this.drawTree();
                });
            }
        });

        this.route.queryParams.subscribe(params => {
            this.isEditing = params['isEditing'] === 'true';
        });

        this.roleService.getRoleTypes().subscribe((roleTypes) => {
            this.roleTypes = roleTypes;
        });
    }

    isFormValid(form: NgForm) {
        return form.valid && this.hasPermissionsSelected();
    }

    private hasPermissionsSelected(): boolean {
        let permissions: Permission[] = Object.assign([], this.permissions);
        while (permissions.length) {
            let permission = permissions.pop();
            if (permission.selected) {
                return true;
            }
            if (permission.children) {
                permissions.push.apply(permissions, permission.children);
            }
        }
        return false;
    }

    cancel(form: NgForm) {
        this.router.navigate(['../../'], { relativeTo: this.route});
    }

    addRole(form: NgForm) {
        this.model.permissions = [];
        let permissions: Permission[] = Object.assign([], this.permissions);
        // flatten permission structure and set it to model
        while (permissions.length) {
            let permission = permissions.pop();
            if (permission.selected) {
                this.model.permissions.push(permission);
            }
            if (permission.children) {
                permissions.push.apply(permissions, permission.children);
            }
        }

        this.model.type = this.roleTypeId;

        this.roleService.createRole(this.model).subscribe((resp) => {
            this.router.navigate(['../../'], { relativeTo: this.route }).then(() => {
                this.roleService.onRoleAdded.next(this.model);
            });
        }, error => {
            this.errors = error.json().errors;
        });
    }

    editRole(form: NgForm) {
        // TODO
    }

    onChangeRoleType() {
        this.drawTree();
    }

    drawTree() {
        this.roleService.getPermissionsTree(this.roleTypeId).subscribe((root: Permission) => {
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
}

/**
 * Recursive component to display a tree of Permissions
 *
 * Note: If the requirement evolves beyond this basic prototype
 *  or gets too complex (shift-select), consider looking for a library
 */
@Component({
    selector: 'permission-tree',
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
				<permission-tree *ngIf="permission.children" [permissions]="permission.children"></permission-tree>
			</li>
		</ul>
    `
})
export class PermissionTree {
    @Input() permissions: Permission [];
    @Input() isLevelZero: boolean;

    isShowCheckbox(permission: Permission) {
        return permission.selectable;
    }

    onPermissionClick(event: any, permission: Permission) {
        if (permission.children) {
            let permissions = Object.assign([], permission.children);
            while (permissions.length) {
                let descendant: Permission = permissions.pop();
                descendant.selected = event.currentTarget.checked;
                descendant.disabled = event.currentTarget.checked;
                if (descendant.children) {
                    permissions.push.apply(permissions, descendant.children);
                }
            }
        }
    }
}