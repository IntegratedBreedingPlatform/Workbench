import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { LocationModel } from '../../shared/location/model/location.model';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-edit-basic-details-pane',
    templateUrl: './edit-basic-details-pane.component.html'
})

export class EditBasicDetailsPaneComponent implements OnInit {

    germplasm: GermplasmDto = new GermplasmDto();
    locations: LocationModel[];

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public activeModal: NgbActiveModal,
                public germplasmService: GermplasmService) {
    }

    ngOnInit(): void {
        this.germplasm = this.germplasmDetailsContext.germplasm;
    }

    updateGermplasmBasicDetails(): void{
        this.germplasmService.updateGermplasmBasicDetails(this.germplasm).toPromise().then(() => {
            this. activeModal.close();
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
export class EditBasicDetailsPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(EditBasicDetailsPaneComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
    }

}
