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
import { EDIT_GERMPLASM_PERMISSION, GERMPLASM_AUDIT_PERMISSION } from '../../shared/auth/permissions';
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { VariableValidationService } from '../../shared/ontology/service/variable-validation.service';

@Component({
    selector: 'jhi-attributes-pane',
    templateUrl: './attributes-pane.component.html'
})
export class AttributesPaneComponent implements OnInit {

    MODIFY_ATTRIBUTES_PERMISSIONS = [...EDIT_GERMPLASM_PERMISSION, 'MODIFY_ATTRIBUTES'];
    ATTRIBUTES_ACTIONS_PERMISSIONS = [...this.MODIFY_ATTRIBUTES_PERMISSIONS, ...GERMPLASM_AUDIT_PERMISSION];
    GERMPLASM_AUDIT_PERMISSION = GERMPLASM_AUDIT_PERMISSION;

    eventSubscriber: Subscription;
    passportAttributes: GermplasmAttribute[] = [];
    attributes: GermplasmAttribute[] = [];
    variableByAttributeId: { [key: number]: VariableDetails } = {};
    VariableTypeEnum = VariableTypeEnum;

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
                private germplasmAttributesContext: GermplasmAttributeContext
    ) {
    }

    ngOnInit(): void {
        this.loadAttributes();
        this.registerGermplasmAttributeChanged();
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

    deleteGermplasmAttribute(germplasmAttribute: GermplasmAttribute): void {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-attribute-modal.delete.warning', { param: germplasmAttribute.variableName });

        confirmModalRef.result.then(() => {
            this.germplasmService.deleteGermplasmAttribute(this.germplasmDetailsContext.gid, germplasmAttribute.id).toPromise().then((result) => {
                this.alertService.success('germplasm-attribute-modal.delete.success');
                this.loadAttributes();
                this.germplasmDetailsContext.notifyGermplasmDetailChanges();
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            });
        }, () => confirmModalRef.dismiss());
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
