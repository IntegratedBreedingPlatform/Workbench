import { Component, HostListener, OnInit, QueryList, ViewChildren } from '@angular/core';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ScrollableTooltipDirective } from '../../shared/tooltip/scrollable-tooltip.directive';
import { LotAttribute } from '../../shared/inventory/model/lot.model';
import { LotService } from '../../shared/inventory/service/lot.service';
import { LotDetailContext } from './lot-detail.context';
import { VariableTypeEnum } from '../../shared/ontology/variable-type.enum';
import { LotAttributeContext } from './attribute/lot-attribute.context';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { GermplasmAttribute } from '../../shared/germplasm/model/germplasm.model';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { formatErrorList } from '../../shared/alert/format-error-list';


@Component({
    selector: 'jhi-lot-attributes-pane',
    templateUrl: './lot-attributes-pane.component.html'
})
export class LotAttributesPaneComponent implements OnInit {

    eventSubscriber: Subscription;
    attributes: LotAttribute[] = [];
    MAX_ATTRIBUTE_DISPLAY_SIZE = 30;

    variableByAttributeId: { [key: number]: VariableDetails } = {};

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private lotDetailContext: LotDetailContext,
                private lotService: LotService,
                private variableService: VariableService,
                private alertService: JhiAlertService,
                private modalService: NgbModal,
                private router: Router,
                private route: ActivatedRoute,
                private lotAttributeContext: LotAttributeContext
    ) {
    }

    ngOnInit(): void {
        this.route.queryParamMap.subscribe( (params) => {
            this.lotDetailContext.lotId = Number(params.get('lotId'));
        }) ;

        this.loadAttributes();
        this.registerLotAttributeChanged();
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
        const attributesByVariableId: { [key: number]: GermplasmAttribute } =
            [ ...this.attributes]
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

    async deleteLotAttribute(lotAttribute: LotAttribute) {
        const variableIds = [lotAttribute.variableId];

        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = this.translateService.instant('lot-attribute-modal.delete.warning', { param: lotAttribute.variableName });

        try {
            await confirmModalRef.result
        } catch (e) {
            return
        }

        try {
            const result = await this.lotService.deleteLotAttribute(this.lotDetailContext.lotId, lotAttribute.id).toPromise()
            this.alertService.success('lot-attribute-modal.delete.success');
            this.loadAttributes();
            this.lotDetailContext.notifyLotDetailChanges();
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
}
