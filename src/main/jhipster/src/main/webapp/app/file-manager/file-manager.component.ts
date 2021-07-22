import { Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Route } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../shared/service/param.context';
import { FileService } from '../shared/file/service/file.service';
import { FileMetadata } from '../shared/file/model/file-metadata';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html'
})
export class FileManagerComponent {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;

    fileMetadata: FileMetadata
    fileName: string;

    constructor(
        private route: ActivatedRoute,
        private activeModal: NgbActiveModal,
        public context: ParamContext,
        private fileService: FileService
    ) {
        this.context.readParams();
        const routeParams = this.route.snapshot.paramMap;
        const queryParamMap = this.route.snapshot.queryParamMap;
        this.fileService.listFileMetadata(queryParamMap.get('observationId'))
            .subscribe((fileMetadataList) => {
                if (fileMetadataList && fileMetadataList.length) {
                    this.fileMetadata = fileMetadataList[0];
                }
            });
    }

    cancel() {
        this.activeModal.dismiss();
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

    getFileKeyEncoded(path) {
        return encodeURIComponent(path);
    }

    delete() {
        // this.fileService.delete(fileMetadata);
    }

    download() {

    }

    onFileChange(evt: any) {
        const target = evt.target;
        this.fileName = target.files[0].name;
        this.fileUpload.nativeElement.innerText = target.files[0].name;
    }
}
