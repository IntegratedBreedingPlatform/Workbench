import { Component, OnInit } from '@angular/core';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LotAttribute } from '../../shared/inventory/model/lot.model';
import { LotService } from '../../shared/inventory/service/lot.service';
import { LotDetailContext } from './lot-detail.context';
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { LotAttributeContext } from './attribute/lot-attribute.context';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { VariableValidationService } from '../../shared/ontology/service/variable-validation.service';
import { FileService } from '../../shared/file/service/file.service';
import { MI_MANAGE_FILES_PERMISSION } from '../../shared/auth/permissions';
import { Principal } from '../../shared';
import { FileDeleteOptionsComponent } from '../../shared/file/component/file-delete-options.component';

@Component({
    selector: 'jhi-lot-attributes-pane',
    templateUrl: './lot-attributes-pane.component.html'
})
export class LotAttributesPaneComponent implements OnInit {

    eventSubscriber: Subscription;
    attributes: LotAttribute[] = [];
    MAX_ATTRIBUTE_DISPLAY_SIZE = 30;
    isFileStorageConfigured: boolean;

    variableByAttributeId: { [key: number]: VariableDetails } = {};


    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private lotDetailContext: LotDetailContext,
                private lotService: LotService,
                private variableService: VariableService,
                private variableValidationService: VariableValidationService,
                private alertService: JhiAlertService,
                private modalService: NgbModal,
                private router: Router,
                private route: ActivatedRoute,
                private lotAttributeContext: LotAttributeContext,
                private fileService: FileService,
                private principal: Principal
    ) {
    }

    ngOnInit(): void {
        this.route.queryParamMap.subscribe((params) => {
            this.lotDetailContext.lotId = Number(params.get('lotId'));
        });

        this.loadAttributes();
        this.registerLotAttributeChanged();
        this.fileService.isFileStorageConfigured().then((isFileStorageConfigured) => this.isFileStorageConfigured = isFileStorageConfigured);
    }

    registerLotAttributeChanged() {
        this.eventSubscriber = this.eventManager.subscribe('attributeChanged', (event) => {
            this.loadAttributes();
            this.lotDetailContext.notifyLotDetailChanges();
        });
    }

    async loadAttributes() {
        const lotId = this.lotDetailContext.lotId;
        this.attributes = await this.lotService.getLotAttributes(lotId + '').toPromise();

        // Get extra info not available in the attribute entity (e.g valid values)
        const attributesByVariableId: { [key: number]: LotAttribute } =
            [...this.attributes]
                .reduce((prev: any, attribute) => (prev[attribute.variableId] = attribute, prev), {});
        this.variableByAttributeId = await this.variableService.filterVariables({ variableIds: Object.keys(attributesByVariableId) })
            .toPromise().then((variables) => {
                return variables.reduce((prev: any, variable) => {
                    prev[attributesByVariableId[Number(variable.id)].id] = variable;
                    return prev;
                }, {});
            });
    }

    createLotAttribute(): void {
        this.router.navigate(['/', { outlets: { popup: 'lot-attribute-dialog/' + this.lotDetailContext.lotId }, }], {
            queryParamsHandling: 'merge'
        });
    }

    editLotAttribute(lotAttribute: LotAttribute): void {
        this.lotAttributeContext.variable = this.variableByAttributeId[lotAttribute.id];
        this.lotAttributeContext.attributeType = VariableTypeEnum.INVENTORY_ATTRIBUTE;
        this.lotAttributeContext.attribute = lotAttribute;
        this.router.navigate(['/', { outlets: { popup: 'lot-attribute-dialog/' + this.lotDetailContext.lotId }, }], {
            queryParamsHandling: 'merge'
        });
    }

    isValidValue(attribute: LotAttribute) {
        const variable = this.variableByAttributeId[attribute.id];
        const validationStatus = this.variableValidationService.isValidValue(attribute.value, variable);
        return validationStatus.isValid && validationStatus.isInRange;
    }

    gotoFiles(attribute: LotAttribute) {
        if ((<any>window.parent)) {
            (<any>window.parent).postMessage({ name: 'navigate-to-files', 'value': attribute.variableName }, '*');
        }
    }

    async deleteLotAttribute(lotAttribute: LotAttribute) {
        const variableIds = [lotAttribute.variableId];
        const fileCountResp = await this.fileService.getFileCount(variableIds, null, this.lotDetailContext.lotId).toPromise();
        const fileCount = Number(fileCountResp.headers.get('X-Total-Count'));
        if (fileCount > 0 && !await this.principal.hasAnyAuthority(MI_MANAGE_FILES_PERMISSION)) {
            this.alertService.error('lot-attribute-modal.delete.blocked.files')
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('lot-attribute-modal.delete.warning', { param: lotAttribute.variableName });

        try {
            await confirmModalRef.result
        } catch (e) {
            return
        }

        try {
            if (fileCount > 0) {
                const fileOptionsModal = this.modalService.open(FileDeleteOptionsComponent as Component);
                fileOptionsModal.componentInstance.fileCount = fileCount;
                let doRemoveFiles;
                try {
                    doRemoveFiles = await fileOptionsModal.result;
                } catch (e) {
                    return;
                }
                if (doRemoveFiles) {
                    await this.fileService.removeFiles(variableIds, null, this.lotDetailContext.lotId).toPromise();
                } else {
                    await this.fileService.detachFiles(variableIds, null, this.lotDetailContext.lotId).toPromise();
                }
            }

            const result = await this.lotService.deleteLotAttribute(this.lotDetailContext.lotId, lotAttribute.id).toPromise()
            this.alertService.success('lot-attribute-modal.delete.success');
            this.loadAttributes();
            this.lotDetailContext.notifyLotDetailChanges();
        } catch (response) {
            if (!response.error) {
                this.alertService.error('error.general.client');
                return;
            }
            const msg = formatErrorList(response.error.errors);
            if (msg) {
                this.alertService.error('error.custom', { param: msg });
            } else {
                this.alertService.error('error.general');
            }
        }
    }
}
