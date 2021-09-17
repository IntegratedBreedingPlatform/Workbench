import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../shared/modal/popup.service';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmProgeny } from '../../shared/germplasm/model/germplasm-progeny.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';

@Component({
    selector: 'jhi-germplasm-progey-dialog',
    templateUrl: './germplasm-progeny-modal.component.html',
})
export class GermplasmProgenyModalComponent implements OnInit {

    gid;
    germplasm: GermplasmDto;
    germplasmProgenies: GermplasmProgeny[] = [];

    constructor(private germplasmService: GermplasmService,
                private modal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.germplasmService.getGermplasmById(this.gid).toPromise().then((response) => {
            this.germplasm = response.body;
        })
        this.germplasmService.getProgenies(this.gid).toPromise().then((germplasmProgenies) => {
            this.germplasmProgenies = germplasmProgenies;
        });
    }

    dismiss() {
        this.modal.dismiss();
    }

}

@Component({
    selector: 'jhi-germplasm-progeny-popup',
    template: ``
})
export class GermplasmProgenyPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const gid = this.route.snapshot.queryParams.gid;
        const modal = this.popupService.open(GermplasmProgenyModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = gid;
        });
    }

}
