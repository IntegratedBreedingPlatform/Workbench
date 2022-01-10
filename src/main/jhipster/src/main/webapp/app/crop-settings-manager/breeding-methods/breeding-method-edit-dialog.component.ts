import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { AlertService } from '../../shared/alert/alert.service';
import { CropSettingsContext } from '../crop-Settings.context';
import { BreedingMethodService } from '../../shared/breeding-method/service/breeding-method.service';
import { PopupService } from '../../shared/modal/popup.service';

@Component({
    selector: 'jhi-breeding-method-edit-dialog',
    templateUrl: './breeding-method-edit-dialog.component.html'
})
export class BreedingMethodEditDialogComponent implements OnInit, OnDestroy {

    breedingMethodId: number;

    isLoading: boolean;

    breedingMethodRequest: any;


    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private breedingMethodService: BreedingMethodService,
                private alertService: AlertService,
                private cropSettingsContext: CropSettingsContext) {

        this.breedingMethodRequest = {};
    }
    ngOnDestroy(): void {
    }

    ngOnInit(): void {

        if (this.cropSettingsContext.breedingMethod) {
            this.breedingMethodId = this.cropSettingsContext.breedingMethod.mid;
            this.breedingMethodRequest.name = this.cropSettingsContext.breedingMethod.name;
            this.breedingMethodRequest.code = this.cropSettingsContext.breedingMethod.code;
            this.breedingMethodRequest.description = this.cropSettingsContext.breedingMethod.description;

        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save(){

    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.breedingMethodRequest.name && this.breedingMethodRequest.type
            && this.breedingMethodRequest.abbreviation;
    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'breedingMethodViewChanged' });
        this.clear();
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
