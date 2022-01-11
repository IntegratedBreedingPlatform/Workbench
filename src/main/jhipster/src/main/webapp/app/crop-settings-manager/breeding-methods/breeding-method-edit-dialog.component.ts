import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { AlertService } from '../../shared/alert/alert.service';
import { CropSettingsContext } from '../crop-Settings.context';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { PopupService } from '../../shared/modal/popup.service';
import { BreedingMethodType } from '../../shared/breeding-method/model/breeding-method-type.model';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { BreedingMethodClass } from '../../shared/breeding-method/model/breeding-method-class.model';
import { BreedingMethodGroup } from '../../shared/breeding-method/model/breeding-method-group.model';

@Component({
    selector: 'jhi-breeding-method-edit-dialog',
    templateUrl: './breeding-method-edit-dialog.component.html'
})
export class BreedingMethodEditDialogComponent implements OnInit, OnDestroy {

    breedingMethodId: number;

    isLoading: boolean;

    breedingMethodRequest: any;

    breedingMethodTypes: BreedingMethodType[] = [];
    breedingMethodClasses: BreedingMethodClass[] = [];
    breedingMethodClassesFileted: BreedingMethodClass[] = [];

    breedingMethodGroups: BreedingMethodGroup[] = [];

    nonSpecifyBreedingMethodType: BreedingMethodType = new BreedingMethodType('');
    nonSpecifyBreedingMethodGroups: BreedingMethodGroup = new BreedingMethodGroup('');

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private breedingMethodService: BreedingMethodService,
                private alertService: AlertService,
                private cropSettingsContext: CropSettingsContext) {

        this.breedingMethodRequest = { code: null, name: null, description: null, type: null, methodClass: null, group: null };
    }

    ngOnDestroy(): void {
        this.cropSettingsContext.breedingMethod = null;
    }

    ngOnInit(): void {
        this.breedingMethodService.queryBreedingMethodTypes().subscribe(
            (resp: BreedingMethodType[]) => {
                this.breedingMethodTypes = resp;
                this.breedingMethodTypes.unshift(this.nonSpecifyBreedingMethodType);
                if (this.cropSettingsContext.breedingMethod) {
                    this.breedingMethodRequest.type = this.cropSettingsContext.breedingMethod.type;
                }
            },
            (res: HttpErrorResponse) => this.onError(res)
        );

        this.breedingMethodService.queryBreedingMethodGroups().subscribe(
            (resp: BreedingMethodGroup[]) => {
                this.breedingMethodGroups = resp;
                this.breedingMethodGroups.unshift(this.nonSpecifyBreedingMethodGroups);
                if (this.cropSettingsContext.breedingMethod) {
                    this.breedingMethodRequest.group = this.cropSettingsContext.breedingMethod.group;
                }
            },
            (res: HttpErrorResponse) => this.onError(res)
        );

        this.breedingMethodService.queryBreedingMethodClasses().subscribe(
            (resp: BreedingMethodClass[]) => {
                this.breedingMethodClasses = resp;
                this.breedingMethodClassesFileted = resp.filter((breedingMethodClass) => breedingMethodClass.methodTypeCode === this.breedingMethodRequest.type);
                if (this.cropSettingsContext.breedingMethod) {
                    this.breedingMethodRequest.methodClass = this.cropSettingsContext.breedingMethod.methodClass;
                }
            },
            (res: HttpErrorResponse) => this.onError(res)
        );

        if (this.cropSettingsContext.breedingMethod) {
            this.breedingMethodId = this.cropSettingsContext.breedingMethod.mid;
            this.breedingMethodRequest.name = this.cropSettingsContext.breedingMethod.name;
            this.breedingMethodRequest.code = this.cropSettingsContext.breedingMethod.code;
            this.breedingMethodRequest.description = this.cropSettingsContext.breedingMethod.description;

            this.breedingMethodRequest.separator = this.cropSettingsContext.breedingMethod['separator'];
            this.breedingMethodRequest.prefix = this.cropSettingsContext.breedingMethod['prefix'];
            this.breedingMethodRequest.count = this.cropSettingsContext.breedingMethod['count'];
            this.breedingMethodRequest.suffix = this.cropSettingsContext.breedingMethod['suffix'];

        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isLoading = true;
        if (this.breedingMethodId) {
            this.breedingMethodService.updateBreedingMethod(this.breedingMethodRequest, this.breedingMethodId).toPromise().then((result) => {
                this.alertService.success('crop-settings-manager.breeding-method.modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        } else {
            this.breedingMethodService.createBreedingMethod(this.breedingMethodRequest).toPromise().then((result) => {
                this.alertService.success('crop-settings-manager.breeding-method.modal.create.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        }

    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.breedingMethodRequest.name && this.breedingMethodRequest.code
            && this.breedingMethodRequest.description && this.breedingMethodRequest.type && this.breedingMethodRequest.methodClass;
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'breedingMethodViewChanged' });
        this.clear();
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
    }

    generationAdvancementTypeChanged() {
        if (this.breedingMethodRequest.type !== this.nonSpecifyBreedingMethodType.code) {
            this.breedingMethodClassesFileted = this.breedingMethodClasses.filter((breedingMethodClass) => breedingMethodClass.methodTypeCode === this.breedingMethodRequest.type);
        } else {
            this.breedingMethodRequest.type = null;
            this.breedingMethodClassesFileted = [];
        }
        this.breedingMethodRequest.methodClass = null;
    }
}

@Component({
    selector: 'jhi-breeding-method-edit-popup',
    template: ''
})
export class BreedingMethodEditPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        this.popupService.open(BreedingMethodEditDialogComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }

}
