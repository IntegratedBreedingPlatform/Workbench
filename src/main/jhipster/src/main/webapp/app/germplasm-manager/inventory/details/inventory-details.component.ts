import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Germplasm } from '../../../entities/germplasm/germplasm.model';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService } from 'ng-jhipster';
import { PopupService } from '../../../shared/modal/popup.service';
import { ParamContext } from '../../../shared/service/param.context';

@Component({
    encapsulation: ViewEncapsulation.None,
    selector: 'jhi-inventory-manager',
    templateUrl: './inventory-details.component.html',
    styleUrls: ['../../../../content/css/global-bs4.scss']
})
export class InventoryDetailsComponent implements OnInit {

    private gid: number;
    private germplasmPreferredName: String;

    constructor(private activeModal: NgbActiveModal,
                private route: ActivatedRoute,
                private germplasmService: GermplasmService,
                private jhiAlertService: JhiAlertService,
                private paramContext: ParamContext
    ) {
        this.paramContext.readParams();

        this.route.queryParams.subscribe((value) => {
            this.gid = value.gid;
        });
    }

    ngOnInit() {
        this.germplasmService.getGermplasmById(this.gid).subscribe(
            (res: HttpResponse<Germplasm>) => this.onSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data: Germplasm) {
        this.germplasmPreferredName = data.germplasmPeferredName;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.jhiAlertService.addAlert({ msg: 'error.custom', type: 'danger', toast: false, params: { param: msg } }, null);
        } else {
            this.jhiAlertService.addAlert({ msg: 'error.general', type: 'danger', toast: false }, null);
        }
    }

    cancel() {
        this.activeModal.dismiss();
        (<any>window.parent).closeModal();
    }

}

@Component({
    selector: 'jhi-inventory-details-popup',
    template: ''
})
export class InventoryDetailsPopupComponent implements OnInit {
    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }
    ngOnInit(): void {
        const modal = this.popupService.open(InventoryDetailsComponent as Component);
    }
}
