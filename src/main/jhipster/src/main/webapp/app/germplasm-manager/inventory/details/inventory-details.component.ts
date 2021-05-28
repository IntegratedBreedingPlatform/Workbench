import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../../shared/alert/format-error-list';
import { JhiAlertService, JhiLanguageService } from 'ng-jhipster';
import { ParamContext } from '../../../shared/service/param.context';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmDto } from '../../../shared/germplasm/model/germplasm.model';

@Component({
    selector: 'jhi-inventory-details',
    templateUrl: './inventory-details.component.html'
})
export class InventoryDetailsComponent implements OnInit {

    gid: number;
    title: String;
    isModal: boolean;

    constructor(private jhiLanguageService: JhiLanguageService,
                private activeModal: NgbActiveModal,
                private route: ActivatedRoute,
                private germplasmService: GermplasmService,
                private jhiAlertService: JhiAlertService,
                private paramContext: ParamContext,
                private translateService: TranslateService,
    ) {
        this.paramContext.readParams();

        this.route.queryParams.subscribe((value) => {
            this.gid = value.gid;
        });

        this.isModal = this.route.snapshot.queryParamMap.has('modal');
    }

    ngOnInit() {
        (<any>window).onCloseModal = this.cancel;

        this.germplasmService.getGermplasmById(this.gid).subscribe(
            (res: HttpResponse<GermplasmDto>) => this.onSuccess(res.body),
            (res: HttpErrorResponse) => this.onError(res)
        );
    }

    private onSuccess(data: GermplasmDto) {
        this.title = this.translateService.instant('inventory-details.title',
            { germplasmPreferredName: data.preferredName, gid: this.gid });
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
        if ((<any>window.parent).closeModal) {
            (<any>window.parent).closeModal();
        }
    }

}
