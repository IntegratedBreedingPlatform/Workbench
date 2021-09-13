import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-modal-confirm',
    template: `
		<jhi-modal>
			<div class="modal-body">
                <span jhiTranslate="fileManager.delete.variable.files.message" [translateValues]="{fileCount: fileCount}"></span>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal" (click)="detachFiles()">
					<span jhiTranslate="fileManager.delete.variable.files.keep"></span>
				</button>
				<button (click)="removeFiles()" class="btn btn-primary">
					<span jhiTranslate="fileManager.delete.variable.files.remove"></span>
				</button>
			</div>
		</jhi-modal>
    `
})
export class FileDeleteOptionsComponent {
    @Input() fileCount;

    constructor(private modal: NgbActiveModal) {
    }

    removeFiles() {
        this.modal.close(true);
    }

    detachFiles() {
        this.modal.close(false);
    }

    dismiss() {
        this.modal.dismiss();
    }
}
