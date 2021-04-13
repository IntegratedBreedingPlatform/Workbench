import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { PopupService } from '../../shared/modal/popup.service';
import { JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-edit-basic-details-pane',
    templateUrl: './edit-basic-details-pane.component.html'
})

export class EditBasicDetailsPaneComponent implements OnInit {

    @Input() public germplasm: GermplasmDto;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                public activeModal: NgbActiveModal,) {
    }

    ngOnInit(): void {
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
        const germplasm = this.route.snapshot.paramMap.get('germplasm');

        const modal = this.popupService.open(EditBasicDetailsPaneComponent as Component);
        modal.then((modalRef) => {
            modalRef.componentInstance.germplasm = germplasm;
        });
    }

}
