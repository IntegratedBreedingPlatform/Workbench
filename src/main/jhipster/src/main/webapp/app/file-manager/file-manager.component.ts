import { Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ParamContext } from '../shared/service/param.context';
import { FileService } from '../shared/file/service/file.service';
import { FileMetadata } from '../shared/file/model/file-metadata';
import { readAsDataURL, saveFile } from '../shared/util/file-utils';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../shared/alert/format-error-list';
import { AlertService } from '../shared/alert/alert.service';
import { finalize } from 'rxjs/operators';
import { ModalConfirmComponent } from '../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { FILE_UPLOAD_SUPPORTED_TYPES } from '../app.constants';
import { VariableDetails } from '../shared/ontology/model/variable-details';
import { FilterType } from '../shared/column-filter/column-filter.component';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html',
    styleUrls: ['./file-manager.scss']
})
export class FileManagerComponent {

    @ViewChild('fileUpload')
    fileUpload: ElementRef;
    file: File;
    variable: VariableDetails;

    fileMetadataSelected: FileMetadata;
    fileMetadataList: FileMetadata[];
    imgToUploadUrlPreview;

    observationUnitUUID: string;

    isLoading = false;
    isLoadingImage = false;
    acceptedFileTypes = (FILE_UPLOAD_SUPPORTED_TYPES || '').split(',').map((t) => '.' + t).join(',');

    filters = {
        variable: {
            key: 'variable',
            type: FilterType.TEXT,
            value: ''
        }
    }

    constructor(
        private route: ActivatedRoute,
        private activeModal: NgbActiveModal,
        public context: ParamContext,
        private fileService: FileService,
        private alertService: AlertService,
        private modalService: NgbModal,
        private translateService: TranslateService
    ) {
        this.context.readParams();
        const queryParamMap = this.route.snapshot.queryParamMap;
        this.observationUnitUUID = queryParamMap.get('observationUnitUUID');
        this.load();
    }

    private load() {
        this.isLoading = true;
        this.fileService.listFileMetadata(this.observationUnitUUID, this.filters.variable.value)
            .pipe(finalize(() => this.isLoading = false))
            .subscribe((fileMetadataList) => {
                this.fileMetadataSelected = null;
                this.imgToUploadUrlPreview = null;
                this.fileMetadataList = fileMetadataList;
            }, (error) => this.onError(error));
    }

    applyFilters() {
        this.load();
    }

    resetFilters() {
        this.load();
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

    async delete($event, fileMetadata: FileMetadata) {
        $event.stopPropagation();
        const confirmModal = this.modalService.open(ModalConfirmComponent);
        confirmModal.componentInstance.message = this.translateService.instant('fileManager.delete.confirm', {fileName: fileMetadata.name});
        try {
            await confirmModal.result;
        } catch (e) {
            return;
        }
        this.isLoading = true;
        this.fileService.delete(fileMetadata.fileUUID)
            .pipe(finalize(() => this.isLoading = false))
            .subscribe(() => {
                this.alertService.success('fileManager.delete.success');
                this.load();
                if (window.parent) {
                    window.parent.postMessage('observations-changed', '*');
                }
            }, (error) => this.onError(error))
        return false;
    }

    download($event, fileMetadata: FileMetadata) {
        $event.stopPropagation();
        this.isLoading = true;
        this.fileService.downloadFile(fileMetadata.path).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe(
            (file: HttpResponse<Blob>) => saveFile(file, fileMetadata.name)
        );
    }

    async onFileChange(evt: any) {
        this.fileMetadataSelected = null;
        this.imgToUploadUrlPreview = null;
        this.file = evt.target.files[0];
        this.fileUpload.nativeElement.innerText = this.file.name;
        if (this.isImage(this.file.name)) {
            this.imgToUploadUrlPreview = await readAsDataURL(this.file);
        }
    }

    upload() {
        this.isLoading = true;
        // upload file / save observation
        this.fileService.upload(
            this.file,
            this.observationUnitUUID,
            this.variable && this.variable.id || null
        ).pipe(
            finalize(() => this.isLoading = false)
        ).subscribe(
            (fileMetadata) => {
                this.load();
                this.alertService.success('fileManager.upload.success');
                if (window.parent) {
                    window.parent.postMessage('observations-changed', '*');
                }
            },
            (error) => this.onError(error)
        );
    }

    select(fileMetadata: FileMetadata) {
        this.imgToUploadUrlPreview = null;
        this.isLoadingImage = true;
        this.fileMetadataSelected = fileMetadata;
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
