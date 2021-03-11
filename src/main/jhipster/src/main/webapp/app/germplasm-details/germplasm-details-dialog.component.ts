import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PopupService } from '../shared/modal/popup.service';
import { ParamContext } from '../shared/service/param.context';
import { GermplasmDetailsContext } from './germplasm-details.context';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-details-dialog',
    templateUrl: './germplasm-details-dialog.component.html'
})
export class GermplasmDetailsDialogComponent implements OnInit {

    constructor(private route: ActivatedRoute, private router: Router,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.router.navigate(['/germplasm-basic-details']);
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

}

@Component({
    selector: 'jhi-germplasm-details-popup',
    template: ``
})
export class GermplasmDetailsPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService,
                private paramContext: ParamContext,
                private germplasmDetailsContext: GermplasmDetailsContext) {
    }

    ngOnInit(): void {
        this.paramContext.readParams();
        const gid = this.route.snapshot.paramMap.get('gid');
        this.germplasmDetailsContext.gid = gid;
        const modal = this.popupService.open(GermplasmDetailsDialogComponent as Component, { windowClass: 'modal-extra-large', backdrop: 'static' });
    }

}
