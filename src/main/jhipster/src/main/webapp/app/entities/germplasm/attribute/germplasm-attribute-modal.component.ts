import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal, NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { LocationModel } from '../../../shared/location/model/location.model';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { DateHelperService } from '../../../shared/service/date.helper.service';
import { GermplasmAttributeContext } from './germplasm-attribute.context';
import { Attribute } from '../../../shared/attributes/model/attribute.model';

@Component({
    selector: 'jhi-germplasm-attribute-modal',
    templateUrl: './germplasm-attribute-modal.component.html',
    styleUrls: ['./germplasm-attribute-modal.component.css']
})
export class GermplasmAttributeModalComponent implements OnInit, OnDestroy {

    isEditMode: boolean;
    gid: number;
    attributeCodes: Promise<Attribute[]>;
    locations: LocationModel[];
    isLoading: boolean;

    attributeId: number;
    attributeCode: string;
    value: string;
    locationId: number;
    date: NgbDate;

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private germplasmAttributeContext: GermplasmAttributeContext,
                private germplasmService: GermplasmService,
                private calendar: NgbCalendar,
                private dateHelperService: DateHelperService,
                private alertService: JhiAlertService) {
    }

    ngOnInit(): void {
        this.attributeCodes = this.germplasmService.getGermplasmAttributesByType(this.germplasmAttributeContext.attributeType).toPromise();
        this.date = this.calendar.getToday();
        if (this.germplasmAttributeContext.attribute) {
            this.attributeId = this.germplasmAttributeContext.attribute.id;
            this.attributeCode = this.germplasmAttributeContext.attribute.attributeCode;
            this.value = this.germplasmAttributeContext.attribute.value;
            this.locationId = Number(this.germplasmAttributeContext.attribute.locationId);
            this.date = this.dateHelperService.convertStringToNgbDate(this.germplasmAttributeContext.attribute.date);
        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {

        if (this.attributeId) {
            // if attribute id is available, we have to update the attribute
            this.isLoading = true;
            this.germplasmService.updateGermplasmAttribute(this.gid, this.attributeId, {
                attributeCode: this.attributeCode,
                attributeType: this.germplasmAttributeContext.attributeType,
                value: this.value,
                locationId: this.locationId,
                date: this.dateHelperService.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.alertService.success('germplasm-attribute-modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        } else {
            // If attribute id is not available, we have to create a new attribute
            this.isLoading = true;
            this.germplasmService.createGermplasmAttribute(this.gid, {
                attributeCode: this.attributeCode,
                attributeType: this.germplasmAttributeContext.attributeType,
                value: this.value,
                locationId: this.locationId,
                date: this.dateHelperService.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.alertService.success('germplasm-attribute-modal.add.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        }

    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'attributeChanged' });
        this.clear();
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.attributeCode
            && this.value && this.locationId && this.date;
    }

    ngOnDestroy(): void {
        this.germplasmAttributeContext.attributeType = null;
        this.germplasmAttributeContext.attribute = null;
    }
}

@Component({
    selector: 'jhi-germplasm-attribute-popup',
    template: ``
})
export class GermplasmAttributePopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmAttributeModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
        });

    }

}
