import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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
import { EXPT_DESIGN_CVTERM_ID, FILE_UPLOAD_SUPPORTED_TYPES, LOCATION_ID_CVTERM_ID, TINY_BLANK_IMAGE, TRIAL_INSTANCE_CVTERM_ID } from '../app.constants';
import { VariableDetails } from '../shared/ontology/model/variable-details';
import { FilterType } from '../shared/column-filter/column-filter.component';
import { Pageable } from '../shared/model/pageable';
import { VariableTypeEnum } from '../shared/ontology/variable-type.enum';
import { MANAGE_FILES_ENVIRONMENT_PERMISSION, MG_MANAGE_FILES_PERMISSION, MI_MANAGE_FILES_PERMISSION, MS_MANAGE_FILES_OBSERVATIONS_PERMISSION } from '../shared/auth/permissions';
import { Principal } from '../shared';

@Component({
    selector: 'jhi-file-manager',
    templateUrl: './file-manager.component.html',
    styleUrls: ['./file-manager.scss']
})
export class FileManagerComponent implements OnInit {

    manageFilesPermissions = [];

    VARIABLE_TYPE_IDS;

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
    instanceId: number;
    lotId: number;

    germplasmUUID: string;
    isLoading = false;
    isLoadingImage = false;
    embedded = false;
    acceptedFileTypes = (FILE_UPLOAD_SUPPORTED_TYPES || '').split(',').map((t) => '.' + t).join(',');
    excludedVariableIds = [TRIAL_INSTANCE_CVTERM_ID, EXPT_DESIGN_CVTERM_ID, LOCATION_ID_CVTERM_ID];
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
        private translateService: TranslateService,
        private principal: Principal,
    ) {
        this.context.readParams();
        const queryParamMap = this.route.snapshot.queryParamMap;
        this.embedded = Boolean(queryParamMap.get('embedded'));
        this.filters.variable.value = queryParamMap.get('variableName');

        this.observationUnitUUID = queryParamMap.get('observationUnitUUID');
        this.germplasmUUID = queryParamMap.get('germplasmUUID');
        this.instanceId = queryParamMap.get('instanceId') ? Number(queryParamMap.get('instanceId')) : null;
        this.lotId = queryParamMap.get('lotId') ? Number(queryParamMap.get('lotId')) : null;
        if (this.observationUnitUUID) {
            this.VARIABLE_TYPE_IDS = [VariableTypeEnum.TRAIT, VariableTypeEnum.SELECTION_METHOD];
            this.datasetId = Number(queryParamMap.get('datasetId'));
            this.manageFilesPermissions = MS_MANAGE_FILES_OBSERVATIONS_PERMISSION;
        } else if (this.germplasmUUID) {
            this.VARIABLE_TYPE_IDS = [VariableTypeEnum.GERMPLASM_ATTRIBUTE, VariableTypeEnum.GERMPLASM_PASSPORT];
            this.manageFilesPermissions = MG_MANAGE_FILES_PERMISSION;
        } else if (this.instanceId) {
            this.datasetId = Number(queryParamMap.get('datasetId'));
            this.VARIABLE_TYPE_IDS = [VariableTypeEnum.ENVIRONMENT_CONDITION, VariableTypeEnum.ENVIRONMENT_DETAIL];
            this.manageFilesPermissions = MANAGE_FILES_ENVIRONMENT_PERMISSION;
        } else if (this.lotId) {
            this.VARIABLE_TYPE_IDS = [VariableTypeEnum.INVENTORY_ATTRIBUTE];
            this.manageFilesPermissions = MI_MANAGE_FILES_PERMISSION;
        }

        this.load();
    }

    load() {
        this.isLoading = true;
        this.fileService.listFileMetadata(
            this.observationUnitUUID,
            this.germplasmUUID,
            this.filters.variable.value,
            this.instanceId,
            this.lotId,
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

    async ngOnInit() {
        // get user account to use hasAnyAuthority directive
        await this.principal.identity();
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

    upload() {
        this.isLoading = true;
        // upload file / save observation
        this.fileService.upload(
            this.file,
            this.observationUnitUUID,
            this.germplasmUUID,
            this.variable && this.variable.id || null,
            this.instanceId,
            this.lotId
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
