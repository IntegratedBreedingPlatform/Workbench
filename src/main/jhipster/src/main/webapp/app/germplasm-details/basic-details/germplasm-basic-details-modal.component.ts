import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { JhiLanguageService } from 'ng-jhipster';
import { DateHelperService } from '../../shared/service/date.helper.service';

@Component({
    selector: 'jhi-edit-basic-details-pane',
    templateUrl: './germplasm-basic-details-modal.component.html'
})

export class GermplasmBasicDetailsModalComponent implements OnInit {

    germplasm: GermplasmDto = new GermplasmDto();
    germplasmDate: NgbDate;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public activeModal: NgbActiveModal,
                public germplasmService: GermplasmService,
                public dateHelperService: DateHelperService) {
    }

    ngOnInit(): void {
        this.germplasm = this.germplasmDetailsContext.germplasm;
        this.germplasmDate = this.dateHelperService.convertStringToNgbDate(this.germplasm.creationDate);
    }

    updateGermplasmBasicDetails(): void{
        this.germplasm.creationDate = this.dateHelperService.convertNgbDateToString(this.germplasmDate);
        this.germplasmService.updateGermplasmBasicDetails(this.germplasm).toPromise().then(() => {
            this.clear();
        })
    }

    clear() {
        this.activeModal.dismiss('cancel');
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
