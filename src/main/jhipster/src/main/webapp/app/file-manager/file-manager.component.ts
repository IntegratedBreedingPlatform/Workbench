import { Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Route } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../shared/service/param.context';
import { FileService } from '../shared/file/service/file.service';
import { FileMetadata } from '../shared/file/model/file-metadata';
import { readAsDataURL } from '../shared/util/file-utils';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { finalize } from 'rxjs/operators';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html',
    styleUrls: ['./file-manager.scss']
})
export class FileManagerComponent {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;
    file: File;

    fileMetadata: FileMetadata
    imgToUploadUrlPreview;

    datasetId: number;
    observationUnitUUID: string;
    observationId: number;
    termId: number;

    isLoading = false;
    isSaving = false;
    // TODO
    acceptedFileTypes = [];

    constructor(
        private route: ActivatedRoute,
        private activeModal: NgbActiveModal,
        public context: ParamContext,
        private fileService: FileService,
        private alertService: AlertService
    ) {
        this.context.readParams();
        const queryParamMap = this.route.snapshot.queryParamMap;
        this.datasetId = Number(queryParamMap.get('datasetId'));
        this.observationUnitUUID = queryParamMap.get('observationUnitUUID');
        this.observationId = Number(queryParamMap.get('observationId'));
        this.termId = Number(queryParamMap.get('termId'));

        if (this.observationId) {
            this.isLoading = true;
            this.fileService.listFileMetadata(this.observationId)
                .pipe(finalize(() => this.isLoading = false))
                .subscribe((fileMetadataList) => {
                    if (fileMetadataList && fileMetadataList.length) {
                        this.fileMetadata = fileMetadataList[0];
                    }
                }, (error) => this.onError(error));
        }
    }

    isImage(fileName) {
        return fileName.match(/\.(gif|jpe?g|tiff?|png|webp|bmp)$/i)
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

    async onFileChange(evt: any) {
        this.file = evt.target.files[0];
        this.fileUpload.nativeElement.innerText = this.file.name;
        if (this.isImage(this.file.name)) {
            this.imgToUploadUrlPreview = await readAsDataURL(this.file);
        }
    }

    upload() {
        this.isSaving = true;
        // upload file / save observation
        this.fileService.upload(
            this.file,
            this.observationUnitUUID,
            this.termId
        ).pipe(
            finalize(() => this.isSaving = false)
        ).subscribe(
            (fileMetadata) => {
                this.fileMetadata = fileMetadata;
                this.onSuccess()
            },
            (error) => this.onError(error)
        );
    }

    private onSuccess() {
        this.alertService.success('fileManager.upload.success');
        if (window.parent) {
            window.parent.postMessage('observations-changed', '*');
        }
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general', null, null);
        }
    }
}
