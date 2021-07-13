import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiLanguageService } from 'ng-jhipster';
import { AlertService } from '../../shared/alert/alert.service';
import { Principal } from '../../shared/auth/principal.service';
import { UserProfileServices } from './service/user-profile-services.service';
import { NavbarMessageEvent } from '../../shared/model/navbar-message.event';

@Component({
    selector: 'jhi-user-profile-update-dialog',
    templateUrl: './user-profile-update-dialog.component.html',
})
export class UserProfileUpdateDialogComponent implements OnInit {

    userId: number;
    model;
    user: any;

    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private route: ActivatedRoute,
        private principal: Principal,
        private userProfileServices: UserProfileServices
    ) {

    }

    ngOnInit(): void {
        this.model = {};
        this.principal.identity().then((identity) => {
            this.user = identity;
            this.model.firstName = this.user.firstName;
            this.model.lastName = this.user.lastName;
            this.model.email = this.user.email;

        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    update(f) {
        this.userProfileServices.update(this.model).subscribe((res: void) => {
            const message: NavbarMessageEvent = { userProfileChanged: true };
            window.parent.postMessage(message, '*');
            this.alertService.success('userProfile.home.messages.updated');
        }, (response) => {
            if (response.error.errors[0].message) {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            } else {
                this.alertService.error('error.general');
            }
        });
        this.activeModal.close();
    }

    isFormValid(f) {
        const form = f.form;
        if (!Object.values(form.controls).filter((control: any) => !control.disabled).length) {
            return false;
        }
        return f.form.valid;
    }
}

@Component({
    selector: 'jhi-user-profile-update-popup',
    template: ''
})
export class UserProfileUpdatePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private popupService: PopupService
    ) {

    }

    ngOnInit() {
        this.routeSub = this.route.params.subscribe(() => {
            this.popupService.open(UserProfileUpdateDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }

}
