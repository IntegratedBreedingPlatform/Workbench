import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';
import { PopupService } from '../../shared/modal/popup.service';
import { ParamContext } from '../../shared/service/param.context';
import { BREEDING_METHODS_BROWSER_DEFAULT_URL } from '../../app.constants';

@Component({
    selector: 'jhi-breeding-method-manager',
    template: `
		<div class="modal-header">
			<h4 class="modal-title font-weight-bold">
				<span jhiTranslate="breeding-method.browser.title"></span>
			</h4>
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true"
					(click)="close()">&times;
			</button>
		</div>
		<div class="modal-body">
			<iframe [src]="safeUrl" style="border: 0" width="100%" height="650"></iframe>
		</div>
    `
})
export class BreedingMethodManagerComponent implements OnInit {
    safeUrl: string;

    constructor(private route: ActivatedRoute,
                public activeModal: NgbActiveModal,
                private sanitizer: DomSanitizer) {
    }

    ngOnInit(): void {
    }

    close() {
        this.activeModal.close();
    }
}

@Component({
    selector: 'jhi-breeding-method-manager-popup',
    template: ``
})
export class BreedingMethodManagerPopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService,
                private sanitizer: DomSanitizer,
                private paramContext: ParamContext) {
    }

    ngOnInit(): void {
        const params = '?programId=' + this.paramContext.selectedProjectId;

        const modal = this.popupService.open(BreedingMethodManagerComponent as Component, { windowClass: 'modal-autofit' });
        modal.then((modalRef) => {
            modalRef.componentInstance.safeUrl =
                this.sanitizer.bypassSecurityTrustResourceUrl(BREEDING_METHODS_BROWSER_DEFAULT_URL + params);
        });
    }

}
