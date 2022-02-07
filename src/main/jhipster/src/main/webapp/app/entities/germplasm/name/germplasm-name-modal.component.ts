import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal, NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { NameType } from '../../../shared/germplasm/model/name-type.model';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { GermplasmNameContext } from './germplasm-name.context';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { DateHelperService } from '../../../shared/service/date.helper.service';

@Component({
    selector: 'jhi-germplasm-name-modal',
    templateUrl: './germplasm-name-modal.component.html',
    styleUrls: ['./germplasm-name-modal.component.css']
})
export class GermplasmNameModalComponent implements OnInit, OnDestroy {

    gid: number;
    nameTypes: Promise<NameType[]>;
    isLoading: boolean;

    nameId: number;
    name: string;
    nameTypeCode: string;
    locationId: number;
    date: NgbDate;
    preferred: boolean;

    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private germplasmNameContext: GermplasmNameContext,
                private germplasmService: GermplasmService,
                private calendar: NgbCalendar,
                private dateHelperService: DateHelperService,
                private alertService: JhiAlertService) {
    }

    ngOnInit(): void {
        this.nameTypes = this.germplasmService.getGermplasmNameTypes([]).toPromise();
        this.date = this.calendar.getToday();
        if (this.germplasmNameContext.germplasmName) {
            this.nameId = this.germplasmNameContext.germplasmName.id;
            this.name = this.germplasmNameContext.germplasmName.name;
            this.nameTypeCode = this.germplasmNameContext.germplasmName.nameTypeCode;
            this.locationId = this.germplasmNameContext.germplasmName.locationId;
            this.date = this.dateHelperService.convertStringToNgbDate(this.germplasmNameContext.germplasmName.date);
            this.preferred = this.germplasmNameContext.germplasmName.preferred;
        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {

        if (this.nameId) {
            // if name id is available, we have to update the name
            this.isLoading = true;
            this.germplasmService.updateGermplasmName(this.gid, this.nameId, {
                name: this.name,
                nameTypeCode: this.nameTypeCode,
                preferredName: this.preferred || false,
                locationId: this.locationId,
                date: this.dateHelperService.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.alertService.success('germplasm-name-modal.edit.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        } else {
            // If name id is not available, we have to create a new name
            this.isLoading = true;
            this.germplasmService.createGermplasmName(this.gid, {
                name: this.name,
                nameTypeCode: this.nameTypeCode,
                preferredName: this.preferred || false,
                locationId: this.locationId,
                date: this.dateHelperService.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.alertService.success('germplasm-name-modal.add.success');
                this.notifyChanges();
                this.isLoading = false;
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
                this.isLoading = false;
            });
        }

    }

    notifyChanges(): void {
        this.eventManager.broadcast({ name: 'basicDetailsChanged' });
        this.clear();
    }

    isFormValid(f) {
        return f.form.valid && !this.isLoading && this.name
            && this.nameTypeCode && this.locationId && this.date;
    }

    ngOnDestroy(): void {
        this.germplasmNameContext.germplasmName = null;
    }
}

@Component({
    selector: 'jhi-germplasm-name-popup',
    template: ``
})
export class GermplasmNamePopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmNameModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
        });

    }

}
