import { TranslateService } from '@ngx-translate/core';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
    selector: 'jhi-save-template',
    template: `
		<jhi-modal [title]="title">
			<div class="modal-body">
                <div class="row form-group">
                    <label class="col-md-2 col-form-label font-weight-bold">Name: </label>
                    <input type="text" class="form-control col-md-10" placeholder="Setting Name" [(ngModel)]="templateName" maxlength="255"/>
                </div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal" (click)="dismiss()" data-test="modalCancelButton">
					<span class="fa fa-ban"></span>&nbsp;<span>Cancel</span>
				</button>
				<button [disabled]="!templateName || templateName.trim().length === 0" (click)="save()" class="btn btn-primary" data-test="modalConfirmButton">
					<span class="fa fa-save"></span>&nbsp;<span>Save</span>
				</button>
			</div>
		</jhi-modal>
    `
})
export class SaveTemplateComponent {
    title = this.translateService.instant('advance-study.attributes.preset.new.settings');
    @Input() templateName: string;
    @Output() templateNameEmitter = new EventEmitter<string>();

    constructor(private modal: NgbActiveModal,
                private translateService: TranslateService,
                public sanitizer: DomSanitizer) {
    }

    save() {
        this.templateNameEmitter.emit(this.templateName);
        this.modal.close(this.templateName);
    }

    dismiss() {
        this.modal.dismiss();
    }
}
