import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '../shared/models/role.model';
import { RoleType } from '../shared/models/role-type.model';
import { RoleService, setParent } from '../shared/services/role.service';
import { Permission } from '../shared/models/permission.model';
import mockData from './permissions-mock';
import { ErrorResponseInterface } from '../shared/services/error-response.interface';

@Component({
    selector: 'role-card',
    templateUrl: './role-card.component.html',
    moduleId: module.id
})

export class RoleCardComponent implements OnInit {
    isEditing: boolean;
    isVisible = true;

    model: Role;
    // TODO merge into Role
    roleType: any = "";
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
            this.isEditing = params['isEditing'];
        });
        this.roleService.getRoleTypes().subscribe((roleTypes) => {
            this.roleTypes = roleTypes;
        });
    }

    isFormValid(form: NgForm) {
        return form.valid && this.hasTransferredPermissions();
    }

    private hasTransferredPermissions(): boolean {
        let permissions: Permission[] = Object.assign([], this.permissions);
        while (permissions.length) {
            let permission = permissions.pop();
            if (permission.isTransferred) {
                return true;
            }
            if (permission.children) {
                permissions.push.apply(permissions, permission.children);
            }
        }
        return false;
    }

    cancel(form: NgForm) {
        this.router.navigate(['../'], { relativeTo: this.route });
    }

    addRole(form: NgForm) {
        this.model.permissions = [];
        let permissions: Permission[] = Object.assign([], this.permissions);
        // flatten permission structure and set it to model
        while (permissions.length) {
            let permission = permissions.pop();
            if (permission.isTransferred) {
                this.model.permissions.push(permission);
            }
            if (permission.children) {
                permissions.push.apply(permissions, permission.children);
            }
        }

        this.model.type = this.roleType.id;

        this.roleService.createRole(this.model).subscribe((resp) => {
            this.router.navigate(['../'], { relativeTo: this.route }).then(() => {
                this.roleService.onRoleAdded.next(this.model);
            });
        }, error => {
            this.errors = error.json().errors;
        });
    }

    editRole(form: NgForm) {
        // TODO
    }

    changeRoleType() {
        this.roleService.getPermissionsTree(this.roleType).subscribe((root: Permission) => {
            this.permissions = setParent([root], null);
        });
    }

    moveSelected(permissions: Permission[], doRemove: boolean) {
        if (!permissions) {
            return;
        }

        for (const permission of permissions) {
            if (permission.selected) {
                permission.isTransferred = !doRemove;
            }
            if (permission.children) {
                this.moveSelected(permission.children, doRemove);
            }
        }
        return permissions;
    }

    /**
     * Iterate over the tree to display all the ancestors of an item that has been transferred
     */
    showTransferredBranches() {
        let parentsToShow: Permission[] = [];
        let permissions: Permission[] = Object.assign([], this.permissions);
        while (permissions && permissions.length) {
            let permission = permissions.pop();
            if (permission.parent && permission.isTransferred && permission.selectable) {
                parentsToShow.push(permission.parent);
            }
            if (permission.children) {
                permissions.push.apply(permissions, permission.children);
            }
            // Take advantage of this tree traversal logic to reset this property
            permission.hasDescendantsTransferred = false;
        }
        // Make all the branch visible
        // Include all the ancestors up to the root
        while (parentsToShow.length) {
            let parent = parentsToShow.pop();
            parent.hasDescendantsTransferred = true;
            if (parent.parent) {
                parentsToShow.push(parent.parent);
            }
        }
    }
}

/**
 * Recursive component to display a tree of Permissions
 * Using a combination of the permission.isTransferred property and the #isSelectedPermissionTable flag
 * it decides to show the selected items or not, which is used to display two trees next to each other:
 * Available and selected.
 *
 * Note: If the requirement evolves beyond this basic prototype
 *  or gets too complex (drag and drop, shift-select), consider looking for a library
 */
@Component({
    selector: 'permission-tree',
    // TODO
    //  - click select children
    template: `
		<ul class="ul-tree" [class.ul-tree-level-zero]="isLevelZero">
			<li *ngFor="let permission of permissions">
				<ng-container *ngIf="isShowContainer(permission)">
					<div [class.checkbox]="permission.selectable">
						<label>
							<input type="checkbox" *ngIf="isShowCheckbox(permission)"
								   [(ngModel)]="permission.selected">
							{{permission.description}}
						</label>
					</div>
				</ng-container>
				<permission-tree *ngIf="permission.children" [permissions]="permission.children"
								 [isSelectedPermissionTable]="isSelectedPermissionTable"></permission-tree>
			</li>
		</ul>
    `
})
export class PermissionTree {
    @Input() permissions: Permission [];
    @Input() isLevelZero: boolean;
    @Input() isSelectedPermissionTable: boolean;

    isShowContainer(permission: Permission) {
        return !this.isSelectedPermissionTable || permission.isTransferred || permission.hasDescendantsTransferred;
    }

    isShowCheckbox(permission: Permission) {
        if (!permission.selectable) {
            return false;
        }
        return !this.isSelectedPermissionTable && !permission.isTransferred
            || this.isSelectedPermissionTable && permission.isTransferred;
    }
}