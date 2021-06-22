import { Component, OnInit } from '@angular/core';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmAttribute } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { Subscription } from 'rxjs';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { Router } from '@angular/router';
import { GermplasmAttributeContext } from '../../entities/germplasm/attribute/germplasm-attribute.context';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EDIT_GERMPLASM_PERMISSION, GERMPLASM_AUDIT_PERMISSION } from '../../shared/auth/permissions';
import { GermplasmAttributeType } from '../../entities/germplasm/attribute/germplasm-attribute-type';

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

    GermplasmAttributeType = GermplasmAttributeType;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private germplasmDetailsContext: GermplasmDetailsContext,
                private germplasmService: GermplasmService,
                private alertService: JhiAlertService,
                private modalService: NgbModal,
                private router: Router,
                private germplasmAttributesContext: GermplasmAttributeContext) {
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

    loadAttributes(): void {
        this.germplasmService.getGermplasmAttributesByGidAndType(this.germplasmDetailsContext.gid, 'PASSPORT').toPromise().then((germplasmAttributes) => {
            this.passportAttributes = germplasmAttributes;
            return this.germplasmService.getGermplasmAttributesByGidAndType(this.germplasmDetailsContext.gid, 'ATTRIBUTE').toPromise();
        }).then((germplasmAttributes) => {
            this.attributes = germplasmAttributes;
        });
    }

    editGermplasmAttribute(attributeType: GermplasmAttributeType, germplasmAttribute: GermplasmAttribute): void {
        this.germplasmAttributesContext.attributeType = attributeType;
        this.germplasmAttributesContext.attribute = germplasmAttribute;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-attribute-dialog/' + this.germplasmDetailsContext.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    createGermplasmAttribute(attributeType: GermplasmAttributeType): void {
        this.germplasmAttributesContext.attributeType = attributeType;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-attribute-dialog/' + this.germplasmDetailsContext.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    deleteGermplasmAttribute(germplasmAttribute: GermplasmAttribute): void {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-attribute-modal.delete.warning', { param: germplasmAttribute.attributeCode });

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

    openGermplasmAttributeAuditChanges(attributeType: GermplasmAttributeType, germplasmAttribute: GermplasmAttribute): void {
        this.germplasmAttributesContext.attributeType = attributeType;
        this.germplasmAttributesContext.attribute = germplasmAttribute;
        this.router.navigate(['/', { outlets: { popup: `germplasm/${this.germplasmDetailsContext.gid}/attribute/${germplasmAttribute.id}/audit-dialog`}, }], {
            queryParamsHandling: 'merge'
        });
    }

}
