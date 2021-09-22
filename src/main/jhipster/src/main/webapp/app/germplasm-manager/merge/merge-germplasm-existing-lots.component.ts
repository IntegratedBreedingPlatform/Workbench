import { Component, Input, OnInit } from '@angular/core';
import { LotSearch } from '../../shared/inventory/model/lot-search.model';
import { LotService } from '../../shared/inventory/service/lot.service';
import { finalize } from 'rxjs/operators';
import { Lot } from '../../shared/inventory/model/lot.model';
import { GermplasmMergeRequest, NonSelectedGermplasm } from '../../shared/germplasm/model/germplasm-merge-request.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { AlertService } from '../../shared/alert/alert.service';
import { JhiEventManager } from 'ng-jhipster';
import { HttpErrorResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';

@Component({
    selector: 'jhi-merge-germplasm-existing-lots',
    templateUrl: './merge-germplasm-existing-lots.component.html',
})
export class MergeGermplasmExistingLotsComponent {

    lotMergeOptionsEnum = LotMergeOptionsEnum;

    @Input()
    germplasmMergeRequest: GermplasmMergeRequest;
    gidsWithLots: number[];

    isLoading: boolean;
    applyToAll: LotMergeOptionsEnum = LotMergeOptionsEnum.CLOSE;

    constructor(
        private lotService: LotService,
        private germplasmService: GermplasmService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private eventManager: JhiEventManager) {
    }

    dismiss() {
        this.modal.dismiss();
    }

    groupBy(list, keyGetter) {
        const map: Map<number, Lot[]> = new Map();
        list.forEach((item) => {
            const key = keyGetter(item);
            const collection = map.get(key);
            if (!collection) {
                map.set(key, [item]);
            } else {
                collection.push(item);
            }
        });
        return map;
    }

    confirm() {
        if (!this.hasValidationError(this.germplasmMergeRequest.nonSelectedGermplasm)) {
            this.germplasmService.mergeGermplasm(this.germplasmMergeRequest).toPromise()
                .then(() => {
                    this.alertService.success('merge-germplasm.success')
                    this.modal.dismiss();
                    // Refresh the Germplasm Manager search germplasm table to reflect the changes made in germplasm.
                    this.eventManager.broadcast({ name: 'germplasmDetailsChanged' });
                }, (error) => {
                    this.onError(error)
                });
        }
    }

    private hasValidationError(nonSelectedGermplasm: NonSelectedGermplasm[]): boolean {
        const nonSelectedGermplasmToDelete = nonSelectedGermplasm.filter((o) => !o.omit);
        if (nonSelectedGermplasmToDelete.length <= 0) {
            this.alertService.error('merge-germplasm.no.germplasm.to.be.merged');
            return true;
        }
        return false;
    }

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
        this.isLoading = false;
    }
}

export enum LotMergeOptionsEnum {
    OMIT = 'omit',
    MIGRATE = 'migrate',
    CLOSE = 'close'
}
