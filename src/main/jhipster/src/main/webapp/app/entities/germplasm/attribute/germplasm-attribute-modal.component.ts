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
import { VariableTypeEnum } from '../../../shared/ontology/variable-type.enum';
import { VariableDetails } from '../../../shared/ontology/model/variable-details';
import { VariableService } from '../../../shared/ontology/service/variable.service';
import { DataTypeEnum } from '../../../shared/ontology/data-type.enum';
import { VariableValidationService } from '../../../shared/ontology/service/variable-validation.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jhi-germplasm-attribute-modal',
    templateUrl: './germplasm-attribute-modal.component.html',
    styleUrls: ['./germplasm-attribute-modal.component.css']
})
export class GermplasmAttributeModalComponent implements OnInit, OnDestroy {

    isEditMode: boolean;
    gid: number;
    locations: LocationModel[];
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
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private germplasmAttributeContext: GermplasmAttributeContext,
                private germplasmService: GermplasmService,
                private variableService: VariableService,
                private validationService: VariableValidationService,
                private calendar: NgbCalendar,
                public dateHelperService: DateHelperService,
                private alertService: JhiAlertService) {
    }

    ngOnInit() {
        this.date = this.calendar.getToday();
        if (this.germplasmAttributeContext.attribute) {
            this.attributeId = this.germplasmAttributeContext.attribute.id;
            this.value = this.germplasmAttributeContext.attribute.value;
            this.dateValue = this.dateHelperService.convertStringToNgbDate(this.value);
            this.locationId = Number(this.germplasmAttributeContext.attribute.locationId);
            this.date = this.dateHelperService.convertStringToNgbDate(this.germplasmAttributeContext.attribute.date);
            this.variable = this.germplasmAttributeContext.variable;
        }
        this.attributeTypeId = this.germplasmAttributeContext.attributeType;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {

        if (this.attributeId) {
            // if attribute id is available, we have to update the attribute
            this.isLoading = true;
            this.germplasmService.updateGermplasmAttribute(this.gid, this.attributeId, {
                variableId: Number(this.variable.id),
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
                variableId: Number(this.variable.id),
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
        return f.form.valid && !this.isLoading && this.variable
            && this.value && this.locationId && this.date;
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
            const title = this.translateService.instant('germplasm-attribute-modal.outOfRange', { min, max });
            return title;
        } else {
            return '';
        }
    }

    ngOnDestroy(): void {
        this.germplasmAttributeContext.variable = null;
        this.germplasmAttributeContext.attributeType = null;
        this.germplasmAttributeContext.attribute = null;
    }

    selectVariable(variable: VariableDetails) {
        this.value = null;
        this.variable = variable;
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
