import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { VariableDetails } from '../../shared/ontology/model/variable-details';
import { VariableService } from '../../shared/ontology/service/variable.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { TranslateService } from '@ngx-translate/core';
import { JhiAlertService } from 'ng-jhipster';
import { VariableDetailsContext } from './variable-details.context';
import { PopupService } from '../../shared/modal/popup.service';
import { InventoryDetailsModalComponent } from '../../germplasm-manager/inventory/details/inventory-details-modal.component';

@Component({
    selector: 'jhi-variable-details',
    templateUrl: './variable-details.component.html'
})
export class VariableDetailsComponent implements OnInit {

    variableId: number;
    title: String;
    isModal: boolean;

    constructor(private activeModal: NgbActiveModal,
                private variableService: VariableService,
                private route: ActivatedRoute,
                private translateService: TranslateService,
                private jhiAlertService: JhiAlertService,
                private variableDetailsContext: VariableDetailsContext) {

        this.route.queryParams.subscribe((value) => {
            this.variableId = value.variableId;
        });

        this.isModal = this.route.snapshot.queryParamMap.has('modal');
    }

    ngOnInit(): void {
        (<any>window).onCloseModal = this.dismiss;

        this.variableService.getVariableById(this.variableId).subscribe(
            (res: VariableDetails) => this.onSuccess(res),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(variableDetails: VariableDetails) {
        this.title = this.translateService.instant('ontology.variable-details.title',
            { name: variableDetails.name });
        this.variableDetailsContext.variableDetails.next(variableDetails);
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
        }
    }

    dismiss() {
        this.activeModal.dismiss();
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

}
