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
export class MergeGermplasmExistingLotsComponent implements OnInit {

    lotMergeOptionsEnum = LotMergeOptionsEnum;

    @Input()
    germplasmMergeRequest: GermplasmMergeRequest;
    gidsWithLots: number[];

    isLoading: boolean;
    lotsByGids: Map<number, Lot[]>;
    applyToAll: LotMergeOptionsEnum = LotMergeOptionsEnum.CLOSE;

    constructor(
        private lotService: LotService,
        private germplasmService: GermplasmService,
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private eventManager: JhiEventManager) {
    }

    ngOnInit(): void {
        const lotSearch = new LotSearch();
        lotSearch.gids = this.gidsWithLots.map(String);
        // Get all lot records from all germplasm with lots.
        this.loadLots(lotSearch);
    }

    search(request: LotSearch): Promise<string> {
        return new Promise((resolve, reject) => {
            this.lotService.search(request).subscribe((response) => {
                resolve(response);
            }, (error) => reject(error));
        });
    }

    loadLots(request: LotSearch) {
        this.isLoading = true;
        this.search(request).then((searchId) => {
            this.lotService.getSearchResults({
                searchRequestId: searchId,
                page: 0,
                size: 10000
            }).pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe((response) => {
                    this.lotsByGids = this.groupBy(response.body, (lot) => lot.gid);
                }
            );
        });
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
