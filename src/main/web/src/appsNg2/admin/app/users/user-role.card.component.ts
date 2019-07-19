import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Crop } from '../shared/models/crop.model';
import { Program } from '../shared/models/program.model';
import { Role } from '../shared/models/role.model';
import { RoleType } from '../shared/models/role-type.model';
import { User } from '../shared/models/user.model';
import { RoleService } from '../shared/services/role.service';
import { ModalContext } from '../shared/components/dialog/modal.context';
import { RoleFilter } from '../shared/models/role-filter.model';


@Component({
    selector: 'user-role-card',
    templateUrl: './user-role.card.component.html',
    moduleId: module.id
})

export class UserRoleCard implements OnInit {

    active: boolean = true;
    roleTypes: RoleType[];
    roles: Role[];
    crops: Crop[];
    programs: Program[];
    roleTypeSelected: number;
    roleSelected: Role;
    cropSelected = '';
    programSelected = '';
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
        this.resetCombo();
        this.modalContext.popupVisible["assign-roles"] = false;
        this.modalContext.popupVisible[this.modalPrevius] = true;
    }

    changeRole() {
        if (this.roleTypeSelected === undefined) {
            this.isRoleNameComboDisable = true;
            this.isCropComboDisable = true;
            this.isProgramComboDisable = true;
            return
        }

        let roleFilter = new RoleFilter([], true, this.roleTypeSelected);
        this.roleService.getFilteredRoles(roleFilter).subscribe(resp => {
                this.roles = resp;
                this.crops = this.model.crops;

                console.log('Select Role Type ' + this.roleTypeSelected);
                this.programs = [];
                this.roleSelected = undefined;
                this.cropSelected = '';
                this.programSelected = '';
                this.isRoleNameComboDisable = false;
                switch (Number(this.roleTypeSelected)) {

                    case 1:
                        console.log('Debe habilitar No habilita Crop y program');
                        this.cropSelected = '';
                        this.programSelected = '';
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

    resetCombo() {
        this.roleTypeSelected = undefined;
        this.roleSelected = undefined;
        this.cropSelected = '';
        this.programSelected = '';
    }
    assignRole() {
        this.resetCombo();
        this.modalContext.popupVisible["assign-roles"] = false;
    }

    isDisable(selected) {
        return selected === '' || selected === undefined ? 'disabled' : null;
    }

    ngOnInit() {
    }

    changeCrop() {
        this.roleService.getPrograms(this.cropSelected).subscribe(resp => {
            this.programs = resp;
        });
    }
}

