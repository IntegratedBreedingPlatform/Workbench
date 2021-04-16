import { Component, OnInit } from '@angular/core';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { PopupService } from '../../shared/modal/popup.service';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-germplasm-selector-modal',
    template: `
		<div class="container">
			<div class="modal-header">
				<div class="col-xs-11 col-md-11">
				</div>
				<div class="col-xs-1 col-md-1">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true"
							(click)="clear()">&times;
					</button>
				</div>
			</div>
			<div class="modal-body">
				<jhi-germplasm-selector></jhi-germplasm-selector>
			</div>
		</div>
    `
})
export class GermplasmSelectorModalComponent implements OnInit {

    germplasm: GermplasmDto
    safeUrl: SafeResourceUrl;
    gid: number;

    constructor(public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

}

@Component({
    selector: 'jhi-germplasm-selector-popup',
    template: ``
})
export class GermplasmSelectorPopupComponent implements OnInit {

    constructor(private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmSelectorModalComponent as Component, { windowClass: 'modal-large', backdrop: 'static' });
    }

}
