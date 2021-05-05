import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { GermplasmDto } from '../../../shared/germplasm/model/germplasm.model';
import { PopupService } from '../../../shared/modal/popup.service';
import { GermplasmDetailsContext } from '../../../germplasm-details/germplasm-details.context';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { DateHelperService } from '../../../shared/service/date.helper.service';

@Component({
    selector: 'jhi-edit-basic-details-pane',
    templateUrl: './germplasm-basic-details-modal.component.html',
    styleUrls: ['./germplasm-basic-details.modal.component.css']
})

export class GermplasmBasicDetailsModalComponent implements OnInit {

    isLoading: boolean;
    germplasm: GermplasmDto = new GermplasmDto();
    germplasmDate: NgbDate;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public activeModal: NgbActiveModal,
                public germplasmService: GermplasmService,
                public dateHelperService: DateHelperService,
                private alertService: JhiAlertService) {
    }

    ngOnInit(): void {
        this.germplasm = this.germplasmDetailsContext.germplasm;
        this.germplasmDate = this.dateHelperService.convertStringToNgbDate(this.germplasm.creationDate);
    }

    updateGermplasmBasicDetails(): void {
        this.isLoading = true;
        this.germplasm.creationDate = this.dateHelperService.convertNgbDateToString(this.germplasmDate);
        this.germplasmService.updateGermplasmBasicDetails(this.germplasm).toPromise().then((result) => {
            this.alertService.success('edit-basic-details.success');
            this.eventManager.broadcast({ name: 'basicDetailsChanged' });
            this.clear();
            this.isLoading = false;
        }).catch((response) => {
            this.alertService.error('error.custom', { param: response.error.errors[0].message });
            this.isLoading = false;
        });
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    isFormValid(f) {
        return f.form.valid && this.germplasmDetailsContext && this.germplasmDate && !this.isLoading && this.germplasm.breedingLocationId;
    }

}

@Component({
    selector: 'jhi-edit-basic-details-popup',
    template: ``
})
export class EditGermplasmBasicDetailsPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmBasicDetailsModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }

}
