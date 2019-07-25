import { Component, Input, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '../shared/models/role.model';
import { RoleType } from '../shared/models/role-type.model';
import { RoleService } from '../shared/services/role.service';
import { Permission } from '../shared/models/permission.model';

@Component({
    selector: 'role-card',
    templateUrl: './role-card.component.html',
    moduleId: module.id
})

export class RoleCardComponent implements OnInit {
    isEditing: boolean;
    isVisible = true;

    model: Role = new Role();
    // TODO merge into Role
    roleType: RoleType;
    roleTypes: RoleType[];

    permissionsSelected: Permission[];
    // TODO hook service
    permissions: Permission[] = [
        {
            id: 1,
            name: 'CROP_MANAGEMENT',
            description: 'Crop management',
            children: [{}],
            selectable: false,
        },
        {
            id: 2,
            name: 'MANAGE_ONTOLOGIES',
            description: 'Manage Ontologies',
            children: [{}],
            selectable: true,
        },
        {
            id: 3,
            name: 'MANAGE_GERMPLASM',
            description: 'Manage Germplasm',
            children: [
                {
                    id: 1,
                    name: 'CROP_MANAGEMENT',
                    description: 'Crop management',
                    children: [{}],
                    selectable: false,
                },
                {
                    id: 2,
                    name: 'MANAGE_ONTOLOGIES',
                    description: 'Manage Ontologies',
                    children: [{}],
                    selectable: true,
                },
                {
                    id: 3,
                    name: 'MANAGE_GERMPLASM',
                    description: 'Manage Germplasm',
                    children: [

                    ],
                    selectable: true,
                }

            ],
            selectable: true,
        }
    ];

    constructor(private route: ActivatedRoute,
                private router: Router,
                private roleService: RoleService) {
    }

    ngOnInit() {
        this.route.params.subscribe(params => {
            this.isEditing = params['isEditing'];
        });
        this.roleService.getRoleTypes().subscribe((roleTypes) => {
            this.roleTypes = roleTypes;
        });
    }

    isFormValid(form: NgForm) {
        return form.valid;
    }

    cancel(form: NgForm) {
        this.router.navigate(['../'], { relativeTo: this.route });
    }

    addRole(form: NgForm) {
        this.roleService.onRoleAdded.next(this.model);
    }

    editRole(form: NgForm) {

    }

    changeRoleType() {

    }

    moveSelected(permissions: Permission[], doRemove: boolean) {
        if (!permissions) {
            return;
        }

        for (const permission of permissions) {
            if (permission.selected) {
                permission.transferred = !doRemove;
            }
            if (permission.children) {
                this.moveSelected(permission.children, doRemove);
            }
        }
    }
}

/**
 * Recursive component to display a tree of Permissions
 * Using a combination of the permission.transferred property and the #isSelectedPermissionTable flag
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
    //  - show non-selected grandparents in right table
    template: `
		<ul class="ul-tree" [class.ul-tree-level-zero]="isLevelZero">
			<li *ngFor="let permission of permissions">
				<ng-container *ngIf="!isSelectedPermissionTable || permission.transferred">
					<div [class.checkbox]="permission.selectable">
						<label>
							<input type="checkbox" *ngIf="permission.selectable && (isSelectedPermissionTable || !permission.transferred)"
                                   [(ngModel)]="permission.selected" >
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
}