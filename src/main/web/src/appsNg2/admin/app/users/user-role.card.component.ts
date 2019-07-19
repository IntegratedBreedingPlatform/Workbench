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
        this.modalContext.popupVisible["assign-roles"] = false;
        this.modalContext.popupVisible[this.modalPrevius] = true;
    }

    changeRole() {
        let roleFilter = new RoleFilter([], true, this.roleTypeSelected);
        this.roleService.getFilteredRoles(roleFilter).subscribe(resp => {
                this.roles = resp;

                console.log('Select Role Type ' + this.roleTypeSelected);
                this.programs = [];
                this.resetCombo();
                switch (Number(this.roleTypeSelected)) {

                    case 1:
                        console.log('Debe habilitar No habilita Crop y program');
                        this.cropSelected = '';
                        this.programSelected = '';
                        break;
                    case 2:
                        console.log('Debe habilitar Crop');
                        this.crops = this.model.crops;
                        break;
                    case 3:
                        console.log('Debe habilitar Crop, program');
                        break;
                }

            }
        );
    }

    resetCombo(){
        this.roleSelected = undefined;
        this.cropSelected = '';
        this.programSelected = '';
    }
    assignRole() {
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

