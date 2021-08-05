import { Component, OnDestroy, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { NameTypeService } from '../../shared/name-type/service/name-type.service';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../../shared/service/param.context';
import { PopupService } from '../../shared/modal/popup.service';
import { NameTypeContext } from './name-type.context';
import { NameType } from '../../shared/germplasm/model/name-type.model';

@Component({
    selector: 'jhi-name-type-edit-dialog',
    templateUrl: './name-type-edit-dialog.component.html'
})
export class NameTypeEditDialogComponent implements OnInit, OnDestroy {

    nameTypeId: number;
    code: string;
    name: string;
    description: string;
    isLoading: boolean;
    nameType: NameType = new NameType();

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private nameTypeService: NameTypeService,
                private alertService: AlertService,
                private nameTypeContext: NameTypeContext) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        if (this.nameTypeId) {
            this.isLoading = true;
            this.nameTypeService.updateNameType(this.nameType, this.nameTypeId).toPromise().then((result) => {
                this.alertService.success('metadata-manager.name-type.modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        } else {
            this.isLoading = true;
            this.nameTypeService.createNameType(this.nameType).toPromise().then((result) => {
                this.alertService.success('metadata-manager.name-type.modal.create.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        }
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.nameType.name
            && this.nameType.code;
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'nameTypeViewChanged' });
        this.clear();
    }

    ngOnInit(): void {
        if (this.nameTypeContext.nameTypeDetails) {
            this.nameTypeId = this.nameTypeContext.nameTypeDetails.id;
            this.nameType.code = this.nameTypeContext.nameTypeDetails.code;
            this.nameType.name = this.nameTypeContext.nameTypeDetails.name;
            this.nameType.description = this.nameTypeContext.nameTypeDetails.description;
        }
    }

    ngOnDestroy(): void {
        this.nameTypeContext.nameTypeDetails = null;
    }
}

@Component({
    selector: 'jhi-name-type-edit-popup',
    template: ''
})
export class NameTypeEditPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        this.popupService.open(NameTypeEditDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }
}
