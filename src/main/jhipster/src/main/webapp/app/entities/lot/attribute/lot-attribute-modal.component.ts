import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal, NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { DateHelperService } from '../../../shared/service/date.helper.service';
import { LotAttributeContext } from './lot-attribute.context';
import { VariableTypeEnum } from '../../../shared/ontology/variable-type.enum';
import { VariableDetails } from '../../../shared/ontology/model/variable-details';
import { VariableService } from '../../../shared/ontology/service/variable.service';
import { DataTypeEnum } from '../../../shared/ontology/data-type.enum';
import { VariableValidationService } from '../../../shared/ontology/service/variable-validation.service';
import { TranslateService } from '@ngx-translate/core';
import { LotService } from '../../../shared/inventory/service/lot.service';
import { PopupService } from '../../../shared/modal/popup.service';

@Component({
    selector: 'jhi-lot-attribute-modal',
    templateUrl: './lot-attribute-modal.component.html',
    styleUrls: ['./lot-attribute-modal.component.css']
})
export class LotAttributeModalComponent implements OnInit, OnDestroy {

    isEditMode: boolean;
    lotId: number;
    isLoading: boolean;
    VariableType = VariableTypeEnum;
    DataType = DataTypeEnum;

    attributeId: number;
    attributeTypeId: number;
    value: string;
    dateValue: NgbDate;
    locationId: number;
    date: NgbDate;
    variable: VariableDetails;

    constructor(public activeModal: NgbActiveModal,
                public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private lotAttributeContext: LotAttributeContext,
                private lotService: LotService,
                private variableService: VariableService,
                private validationService: VariableValidationService,
                private calendar: NgbCalendar,
                public dateHelperService: DateHelperService,
                private alertService: JhiAlertService,
                private route: ActivatedRoute) {
    }

    ngOnInit() {
        (<any>window).onCloseModal = this.dismiss;

        this.lotAttributeContext.attributeType = VariableTypeEnum.INVENTORY_ATTRIBUTE;
        this.date = this.calendar.getToday();
        if (this.lotAttributeContext.attribute) {
            this.attributeId = this.lotAttributeContext.attribute.id;
            this.value = this.lotAttributeContext.attribute.value;
            this.dateValue = this.dateHelperService.convertStringToNgbDate(this.value);
            this.locationId = Number(this.lotAttributeContext.attribute.locationId);
            this.date = this.dateHelperService.convertStringToNgbDate(this.lotAttributeContext.attribute.date);
            this.variable = this.lotAttributeContext.variable;
            this.lotAttributeContext.attributeType = VariableTypeEnum.INVENTORY_ATTRIBUTE;
        }
        this.attributeTypeId = this.lotAttributeContext.attributeType;
    }

    clear() {
        this.dismiss();
    }

    save() {
        if (this.attributeId) {
            // if attribute id is available, we have to update the attribute
            this.isLoading = true;
            this.lotService.updateLotAttribute(this.lotId, this.attributeId, {
                variableId: Number(this.variable.id),
                value: this.value,
                locationId: this.locationId,
                date: this.dateHelperService.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.alertService.success('lot-attribute-modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        } else {
            // If attribute id is not available, we have to create a new attribute
            this.isLoading = true;
            this.lotService.createLotAttribute(this.lotId, {
                variableId: Number(this.variable.id),
                value: this.value,
                locationId: this.locationId,
                date: this.dateHelperService.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.alertService.success('lot-attribute-modal.add.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        }

    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'attributeChanged', content: '' });
        this.clear();
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.variable
            && this.value && this.locationId != null && this.date;
    }

    isNumericOutOfRange() {
        return !this.validationService.isValidValue(this.value, this.variable).isInRange;
    }

    getNumericOutOfRangeWarning() {
        const isOutOfrange = !this.validationService.isValidValue(this.value, this.variable).isInRange;
        if (isOutOfrange) {
            const min = this.variable.scale.validValues && (
                this.variable.scale.validValues.min || this.variable.scale.validValues.min === 0)
                ? this.variable.scale.validValues.min
                : this.variable.expectedRange.min;
            const max = this.variable.scale.validValues && (
                this.variable.scale.validValues.max || this.variable.scale.validValues.max === 0)
                ? this.variable.scale.validValues.max
                : this.variable.expectedRange.max;
            const title = this.translateService.instant('lot-attribute-modal.outOfRange', { min, max });
            return title;
        } else {
            return '';
        }
    }

    ngOnDestroy(): void {
        this.lotAttributeContext.variable = null;
        this.lotAttributeContext.attributeType = null;
        this.lotAttributeContext.attribute = null;
    }

    selectVariable(variable: VariableDetails) {
        this.value = null;
        this.variable = variable;
    }

    dismiss() {
        this.activeModal.dismiss();
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }
}

@Component({
    selector: 'jhi-lot-attribute-popup',
    template: ``
})
export class LotAttributePopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(LotAttributeModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.lotId = Number(this.route.snapshot.paramMap.get('lotId'));
        });

    }

}
