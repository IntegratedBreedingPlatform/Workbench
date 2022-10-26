import { Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { User } from '../shared/models/user.model';

import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { MailService } from './../shared/services/mail.service';
import { Response } from '@angular/http';
import { Crop } from '../shared/models/crop.model';
import { Select2OptionData } from 'ng2-select2';
import { UserRole } from '../shared/models/user-role.model';
import { ActivatedRoute, Router } from '@angular/router';
import { CropService } from '../shared/services/crop.service';
import { EMAIL_LOCAL_PART_REGEX } from '../shared/validators/email-validator.component';
import { NavbarMessageEvent } from '../../../../../../jhipster/src/main/webapp/app/shared/model/navbar-message.event';

@Component({
    selector: 'user-card',
    templateUrl: './user-card.component.html',
    moduleId: module.id
})

export class UserCard implements OnInit {

    EMAIL_LOCAL_PART_REGEX = EMAIL_LOCAL_PART_REGEX;

    active: boolean = true;

    dialogTitle: string;

    errorUserMessage: string = '';
    errorClass: string = 'alert alert-danger';
    submitted = false;
    sendingEmail: boolean = false;
    isEditing: boolean;
    sendMail: boolean;

    userSaved: boolean = false;
    model: User;

    showDeleteUserRoleConfirmPopUpDialog = false;
    userRoleSelected: UserRole;
    /*
     * TODO Multi-select:
     *  - ng2-select2 is a bit wonky -> find alternative
     *  - validations: pristine/touched not working
     *     See https://github.com/NejcZdovc/ng2-select2/issues/13
     *      and https://github.com/NejcZdovc/ng2-select2/issues/101
     *  - won't work with the latest version https://github.com/NejcZdovc/ng2-select2/issues/144
     */
    crops: Crop[];

    modalTitle: string = 'Delete Role';
    modalMessage: string = 'Selected role will be dissociated from user. If it is a crop or program role, the user will no longer have access to the crop or program associated to the role. Do you want to proceed?';

    public select2Options = {
        multiple: true,
        theme: 'classic'
    };

    constructor(private cropService: CropService, private userService: UserService, private roleService: RoleService, private mailService: MailService, private router: Router, private activatedRoute: ActivatedRoute) {
        // New empty user is built to open a form with empty default values
        // id, firstName, lastName, username, role, email, status
        this.model = new User('0', '', '', '', [], [], '', 'true', false);
        this.errorUserMessage = '';
    }

    /**
     * XXX
     * Reset form hack
     * see https://angular.io/docs/ts/latest/guide/forms.html#!#add-a-hero-and-reset-the-form
     */
    resetForm() {
        this.active = false;
        setTimeout(() => this.active = true, 0);
    }

    initialize(isEditing: boolean) {
        this.isEditing = isEditing;
        this.errorUserMessage = '';
        this.sendMail = !this.isEditing;
    }

    onSubmit() {
        this.submitted = true;
    }

    cancel() {
        this.errorUserMessage = '';
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
    }

    ngOnInit() {
        this.activatedRoute.params.subscribe(params => {
            this.isEditing = params['isEditing'] === 'true';
            this.errorUserMessage = '';
            this.sendMail = !this.isEditing;


            this.dialogTitle = this.isEditing ? 'Edit User' : 'Add User';
            this.model = this.userService.user;
            this.crops = this.cropService.crops;
        });
    }

    onChangeCrop(data: { value: string[] }) {
        if (!data || !data.value) {
            return;
        }
        this.model.crops = data.value.map((cropName) => {
            return {
                cropName: cropName
            };
        });
    }

    onSelectAllCrops($event: any) {
        if ($event.currentTarget.checked) {
            this.model.crops = Object.assign([], this.crops);
        } else {
            this.model.crops = [];
        }
    }

    addUser() {
        this.userService
            .save(this.trimAll(this.model))
            .subscribe(
                resp => {
                    this.userSaved = true;
                    this.errorUserMessage = '';
                    this.sendEmailToResetPassword(resp);
                },
                error => {
                    this.errorUserMessage = this.mapErrorUser(error.json().errors);
                });
    }

    editUser() {
        this.userService
            .update(this.trimAll(this.model))
            .subscribe(
                resp => {
                    this.userSaved = true;
                    this.errorUserMessage = '';
                    this.sendEmailToResetPassword(resp);
                    const message: NavbarMessageEvent = { userProfileChanged: true };
                    window.parent.postMessage(message, '*');
                },
                error => {
                    this.errorUserMessage = this.mapErrorUser(error.json().errors);
                });
    }

    AssignRole() {
        this.router.navigate(['../user-role-card', { isEditing: this.isEditing }], { relativeTo: this.activatedRoute });
    }

    private mapErrorUser(response: any): string {
        return response.map(e => e.message + ' ');
    }

    private sendEmailToResetPassword(respSaving: Response) {
        if (!this.isEditing) {
            this.model.id = respSaving.text();
        }
        if (this.sendMail) {
            this.sendingEmail = true;
            this.mailService
                .send(this.model)
                .subscribe(
                    resp => {
                        setTimeout(() => {
                            this.sendingEmail = false;
                            this.userSaved = false;
                            this.sendMail = !this.isEditing;
                            this.updateUserDataGridTable();
                        }, 1000);
                    },
                    error => {
                        this.sendingEmail = false;
                        this.errorClass = 'alert alert-warning';
                        this.errorUserMessage = 'Email was not sent. Please contact your system administrator';
                        setTimeout(() => {
                            this.errorUserMessage = '';
                            this.errorClass = 'alert alert-danger';
                            this.userSaved = false;
                            this.sendMail = !this.isEditing;
                            this.updateUserDataGridTable();
                        }, 2000);
                    }
                );
        } else {
            setTimeout(() => {
                this.userSaved = false;
                this.sendMail = !this.isEditing;
                this.updateUserDataGridTable();
            }, 1000);
        }
    }

    private updateUserDataGridTable() {
        if (!this.isEditing) {
            this.userService.onUserAdded.next(this.model);
        } else {
            this.userService.onUserUpdated.next(this.model);
        }
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
    }

    private trimAll(model: User) {
        model.firstName = model.firstName.trim();
        model.lastName = model.lastName.trim();
        model.email = model.email.trim();
        model.username = model.username.trim();
        return model;
    }

    isFormValid(form) {
        return form.valid && this.model.crops.length;
    }

    showDeleteUserRoleConfirmPopUp(userRole: UserRole) {
        this.showDeleteUserRoleConfirmPopUpDialog = true;
        this.userRoleSelected = userRole;
    }

    closeUserRoleDeleteConfirmPopUp() {
        this.showDeleteUserRoleConfirmPopUpDialog = false;
    }

    deleteUserRole() {
        const idx = this.model.userRoles.indexOf(this.userRoleSelected);
        this.model.userRoles.splice(idx, 1);
        this.showDeleteUserRoleConfirmPopUpDialog = false;
        this.userRoleSelected = undefined;
    }
}

@Pipe({ name: 'toSelect2OptionData' })
export class ToSelect2OptionDataPipe implements PipeTransform {
    transform(crops: Crop[]): Select2OptionData[] {
        if (!crops) {
            return [];
        }
        return crops.map((crop) => {
            return {
                id: crop.cropName,
                text: crop.cropName
            };
        });
    }
}

@Pipe({ name: 'toSelect2OptionId' })
export class ToSelect2OptionIdPipe implements PipeTransform {
    transform(crops: Crop[]): string[] {
        if (!crops) {
            return [];
        }
        return crops.map((crop) => crop.cropName);
    }
}

