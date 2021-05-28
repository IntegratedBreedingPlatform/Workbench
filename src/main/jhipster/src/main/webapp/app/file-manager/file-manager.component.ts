import { Component } from '@angular/core';
import { ActivatedRoute, Route } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html'
})
export class FileManagerComponent {
    fileKey: string;
    fileName: string;

    constructor(
        private route: ActivatedRoute,
        private activeModal: NgbActiveModal,
    ) {
        const routeParams = this.route.snapshot.paramMap;
        const queryParamMap = this.route.snapshot.queryParamMap;
        this.fileKey = routeParams.get('fileKey');
        this.fileName = queryParamMap.get('fileName');
    }

    cancel() {
        this.activeModal.dismiss();
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

    getFileKeyEncoded() {
        return encodeURIComponent(this.fileKey);
    }
}
