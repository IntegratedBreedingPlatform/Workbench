import { Component, EventEmitter, Input, OnInit, Output, Pipe, PipeTransform } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { User } from '../shared/models/user.model';

import { UserService } from './../shared/services/user.service';
import { RoleService } from './../shared/services/role.service';
import { MailService } from './../shared/services/mail.service';
import { Role } from './../shared/models/role.model';
import { Response } from '@angular/http';
import { Crop } from '../shared/models/crop.model';
import { Select2OptionData } from 'ng2-select2';
import { ModalContext } from '../shared/components/dialog/modal.context';
import { UserRole } from '../shared/models/user-role.model';

@Component({
    selector: 'user-card',
    templateUrl: './user-card.component.html',
    moduleId: module.id
})

export class UserCard implements OnInit {

    active: boolean = true;

    errorUserMessage: string = '';
    errorClass: string = 'alert alert-danger';
    submitted = false;
    sendingEmail: boolean = false;
    isEditing: boolean;
    sendMail: boolean;

    @Input() originalUser: User;
    @Input() userSaved: boolean = false;
    @Input() model: User;
    @Input() roles: Role[];
    @Output() onUserAdded = new EventEmitter<User>();
    @Output() onUserEdited = new EventEmitter<User>();
    @Output() onCancel = new EventEmitter<void>();

    userRoles: UserRole[];
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
    @Input() crops: Select2OptionData[];
    @Input() selectedCropIds: string[];

    modalTitle: string = 'Delete Role';
    modalMessage: string = 'Selected role will be dissociated from user. If it is a crop or program role, the user will no longer have access to the crop or program associated to the role. Do you want to proceed?';

    public select2Options = {
        multiple: true,
        theme: 'classic'
    };

    constructor(private userService: UserService, private roleService: RoleService, private mailService: MailService, private modalContext: ModalContext) {
        // New empty user is built to open a form with empty default values
        // id, firstName, lastName, username, role, email, status
        this.model = new User('0', '', '', '', [], [], '', 'true');
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

    cancel(form: FormGroup) {
        form.reset();
        this.errorUserMessage = '';
        this.onCancel.emit();
    }

    ngOnInit() {
    }

    onChangeCrop(data: {value: string[]}) {
        if (!data || !data.value) {
            return;
        }
        this.model.crops = data.value.map((cropName) => {
            return {
                cropName: cropName
            };
        });
    }

    addUser(form: FormGroup) {
        this.userService
            .save(this.trimAll(this.model))
            .subscribe(
                resp => {
                    this.userSaved = true;
                    this.errorUserMessage = '';
                    this.sendEmailToResetPassword(resp);
                },
                error => {
                    this.errorUserMessage = this.mapErrorUser(error.json().ERROR.errors);

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
                },
                error => {
                    this.errorUserMessage = this.mapErrorUser(error.json().ERROR.errors);
                });
    }

    AssignRole() {
        this.modalContext.popupVisible['assign-roles'] = true;
        if (this.modalContext.popupVisible['user-create']) {
            this.modalContext.popupVisible['user-create'] = false;
        } else {
            this.modalContext.popupVisible['user-edit'] = false;
        }
    }

    private mapErrorUser(response: any): string {
        return response.map(this.toErrorUser);
    }

    private toErrorUser(r: any): string {
        let msg = {
            fieldNames: r.fieldNames,
            message: r.message,
        }
        return ' ' + msg.fieldNames + ' ' + msg.message;
    }

    private sendEmailToResetPassword(respSaving: Response) {
        if (!this.isEditing) {
            this.model.id = respSaving.json().id;
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
                            if (!this.isEditing) {
                                this.onUserAdded.emit(this.model);
                            } else {
                                this.onUserEdited.emit(this.model);
                            }
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
                            if (!this.isEditing) {
                                this.onUserAdded.emit(this.model);
                            } else {
                                this.onUserEdited.emit(this.model);
                            }
                        }, 2000);
                    }
                );
        } else {
            setTimeout(() => {
                this.userSaved = false;
                this.sendMail = !this.isEditing;
                if (!this.isEditing) {
                    this.onUserAdded.emit(this.model);
                } else {
                    this.onUserEdited.emit(this.model);
                }
            }, 1000);
        }
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

    showDeleteUserRoleConfirmPopUp(userRole: UserRole){
        this.showDeleteUserRoleConfirmPopUpDialog = true;
        this.userRoleSelected = userRole;
    }

    closeUserRoleDeleteConfirmPopUp(){
        this.showDeleteUserRoleConfirmPopUpDialog = false;
    }

    deleteUserRole(){
        const idx = this.model.userRoles.indexOf(this.userRoleSelected);
        this.model.userRoles.splice(idx, 1);
        this.showDeleteUserRoleConfirmPopUpDialog = false;
        this.userRoleSelected = undefined;
    }
}

@Pipe({name: 'toSelect2OptionData'})
export class ToSelect2OptionDataPipe implements PipeTransform {
    transform(crops: Crop[]): Select2OptionData[] {
        if (!crops) {
            return [];
        }
        return crops.map((crop) => {
            return {
                id: crop.cropName,
                text : crop.cropName
            };
        });
    }
}

@Pipe({name: 'toSelect2OptionId'})
export class ToSelect2OptionIdPipe implements PipeTransform {
    transform(crops: Crop[]): string[] {
        if (!crops) {
            return [];
        }
        return crops.map((crop) => crop.cropName);
    }
}

