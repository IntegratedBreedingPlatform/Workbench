import { TranslateService } from '@ngx-translate/core';
import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
    selector: 'jhi-modal-no-entry-values',
    template: `
		<jhi-modal [title]="title">
			<div class="modal-body word-wrap" [innerHTML]="sanitizer.bypassSecurityTrustHtml(message)">
			</div>
			<div class="modal-footer">
				<button *ngIf="this.showCancelButton" type="button" class="btn btn-secondary" data-dismiss="modal" (click)="dismiss()" data-test="modalCancelButton">
					<span>{{cancelLabel}}</span>
				</button>
				<button (click)="confirm()" class="btn btn-primary" data-test="modalConfirmButton">
					<span>{{confirmLabel}}</span>
				</button>
			</div>
		</jhi-modal>
    `
})
export class ModalAlertComponent {
    @Input() message: string;
    @Input() title: string;
    @Input() cancelLabel = this.translateService.instant('cancel');
    @Input() confirmLabel = this.translateService.instant('ok');
    @Input() showCancelButton = true;

    constructor(private modal: NgbActiveModal,
                private translateService: TranslateService,
                public sanitizer: DomSanitizer) {
    }

    confirm() {
        this.modal.close();
    }

    dismiss() {
        this.modal.dismiss();
    }

}
