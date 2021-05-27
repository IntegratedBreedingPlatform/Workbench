import { Component, OnInit } from '@angular/core';
import { GermplasmDto } from '../shared/germplasm/model/germplasm.model';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmPedigreeService } from '../shared/germplasm/service/germplasm.pedigree.service';
import { graphviz } from 'd3-graphviz';

@Component({
    selector: 'jhi-germplasm-details-graphviz-modal',
    templateUrl: './germplasm-details-graphviz-modal.component.html'
})
export class GermplasmDetailsGraphvizModalComponent implements OnInit {

    gid: number;
    includeDerivativeLines: boolean;

    constructor(public activeModal: NgbActiveModal,
                private germplasmPedigreeService: GermplasmPedigreeService) {

    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    ngOnInit(): void {
    }

}

@Component({
    selector: 'jhi-germplasm-details-graphviz-popup-modal',
    template: ``
})
export class GermplasmDetailsGraphvizModalPopupComponent implements OnInit {

    germplasm: GermplasmDto;

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmDetailsGraphvizModalComponent as Component, { windowClass: 'modal-fillview', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
        });
    }

}
