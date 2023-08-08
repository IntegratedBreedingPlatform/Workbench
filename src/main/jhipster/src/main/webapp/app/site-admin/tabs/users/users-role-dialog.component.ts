import { Component, OnInit } from '@angular/core';
import { PopupService } from '../../../shared/modal/popup.service';
import { UserEditDialogComponent } from './user-edit-dialog.component';
import { UserService } from '../../services/user.service';
import { RoleService } from '../../services/role.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RoleType } from '../../model/role-type.model';
import { Role } from '../../model/role.model';
import { Crop } from '../../../shared/model/crop.model';
import { Program } from '../../../shared/user/model/program.model';
import { RoleFilter } from '../../model/role-filter.model';
import { UserRole } from '../../../shared/user/model/user-role.model';
import { User } from '../../../shared/user/model/user.model';
import { SiteAdminContext } from '../../site-admin-context';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../../../shared/alert/alert.service';
import { Pageable } from '../../../shared/model/pageable';

@Component({
    selector: 'jhi-users-role-dialog',
    templateUrl: 'users-role-dialog.component.html'
})

export class UserRoleDialogComponent implements OnInit {

    roleTypeSelected: any = '';
    roleSelected: any = '';
    cropSelected: any = '';
    programSelected: any = '';
    predicate: any;

    roleTypes: RoleType[];
    roles: { [id: number]: any } = {};
    programs: { [id: string]: Program } = {};
    roleOptions: any;
    programOptions: any;
    cropOptions: string[];

    model: User;

    constructor(private userService: UserService,
                private roleService: RoleService,
                private router: Router,
                private modalService: NgbModal,
                private modal: NgbActiveModal,
                private alertService: AlertService,
                public translateService: TranslateService,
                private context: SiteAdminContext) {

        this.predicate = ['name'];
        this.roleService.getRoleTypes().subscribe((resp) => {
                this.roleTypes = resp;
            }
        );
    }
    ngOnInit() {
        this.model = this.context.user;
        this.reset();
    }

    changeRole() {
        const roleFilter = new RoleFilter([], true, this.roleTypeSelected);
        this.roleService.searchRoles(roleFilter, <Pageable>({
            page: null,
            size: null,
            sort: this.getSort()
        })).subscribe((resp) => {
                this.roleOptions = resp.body.map((role) => {
                    return {
                        id: role.id,
                        text: role.name
                    }
                });
                this.cropOptions = this.model.crops.map((list) => list.cropName);

                Object.assign(this.roles, resp.body.reduce((prev, curr) => {
                    prev[curr.id] = curr;
                    return prev;
                }, {}));

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
    }

    assignRole() {
        let userRole: UserRole = undefined;
        switch (Number(this.roleTypeSelected)) {
            case 1:
                userRole = new UserRole(null, this.roles[this.roleSelected], null, null, null);
                break;
            case 2:
                userRole = new UserRole(null, this.roles[this.roleSelected], {cropName: this.cropSelected}, null, null);
                break;
            case 3:
                userRole = new UserRole(null, this.roles[this.roleSelected], {cropName: this.cropSelected}, this.programs[this.programSelected], null);
                break;
        }

        if (this.isUserRoleValid(userRole)) {
            this.model.userRoles.push(userRole);
            this.back();
        }
    }

    isRoleNameComboDisable() {
        return !this.roleTypeSelected;
    }

    isCropComboDisable() {
        return this.isRoleNameComboDisable() || this.roleTypeSelected === RoleTypeEnum.INSTANCE;
    }

    isProgramComboDisable() {
        return this.isRoleNameComboDisable() || this.roleTypeSelected !== RoleTypeEnum.PROGRAM;
    }

    isAssignRoleButtonDisable() {
        return !this.roleTypeSelected
            || (this.roleTypeSelected === RoleTypeEnum.INSTANCE && !this.roleSelected)
            || (this.roleTypeSelected === RoleTypeEnum.CROP && (!this.roleSelected || !this.cropSelected))
            || (this.roleTypeSelected === RoleTypeEnum.PROGRAM && (!this.roleSelected || !this.cropSelected || !this.programSelected));
    }

    isUserRoleValid(newUserRole: UserRole): boolean {
        let errorMessage = '';
        let result: UserRole[];
        switch (Number(this.roleTypeSelected)) {
            case 1:
                result = this.model.userRoles.filter((userRole) => userRole.role.roleType.id === newUserRole.role.roleType.id);
                errorMessage = this.translateService.instant('site-admin.user.modal.role.instance.role.error', { user: this.model.username });
                break;
            case 2:
                result = this.model.userRoles.filter((userRole) => userRole.role.roleType.id === newUserRole.role.roleType.id && //
                    userRole.crop.cropName === newUserRole.crop.cropName);
                errorMessage = this.translateService.instant('site-admin.user.modal.role.crop.role.error', { user: this.model.username, cropName: newUserRole.crop.cropName });
                break;
            case 3:
                result = this.model.userRoles.filter((userRole) => userRole.role.roleType.id === newUserRole.role.roleType.id && //
                    userRole.crop.cropName === newUserRole.crop.cropName && userRole.program.uuid === newUserRole.program.uuid);
                errorMessage = this.translateService.instant('site-admin.user.modal.role.program.role.error', {
                    user: this.model.username,
                    programName: newUserRole.program.name
                });
                break;
        }

        if (result.length > 0) {
            this.alertService.error('error.custom', { param: errorMessage });
            return false;
        }
        return true;
    }

    changeCrop() {
        this.programSelected = '';
        this.roleService.getPrograms(this.cropSelected).subscribe((resp) => {
            this.programOptions = resp.map((program) => {
                return {
                    id: program.uuid,
                    text: program.name
                };
            });
            Object.assign(this.programs, resp.reduce((prev, curr) => {
                prev[curr.uuid] = curr;
                return prev;
            }, {}));
        });
    }

    back() {
        this.modal.close();
        this.modalService.open(UserEditDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }

    private getSort() {
        if (!this.predicate) {
            return '';
        }
        return [this.predicate + ',asc'];
    }

}

enum RoleTypeEnum {
    INSTANCE = 1, CROP = 2, PROGRAM = 3
}

@Component({
    selector: 'jhi-user-role-popup',
    template: ''
})
export class UserRolePopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        this.popupService.open(UserRoleDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }
}
