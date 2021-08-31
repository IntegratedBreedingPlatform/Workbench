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
import { finalize, map } from 'rxjs/operators';
import { ModalConfirmComponent } from '../shared/modal/modal-confirm.component';
import { TranslateService } from '@ngx-translate/core';
import { FILE_UPLOAD_SUPPORTED_TYPES, TINY_BLANK_IMAGE } from '../app.constants';
import { VariableDetails } from '../shared/ontology/model/variable-details';
import { FilterType } from '../shared/column-filter/column-filter.component';
import { Pageable } from '../shared/model/pageable';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html',
    styleUrls: ['./file-manager.scss']
})
export class FileManagerComponent {

    VARIABLE_TYPE_IDS = [VariableTypeEnum.TRAIT, VariableTypeEnum.SELECTION_METHOD]

    @ViewChild('fileUpload')
    fileUpload: ElementRef;
    file: File;
    variable: VariableDetails;
    @ViewChild('img')
    img: ElementRef;

    fileMetadataSelected: FileMetadata;
    fileMetadataList: FileMetadata[];
    imgToUploadUrlPreview;

    observationUnitUUID: string;
    datasetId: number;

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

    page = 1;
    pageSize = 5;
    totalCount: any;

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
        this.datasetId = Number(queryParamMap.get('datasetId'));
        this.filters.variable.value = queryParamMap.get('variableName');
        this.load();
    }

    load() {
        this.isLoading = true;
        this.fileService.listFileMetadata(
            this.observationUnitUUID,
            this.filters.variable.value,
            <Pageable>({
                page: this.page - 1,
                size: this.pageSize,
                sort: null
            }),
        ).pipe(
            finalize(() => this.isLoading = false),
            map((resp) => {
                this.totalCount = resp.headers.get('X-Total-Count')
                return resp;
            })
        ).subscribe((resp) => {
            this.fileMetadataSelected = null;
            this.imgToUploadUrlPreview = null;
            this.fileMetadataList = resp.body;
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

    close() {
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
        confirmModal.componentInstance.message = this.translateService.instant('fileManager.delete.confirm', { fileName: fileMetadata.name });
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

    validateFileNameDuplicated() {
        let filesObservations = [];
        if (this.variable && this.variable.id) {
            const variableId = Number(this.variable.id);
            filesObservations = this.fileMetadataList.filter((fileMetadata) => //
                fileMetadata.variables.length !== 0 && Number(fileMetadata.variables[0].id) === variableId);
        } else {
            filesObservations = this.fileMetadataList.filter((fileMetadata) => //
                fileMetadata.name === this.file.name && fileMetadata.variables.length === 0);
        }

        return filesObservations.filter((fileMetadata) => fileMetadata.name === this.file.name).length !== 0;
    }

    upload() {
        if (this.validateFileNameDuplicated()) {
            this.alertService.error('fileManager.duplicate.file.name.error');
            return false;
        }

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

    onImageLoad() {
        if (this.img.nativeElement.src !== TINY_BLANK_IMAGE) {
            this.isLoadingImage = false;
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
