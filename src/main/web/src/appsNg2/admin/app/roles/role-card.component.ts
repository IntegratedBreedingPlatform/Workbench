import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '../shared/models/role.model';
import { RoleType } from '../shared/models/role-type.model';
import { RoleService } from '../shared/services/role.service';

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
