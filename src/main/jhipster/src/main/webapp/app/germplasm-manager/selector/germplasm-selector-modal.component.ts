import { Component, HostListener, OnInit } from '@angular/core';
import { PopupService } from '../../shared/modal/popup.service';
import { SafeResourceUrl } from '@angular/platform-browser/src/security/dom_sanitization_service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GERMPLASM_SEARCH_SELECTOR } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { JhiEventManager } from 'ng-jhipster';

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
			<iframe [src]="safeUrl" style="border: 0" width="100%" height="720px"></iframe>
		</div>
    `
})
export class GermplasmSelectorModalComponent implements OnInit {

    selectMultiple: boolean;
    selectorValue;
    safeUrl: SafeResourceUrl;

    constructor(public activeModal: NgbActiveModal,
                private paramContext: ParamContext,
                private sanitizer: DomSanitizer,
                private eventManager: JhiEventManager) {
    }

    @HostListener('window:message', ['$event'])
    onMessage(event) {
        if (event.data.name === 'selector-changed') {
            this.eventManager.broadcast({ name: 'germplasmSelectorSelected', content: event.data.value.join(',') });
            this.clear();
        } else if (event.data.name === 'cancel') {
            this.clear();
        }
    }

    ngOnInit(): void {
        const queryParams = '?cropName=' + this.paramContext.cropName
            + '&programUUID=' + this.paramContext.programUUID
            + '&authToken=' + this.paramContext.authToken
            + '&selectedProjectId=' + this.paramContext.selectedProjectId
            + '&loggedInUserId=' + this.paramContext.loggedInUserId
            + '&selectMultiple=' + this.selectMultiple; // Tell the page that it is shown as a modal.

        this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(GERMPLASM_SEARCH_SELECTOR + queryParams);
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

    selectMultiple: boolean;

    constructor(private popupService: PopupService, private activatedRoute: ActivatedRoute, ) {
        const queryParams = this.activatedRoute.snapshot.queryParams;
        this.selectMultiple = queryParams.selectMultiple === 'true';
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmSelectorModalComponent as Component, { windowClass: 'modal-large', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.selectMultiple = this.selectMultiple;
        });
    }

}
