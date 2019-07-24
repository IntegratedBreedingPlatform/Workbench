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
        })
    }

    isFormValid(form: NgForm) {
        return form.valid;
    }

    cancel(form: NgForm) {
        this.router.navigate(['../'], { relativeTo: this.route });
    }

    addRole(form: NgForm) {

    }

    editRole(form: NgForm) {

    }

    changeRoleType() {

    }
}

@Component({
    selector: 'permission-tree',
    // TODO level 0 padding 0, extract style
    template: `
		<ul style="list-style-type: none">
			<li *ngFor="let permission of permissions">
				<div *ngIf="!permission.selectable">
					<strong>
						{{permission.description}}
					</strong>
				</div>
				<div class="checkbox" *ngIf="permission.selectable">
					<label>
						<input type="checkbox" value="">
						{{permission.description}}
					</label>
				</div>
				<permission-tree *ngIf="permission.children" [permissions]="permission.children"></permission-tree>
			</li>
		</ul>
    `
})
export class PermissionTree {
    @Input() permissions: Permission [];
}