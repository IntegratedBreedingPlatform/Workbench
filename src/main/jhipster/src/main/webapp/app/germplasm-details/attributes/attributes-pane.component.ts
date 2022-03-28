import { Component, OnInit } from '@angular/core';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmAttribute } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { Subscription } from 'rxjs';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { ActivatedRoute, Router } from '@angular/router';
import { GermplasmAttributeContext } from '../../entities/germplasm/attribute/germplasm-attribute.context';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EDIT_GERMPLASM_PERMISSION, GERMPLASM_AUDIT_PERMISSION, MG_MANAGE_FILES_PERMISSION } from '../../shared/auth/permissions';
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { VariableValidationService } from '../../shared/ontology/service/variable-validation.service';
import { FileService } from '../../shared/file/service/file.service';
import { FileDeleteOptionsComponent } from '../../shared/file/component/file-delete-options.component';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { Principal } from '../../shared';

@Component({
    selector: 'jhi-attributes-pane',
    templateUrl: './attributes-pane.component.html'
})
export class AttributesPaneComponent implements OnInit {

    static readonly MAX_ATTRIBUTE_DISPLAY_SIZE = 30;

    MODIFY_ATTRIBUTES_PERMISSIONS = [...EDIT_GERMPLASM_PERMISSION, 'MODIFY_ATTRIBUTES'];
    ATTRIBUTES_ACTIONS_PERMISSIONS = [...this.MODIFY_ATTRIBUTES_PERMISSIONS, ...GERMPLASM_AUDIT_PERMISSION];
    GERMPLASM_AUDIT_PERMISSION = GERMPLASM_AUDIT_PERMISSION;

    eventSubscriber: Subscription;
    passportAttributes: GermplasmAttribute[] = [];
    attributes: GermplasmAttribute[] = [];
    variableByAttributeId: { [key: number]: VariableDetails } = {};
    VariableTypeEnum = VariableTypeEnum;
    isFileStorageConfigured: boolean;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private germplasmService: GermplasmService,
                private variableService: VariableService,
                private variableValidationService: VariableValidationService,
                private alertService: JhiAlertService,
                private modalService: NgbModal,
                private router: Router,
                private route: ActivatedRoute,
                private germplasmAttributesContext: GermplasmAttributeContext,
                private fileService: FileService,
                private principal: Principal
    ) {
    }

    ngOnInit(): void {
        this.loadAttributes();
        this.registerGermplasmAttributeChanged();
        this.fileService.isFileStorageConfigured().then((isFileStorageConfigured) => this.isFileStorageConfigured = isFileStorageConfigured);
    }

    registerGermplasmAttributeChanged() {
        this.eventSubscriber = this.eventManager.subscribe('attributeChanged', (event) => {
            this.loadAttributes();
            this.germplasmDetailsContext.notifyGermplasmDetailChanges();
        });
    }

    async loadAttributes() {
        const gid = this.germplasmDetailsContext.gid;
        this.passportAttributes = await this.germplasmService.getGermplasmAttributesByGidAndType(gid, VariableTypeEnum.GERMPLASM_PASSPORT).toPromise();
        this.attributes = await this.germplasmService.getGermplasmAttributesByGidAndType(gid, VariableTypeEnum.GERMPLASM_ATTRIBUTE).toPromise();
        // Truncate long values
        this.passportAttributes.forEach((item) => item.displayValue = this.getAttributeDisplay(item.value));
        this.attributes.forEach((item) => item.displayValue = this.getAttributeDisplay(item.value));

        // Get extra info not available in the attribute entity (e.g valid values)
        const attributesByVariableId: {[key: number]: GermplasmAttribute} =
            [...this.passportAttributes, ...this.attributes]
                .reduce((prev: any, attribute) => (prev[attribute.variableId] = attribute, prev), {});
        this.variableByAttributeId = await this.variableService.filterVariables({ variableIds: Object.keys(attributesByVariableId) })
            .toPromise().then((variables) => {
                return variables.reduce((prev: any, variable) => {
                    prev[attributesByVariableId[Number(variable.id)].id] = variable;
                    return prev;
                }, {});
            });
    }

    getAttributeDisplay(value: string): string {
        return value.length <= AttributesPaneComponent.MAX_ATTRIBUTE_DISPLAY_SIZE ? value : value.substring(0, AttributesPaneComponent.MAX_ATTRIBUTE_DISPLAY_SIZE) + '...';
    }

    editGermplasmAttribute(attributeType: number, germplasmAttribute: GermplasmAttribute): void {
        this.germplasmAttributesContext.variable = this.variableByAttributeId[germplasmAttribute.id];
        this.germplasmAttributesContext.attributeType = attributeType;
        this.germplasmAttributesContext.attribute = germplasmAttribute;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-attribute-dialog/' + this.germplasmDetailsContext.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    createGermplasmAttribute(attributeType: number): void {
        this.germplasmAttributesContext.attributeType = attributeType;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-attribute-dialog/' + this.germplasmDetailsContext.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    async deleteGermplasmAttribute(germplasmAttribute: GermplasmAttribute) {
        const germplasmResp = await this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise();
        const germplasmUUID = germplasmResp.body.germplasmUUID;
        const variableIds = [germplasmAttribute.variableId];
        const fileCountResp = await this.fileService.getFileCount(variableIds, germplasmUUID).toPromise();
        const fileCount = Number(fileCountResp.headers.get('X-Total-Count'));
        if (fileCount > 0 && !await this.principal.hasAnyAuthority(MG_MANAGE_FILES_PERMISSION)) {
            this.alertService.error('germplasm-attribute-modal.delete.blocked.files')
            return;
        }

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-attribute-modal.delete.warning', { param: germplasmAttribute.variableName });

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
                    await this.fileService.removeFiles(variableIds, germplasmUUID).toPromise();
                } else {
                    await this.fileService.detachFiles(variableIds, germplasmUUID).toPromise();
                }
            }

            const result = await this.germplasmService.deleteGermplasmAttribute(this.germplasmDetailsContext.gid, germplasmAttribute.id).toPromise()
            this.alertService.success('germplasm-attribute-modal.delete.success');
            this.loadAttributes();
            this.germplasmDetailsContext.notifyGermplasmDetailChanges();
        } catch (response) {
            if (!response.error) {
                this.alertService.error('error.general.client');
                console.error(response);
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

    isValidValue(attribute: GermplasmAttribute) {
        const variable = this.variableByAttributeId[attribute.id];
        const validationStatus = this.variableValidationService.isValidValue(attribute.value, variable);
        return validationStatus.isValid && validationStatus.isInRange;
    }

    openGermplasmAttributeAuditChanges(attributeType: number, germplasmAttribute: GermplasmAttribute): void {
        this.germplasmAttributesContext.attributeType = attributeType;
        this.germplasmAttributesContext.attribute = germplasmAttribute;
        this.router.navigate(['/', { outlets: { popup: `germplasm/${this.germplasmDetailsContext.gid}/attributes/${germplasmAttribute.id}/audit-dialog`}, }], {
            queryParamsHandling: 'merge'
        });
    }

    gotoFiles(attribute: GermplasmAttribute) {
        this.router.navigate(['../files'], {
            relativeTo: this.route,
            queryParamsHandling: 'merge',
            queryParams: {
                variableName: attribute.variableName
            }
        });
    }

}
