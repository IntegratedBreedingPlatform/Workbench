import { Component, OnInit, Pipe, PipeTransform } from '@angular/core';
import { PopupService } from '../../../shared/modal/popup.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { AlertService } from '../../../shared/alert/alert.service';
import { SiteAdminContext } from '../../site-admin-context';
import { UserService } from '../../services/user.service';
import { User } from '../../../shared/user/model/user.model';
import { EMAIL_LOCAL_PART_REGEX } from '../../validators/email-validator.component';
import { UserRole } from '../../../shared/user/model/user-role.model';
import { Crop } from '../../../shared/model/crop.model';
import { CropService } from '../../services/crop.service';
import { Select2OptionData } from 'ng-select2';
import { GermplasmListVariableMatchesComponent } from '../../../germplasm-list/import/germplasm-list-variable-matches.component';
import { UserRoleDialogComponent } from './users-role-dialog.component';
import { ModalConfirmComponent } from '../../../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { NavbarMessageEvent } from '../../../shared/model/navbar-message.event';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { MailService } from '../../services/mail.service';
import { finalize } from 'rxjs/internal/operators/finalize';

@Component({
    selector: 'jhi-user-edit-dialog',
    templateUrl: 'user-edit-dialog.component.html'
})

export class UserEditDialogComponent implements OnInit {

    EMAIL_LOCAL_PART_REGEX = EMAIL_LOCAL_PART_REGEX;

    model: User;

    showDeleteUserRoleConfirmPopUpDialog = false;
    crops: Crop[];

    // isLoading: boolean;
    sendingEmail: boolean;
    isEditing: boolean;
    sendMail: boolean;

    userSaved: boolean;

    public select2Options = {
        multiple: true,
        theme: 'classic'
    };

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private userService: UserService,
                private cropService: CropService,
                private alertService: AlertService,
                private mailService: MailService,
                private modalService: NgbModal,
                private modal: NgbActiveModal,
                public translateService: TranslateService,
                public context: SiteAdminContext) {

        this.sendingEmail = false;
        this.userSaved = false;
    }

    ngOnInit(): void {
        this.crops = this.cropService.crops;
        this.model = this.context.user;

        this.isEditing = this.model.id ? true : false;

    }

    onChangeCrop(data: { value: string[] }) {
        if (!data || !data.value) {
            return;
        }
        this.model.crops = data.value.map((cropName) => {
            return {
                cropName
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

    clear() {
        this.activeModal.dismiss('cancel');
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'onUserViewChanged' });
        this.activeModal.close();
    }

    addUser() {
        this.userService
            .save(this.trimAll(this.model))
            .subscribe(
            (resp) => {
                this.userSaved = true;
                this.sendEmailToResetPassword(resp, 'site-admin.user.modal.create.success');
            }, (error) => this.onError(error));
    }

    updateUser() {
        this.userService
            .update(this.trimAll(this.model))
            .subscribe(
                (resp) => {
                    this.userSaved = true;
                    this.sendEmailToResetPassword(resp, 'site-admin.user.modal.edit.success');
                    const message: NavbarMessageEvent = { userProfileChanged: true };
                    window.parent.postMessage(message, '*');
                },
                (res: HttpErrorResponse) => this.onError(res));
    }

    AssignRole() {
        this.modal.close();
        this.modalService.open(UserRoleDialogComponent as Component, { size: 'lg', backdrop: 'static' });
    }

    private sendEmailToResetPassword(respSaving: any, msg: any) {
        if (!this.isEditing) {
            this.model.id = respSaving;
        }
        if (this.sendMail) {
            this.sendingEmail = true;
            this.mailService
                .send(this.model)
                .subscribe(
                    (resp) => {
                        setTimeout(() => {
                            this.alertService.success(msg);
                            this.sendingEmail = false;
                            this.userSaved = false;
                            this.sendMail = !this.isEditing;
                            this.notifyChanges();
                        }, 1000);
                    }, (error) => {
                        setTimeout(() => {
                        this.alertService.success(msg);
                        this.sendingEmail = false;
                        this.userSaved = false;
                        this.sendMail = !this.isEditing;
                        this.onError(error);
                        }, 2000);
                    });
        } else {
            setTimeout(() => {
                this.alertService.success(msg);
                this.userSaved = false;
                this.sendMail = !this.isEditing;
                this.notifyChanges();
            }, 1000);
        }
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
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
        return form.valid && this.model.crops && this.model.crops.length;
    }

    showDeleteUserRoleConfirmPopUp(userRole: UserRole) {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.title = 'Confirmation';
        confirmModalRef.componentInstance.message = this.translateService.instant('site-admin.user.modal.delete-role-assigned-message');
        confirmModalRef.result.then(() => {
            this.deleteUserRole(userRole);
        }, () => confirmModalRef.dismiss());
    }

    deleteUserRole(userRole: UserRole) {
        const idx = this.model.userRoles.indexOf(userRole);
        this.model.userRoles.splice(idx, 1);
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

@Component({
    selector: 'jhi-user-edit-popup',
    template: ''
})
export class UserEditPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        this.popupService.open(UserEditDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }
}
