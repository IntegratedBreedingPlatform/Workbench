import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Crop } from '../shared/models/crop.model';
import { Program } from '../shared/models/program.model';
import { Role } from '../shared/models/role.model';
import { RoleType } from '../shared/models/role-type.model';
import { User } from '../shared/models/user.model';
import { RoleService } from '../shared/services/role.service';
import { ModalContext } from '../shared/components/dialog/modal.context';
import { RoleFilter } from '../shared/models/role-filter.model';
import { UserRole } from '../shared/models/user-role.model';


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

    roleTypeSelected: number = 0;
    roleSelected: any = '';
    cropSelected: any = '';
    programSelected: any = '';

    isRoleNameComboDisable: boolean = true;
    isCropComboDisable: boolean = true;
    isProgramComboDisable: boolean = true;

    @Input() model: User;
    @Input() modalPrevius: string;
    onCancel = new EventEmitter<void>();

    constructor(private roleService: RoleService, private modalContext: ModalContext) {
        this.roleService.getRoleTypes().subscribe(resp => {
                this.roleTypes = resp;
            }
        );
    }

    cancel() {
        this.reset();
        this.modalContext.popupVisible['assign-roles'] = false;
        this.modalContext.popupVisible[this.modalPrevius] = true;
    }

    changeRole() {
        if (this.roleTypeSelected === 0) {
            this.isRoleNameComboDisable = true;
            this.isCropComboDisable = true;
            this.isProgramComboDisable = true;
            return
        }

        let roleFilter = new RoleFilter([], true, this.roleTypeSelected);
        this.roleService.getFilteredRoles(roleFilter).subscribe(resp => {
                this.roles = resp;
                this.crops = this.model.crops;

                this.programs = [];
                this.roleSelected = '';
                this.cropSelected = '';
                this.programSelected = '';
                this.isRoleNameComboDisable = false;
                switch (Number(this.roleTypeSelected)) {

                    case 1:
                        this.isCropComboDisable = true;
                        this.isProgramComboDisable = true;
                        break;
                    case 2:
                        console.log('Debe habilitar Crop');
                        this.isCropComboDisable = false;
                        this.isProgramComboDisable = true;
                        break;
                    case 3:
                        console.log('Debe habilitar Crop, program');
                        this.isCropComboDisable = false;
                        this.isProgramComboDisable = false;
                        break;
                }

            }
        );
    }

    reset() {
        this.roleTypeSelected = 0;
        this.roleSelected = '';
        this.cropSelected = '';
        this.programSelected = '';

        this.isRoleNameComboDisable = true;
        this.isCropComboDisable = true;
        this.isProgramComboDisable = true;
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
            this.reset();
            this.modalContext.popupVisible['assign-roles'] = false;
            this.modalContext.popupVisible[this.modalPrevius] = true;
        }
    }

    isAssignRoleButtonDisable() {
        return !this.roleTypeSelected
            || (this.roleTypeSelected === 1 && !this.roleSelected)
            || (this.roleTypeSelected === 2 && (!this.roleSelected || !this.cropSelected))
            || (this.roleTypeSelected === 3 && (!this.roleSelected || !this.cropSelected || !this.programSelected));
    }

    isUserRoleValid(newUserRole: UserRole): boolean {
        this.errorUserRoleMessage = '';
        let errorMessage = '';
        let result: UserRole[];
        switch (Number(this.roleTypeSelected)) {
            case 1:
                result = this.model.userRoles.filter(userRole => userRole.role.type === newUserRole.role.type);
                errorMessage = 'The user ' + this.model.username + ' has already assigned an Instance role.';
                break;
            case 2:
                result = this.model.userRoles.filter(userRole => userRole.role.type === newUserRole.role.type && userRole.crop === newUserRole.crop);
                errorMessage = 'The user ' + this.model.username + ' has already assigned a Crop role for ' + newUserRole.crop + ' crop.';
                break;
            case 3:
                result = this.model.userRoles.filter(userRole => userRole.role.type === newUserRole.role.type && userRole.crop === newUserRole.crop && userRole.program === newUserRole.program);
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
    }

    changeCrop() {
        this.programSelected = '';
        this.roleService.getPrograms(this.cropSelected.cropName).subscribe(resp => {
            this.programs = resp;
        });
    }
}

