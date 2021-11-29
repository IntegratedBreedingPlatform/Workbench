import { TranslateService } from '@ngx-translate/core';
import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
    selector: 'jhi-modal-confirm',
    template: `
		<jhi-modal [title]="title">
			<div class="modal-body word-wrap" [innerHTML]="sanitizer.bypassSecurityTrustHtml(message)">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal" (click)="dismiss()">
					<span class="fa fa-ban"></span>&nbsp;<span>{{cancelLabel}}</span>
				</button>
				<button (click)="confirm()" class="btn btn-primary">
					<span class="fa fa-save"></span>&nbsp;<span>{{confirmLabel}}</span>
				</button>
			</div>
		</jhi-modal>
    `
})
export class ModalConfirmComponent {
    @Input() message: string;
    @Input() title: string;
    @Input() cancelLabel = this.translateService.instant('cancel');
    @Input() confirmLabel = this.translateService.instant('confirm');

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
