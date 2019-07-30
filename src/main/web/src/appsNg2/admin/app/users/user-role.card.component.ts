import { Component, EventEmitter, Input, OnInit } from '@angular/core';
import { Crop } from '../shared/models/crop.model';
import { Program } from '../shared/models/program.model';
import { Role } from '../shared/models/role.model';
import { RoleType } from '../shared/models/role-type.model';
import { User } from '../shared/models/user.model';
import { RoleService } from '../shared/services/role.service';
import { RoleFilter } from '../shared/models/role-filter.model';
import { UserRole } from '../shared/models/user-role.model';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../shared/services/user.service';


@Component({
    selector: 'user-role-card',
    templateUrl: './user-role.card.component.html',
    moduleId: module.id
})

export class UserRoleCard implements OnInit {

    active: boolean = true;

    errorClass: string = 'alert alert-danger';
    errorUserRoleMessage: string = '';

    roleTypes: RoleType[];
    roles: Role[];
    crops: Crop[];
    programs: Program[];

    roleTypeSelected: any = '';
    roleSelected: any = '';
    cropSelected: any = '';
    programSelected: any = '';

    model: User;
    onCancel = new EventEmitter<void>();
    isEditing: Boolean;

    constructor(private userService: UserService, private roleService: RoleService, private router: Router, private activatedRoute: ActivatedRoute) {
        this.roleService.getRoleTypes().subscribe(resp => {
                this.roleTypes = resp;
            }
        );
    }

    cancel() {
        this.router.navigate(['../user-card', { isEditing: this.isEditing }], { relativeTo: this.activatedRoute });
    }

    changeRole() {
        let roleFilter = new RoleFilter([], true, this.roleTypeSelected);
        this.roleService.getFilteredRoles(roleFilter).subscribe(resp => {
                this.roles = resp;
                this.crops = this.model.crops;

                this.programs = [];
                this.roleSelected = '';
                this.cropSelected = '';
                this.programSelected = '';
            }
        );
    }

    reset() {
        this.roleTypeSelected = '';
        this.roleSelected = '';
        this.cropSelected = '';
        this.programSelected = '';
        this.errorUserRoleMessage = '';
    }

    assignRole() {
        let userRole: UserRole = undefined;
        switch (Number(this.roleTypeSelected)) {
            case 1:
                userRole = new UserRole(null, this.roleSelected, null, null, null);
                break;
            case 2:
                userRole = new UserRole(null, this.roleSelected, this.cropSelected, null, null);
                break;
            case 3:
                userRole = new UserRole(null, this.roleSelected, this.cropSelected, this.programSelected, null);
                break;
        }

        if (this.isUserRoleValid(userRole)) {
            this.model.userRoles.push(userRole);
            this.router.navigate(['../user-card', { isEditing: this.isEditing }], { relativeTo: this.activatedRoute });
        }
    }

    isRoleNameComboDisable(){
        return !this.roleTypeSelected;
    }

    isCropComboDisable(){
        return this.isRoleNameComboDisable() || this.roleTypeSelected === RoleTypeEnum.INSTANCE;
    }

    isProgramComboDisable(){
        return this.isRoleNameComboDisable() || this.roleTypeSelected != RoleTypeEnum.PROGRAM;
    }

    isAssignRoleButtonDisable() {
        return !this.roleTypeSelected
            || (this.roleTypeSelected === RoleTypeEnum.INSTANCE && !this.roleSelected)
            || (this.roleTypeSelected === RoleTypeEnum.CROP && (!this.roleSelected || !this.cropSelected))
            || (this.roleTypeSelected === RoleTypeEnum.PROGRAM && (!this.roleSelected || !this.cropSelected || !this.programSelected));
    }

    isUserRoleValid(newUserRole: UserRole): boolean {
        this.errorUserRoleMessage = '';
        let errorMessage = '';
        let result: UserRole[];
        switch (Number(this.roleTypeSelected)) {
            case 1:
                result = this.model.userRoles.filter(userRole => userRole.role.type == newUserRole.role.type);
                errorMessage = 'The user ' + this.model.username + ' has already assigned an Instance role.';
                break;
            case 2:
                result = this.model.userRoles.filter(userRole => userRole.role.type == newUserRole.role.type && userRole.crop.cropName === newUserRole.crop.cropName);
                errorMessage = 'The user ' + this.model.username + ' has already assigned a Crop role for ' + newUserRole.crop.cropName + ' crop.';
                break;
            case 3:
                result = this.model.userRoles.filter(userRole => userRole.role.type == newUserRole.role.type && userRole.crop.cropName === newUserRole.crop.cropName && userRole.program.uuid === newUserRole.program.uuid);
                errorMessage = 'The user ' + this.model.username + ' has already assigned a Program role for ' + newUserRole.program.name + ' program.';
                break;
        }

        if (result.length > 0) {
            this.errorUserRoleMessage = errorMessage;
            return false;
        }
        return true;
    }

    ngOnInit() {
        this.activatedRoute.params.subscribe(params => {
            this.isEditing = params['isEditing'] === 'true';
        });

        this.model = this.userService.user;
        this.reset();
    }

    changeCrop() {
        this.programSelected = '';
        this.roleService.getPrograms(this.cropSelected.cropName).subscribe(resp => {
            this.programs = resp;
        });
    }
}

enum RoleTypeEnum {
    INSTANCE = 1, CROP = 2, PROGRAM = 3
}