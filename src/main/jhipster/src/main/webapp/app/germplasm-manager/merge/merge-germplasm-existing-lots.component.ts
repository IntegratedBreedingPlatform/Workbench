import { Component, Input } from '@angular/core';
import { Lot } from '../../shared/inventory/model/lot.model';
import { GermplasmMergeRequest, NonSelectedGermplasm } from '../../shared/germplasm/model/germplasm-merge-request.model';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AlertService } from '../../shared/alert/alert.service';
import { MergeGermplasmReviewComponent } from './merge-germplasm-review.component';

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
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal) {
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
            const mergeGermplasmReviewModal = this.modalService.open(MergeGermplasmReviewComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
            mergeGermplasmReviewModal.componentInstance.germplasmMergeRequest = this.germplasmMergeRequest;
            this.modal.dismiss();
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

    applyToAllNonSelectedGermplasm() {
        this.germplasmMergeRequest.nonSelectedGermplasm.forEach((nonSelectedGermplasm) => {
            nonSelectedGermplasm.migrateLots = this.applyToAll === LotMergeOptionsEnum.MIGRATE;
            nonSelectedGermplasm.omit = this.applyToAll === LotMergeOptionsEnum.OMIT;
            }
        );
    }

}

export enum LotMergeOptionsEnum {
    OMIT = 'omit',
    MIGRATE = 'migrate',
    CLOSE = 'close'
}
