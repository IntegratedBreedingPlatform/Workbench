import { Component, OnInit } from '@angular/core';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmDto, GermplasmName } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import { PopupService } from '../../shared/modal/popup.service';
import { Router } from '@angular/router';
import { GermplasmNameContext } from '../../entities/germplasm/name/germplasm-name.context';
import { Subscription } from 'rxjs';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EDIT_GERMPLASM_PERMISSION, GERMPLASM_AUDIT_PERMISSION } from '../../shared/auth/permissions';
import { UrlService } from '../../shared/service/url.service';
import { ParamContext } from '../../shared/service/param.context';
import { ScrollableTooltipDirective } from '../../shared/tooltip/scrollable-tooltip.directive';
import { TruncateWithEllipsisPipe } from '../../shared/util/truncate-with-ellipsis.pipe';

@Component({
    selector: 'jhi-basic-details-pane',
    templateUrl: './basic-details-pane.component.html'
})
export class BasicDetailsPaneComponent implements OnInit {

    GERMPLASM_AUDIT_PERMISSION = GERMPLASM_AUDIT_PERMISSION;

    EDIT_BASIC_DETAILS_PERMISSIONS = [...EDIT_GERMPLASM_PERMISSION, 'MODIFY_BASIC_DETAILS'];
    EDIT_BASIC_DETAILS_ACTIONS_PERMISSIONS = [...this.EDIT_BASIC_DETAILS_PERMISSIONS, ...GERMPLASM_AUDIT_PERMISSION];

    MODIFY_NAMES_PERMISSIONS = [...EDIT_GERMPLASM_PERMISSION, 'MODIFY_NAMES'];
    NAMES_ACTIONS_PERMISSIONS = [...this.MODIFY_NAMES_PERMISSIONS, ...GERMPLASM_AUDIT_PERMISSION];

    MAX_NAME_DISPLAY_SIZE = 30;

    eventSubscriber: Subscription;
    germplasm: GermplasmDto;
    geojson: any;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private germplasmService: GermplasmService,
                public germplasmDetailsContext: GermplasmDetailsContext,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                public urlService: UrlService,
                private modalService: NgbModal,
                private popupService: PopupService,
                private router: Router,
                private germplasmNameContext: GermplasmNameContext,
                public paramContext: ParamContext,
                private alertService: JhiAlertService) {
    }

    ngOnInit(): void {
        this.loadGermplasm();
        this.registerGermplasmNameChanged();
    }

    registerGermplasmNameChanged() {
        this.eventSubscriber = this.eventManager.subscribe('basicDetailsChanged', (event) => {
            this.loadGermplasm();
            this.germplasmDetailsContext.notifyGermplasmDetailChanges();
        });
    }

    loadGermplasm(): void {
        this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasm = value.body;
        })
    }

    editGermplasmBasicDetails(): void {
        this.germplasmDetailsContext.germplasm = this.germplasm;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-edit-basic-details' }, }], {
            queryParamsHandling: 'merge'
        });
    }

    editGermplasmName(germplasmName: GermplasmName): void {
        this.germplasmNameContext.germplasmName = germplasmName;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-name-dialog/' + this.germplasm.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    createGermplasmName(): void {
        this.router.navigate(['/', { outlets: { popup: 'germplasm-name-dialog/' + this.germplasm.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    deleteGermplasmName(germplasmName: GermplasmName): void {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        const truncateWithEllipsisPipe = new TruncateWithEllipsisPipe();
        confirmModalRef.componentInstance.message = this.translateService.instant('germplasm-name-modal.delete.warning',
            {param: truncateWithEllipsisPipe.transform(germplasmName.name, this.MAX_NAME_DISPLAY_SIZE)});

        confirmModalRef.result.then(() => {
            this.germplasmService.deleteGermplasmName(this.germplasm.gid, germplasmName.id).toPromise().then((result) => {
                this.alertService.success('germplasm-name-modal.delete.success');
                this.loadGermplasm();
                this.germplasmDetailsContext.notifyGermplasmDetailChanges();
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            });
        }, () => confirmModalRef.dismiss());
    }

    isStudyClickable(studyProgramUUID: string) {
        return this.paramContext.programUUID === studyProgramUUID;
    }

    openGermplasmNameAuditModal(germplasmName: GermplasmName): void {
        this.germplasmNameContext.germplasmName = germplasmName;
        this.router.navigate(['/', { outlets: { popup: `germplasm/${this.germplasm.gid}/names/${germplasmName.id}/audit-dialog`}, }], {
            queryParamsHandling: 'merge'
        });
    }

    openGermplasmBasicDetailsAuditModal(): void {
        this.router.navigate(['/', { outlets: { popup: `germplasm/${this.germplasm.gid}/basic-details/audit-dialog`}, }], {
            queryParamsHandling: 'merge'
        });
    }

}
