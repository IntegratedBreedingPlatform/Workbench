import { Component, OnDestroy, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';
import { NameTypeService } from '../../shared/name-type/service/name-type.service';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PopupService } from '../../shared/modal/popup.service';
import { NameType } from '../../shared/germplasm/model/name-type.model';
import { CropSettingsContext } from '../crop-Settings.context';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';

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
                private cropSettingsContext: CropSettingsContext) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        if (this.nameTypeId) {
            this.isLoading = true;
            this.nameTypeService.updateNameType(this.nameType, this.nameTypeId).pipe(
                finalize(() => this.isLoading = false)
            ).subscribe(() => {
                this.alertService.success('crop-settings-manager.name-type.modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }, (error) => this.onError(error));
        } else {
            this.isLoading = true;
            this.nameTypeService.createNameType(this.nameType).pipe(
                finalize(() => this.isLoading = false)
            ).subscribe(() => {
                this.alertService.success('crop-settings-manager.name-type.modal.create.success');
                this.notifyChanges();
                this.isLoading = false;
            }, (error) => this.onError(error));
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

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.nameType.name
            && this.nameType.code;
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'nameTypeViewChanged', content: '' });
        this.clear();
    }

    ngOnInit(): void {
        if (this.cropSettingsContext.nameTypeDetails) {
            this.nameTypeId = this.cropSettingsContext.nameTypeDetails.id;
            this.nameType.code = this.cropSettingsContext.nameTypeDetails.code;
            this.nameType.name = this.cropSettingsContext.nameTypeDetails.name;
            this.nameType.description = this.cropSettingsContext.nameTypeDetails.description;
        }
    }

    ngOnDestroy(): void {
        this.cropSettingsContext.nameTypeDetails = null;
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
