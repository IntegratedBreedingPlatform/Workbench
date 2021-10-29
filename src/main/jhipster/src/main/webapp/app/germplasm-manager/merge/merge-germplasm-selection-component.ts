import { Component, Input, OnInit } from '@angular/core';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Germplasm } from '../../entities/germplasm/germplasm.model';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';
import { GermplasmMergeRequest, MergeOptions, NonSelectedGermplasm } from '../../shared/germplasm/model/germplasm-merge-request.model';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { MergeGermplasmExistingLotsComponent } from './merge-germplasm-existing-lots.component';
import { MergeGermplasmReviewComponent } from './merge-germplasm-review.component';

@Component({
    selector: 'jhi-merge-germplasm-select',
    templateUrl: './merge-germplasm-selection.component.html',
})
export class MergeGermplasmSelectionComponent implements OnInit {

    @Input()
    gids: number[];

    isLoading: boolean;
    germplasmList: Germplasm[];
    request = new GermplasmSearchRequest();
    searchResultDbId: string;

    selectedGid: number;
    isTransferPassportData: boolean;
    isTransferAttributesData: boolean;
    isTransferNameTypesData: boolean;

    constructor(
        private alertService: AlertService,
        private modal: NgbActiveModal,
        private modalService: NgbModal,
        private germplasmService: GermplasmService
    ) {
    }

    ngOnInit(): void {
        this.request = new GermplasmSearchRequest();
        this.request.addedColumnsPropertyIds = ['PREFERRED NAME', 'HAS PROGENY', 'USED IN LOCKED STUDY', 'USED IN LOCKED LIST',
            'FGID', 'MGID'];
        this.request.gids = this.gids;

        this.isLoading = true;
        this.search(this.request).then((searchId) => {
            this.germplasmService.getSearchResults(
                { searchRequestId: searchId }
            ).pipe(finalize(() => {
                this.isLoading = false;
            })).subscribe(
                (res: HttpResponse<Germplasm[]>) => this.germplasmList = res.body,
                (res: HttpErrorResponse) => this.onError(res)
            );
        }, (error) => this.onError(error));

    }

    search(request: GermplasmSearchRequest): Promise<string> {
        return new Promise((resolve, reject) => {
            if (!this.searchResultDbId) {
                this.germplasmService.search(request).subscribe((response) => {
                    this.searchResultDbId = response;
                    resolve(this.searchResultDbId);
                }, (error) => reject(error));
            } else {
                resolve(this.searchResultDbId);
            }
        });
    }

    isSelected(germplasm: Germplasm) {
        return this.selectedGid === germplasm.gid;
    }

    toggleSelect(germplasm: Germplasm) {
        if (this.selectedGid !== germplasm.gid) {
            this.selectedGid = germplasm.gid;
        }
    }

    dismiss() {
        this.modal.dismiss();
    }

    confirm() {
        var nonSelectedGermplasmList = this.germplasmList.filter((germplasm) => this.selectedGid !== germplasm.gid);
        if (this.hasValidationError(nonSelectedGermplasmList)) {
            return;
        }
        const germplasmMergeRequest = new GermplasmMergeRequest();
        germplasmMergeRequest.mergeOptions = new MergeOptions();
        germplasmMergeRequest.mergeOptions.migrateAttributesData = this.isTransferAttributesData;
        germplasmMergeRequest.mergeOptions.migrateNameTypes = this.isTransferNameTypesData;
        germplasmMergeRequest.mergeOptions.migratePassportData = this.isTransferPassportData;
        germplasmMergeRequest.targetGermplasmId = this.selectedGid;
        germplasmMergeRequest.nonSelectedGermplasm = [];

        nonSelectedGermplasmList.forEach((germplasm) => {
            const nonSelectedGermplasm = new NonSelectedGermplasm();
            nonSelectedGermplasm.germplasm = germplasm;
            nonSelectedGermplasm.germplasmId = germplasm.gid;
            nonSelectedGermplasm.migrateLots = false;
            nonSelectedGermplasm.omit = false;
            germplasmMergeRequest.nonSelectedGermplasm.push(nonSelectedGermplasm);
        });

        const gidsWithLots = nonSelectedGermplasmList.filter((germplasm) => (germplasm.lotCount > 0)).map((germplasm) => germplasm.gid);

        if (gidsWithLots.length > 0) {
            const mergeGermplasmExistingLotsModal = this.modalService.open(MergeGermplasmExistingLotsComponent as Component, { windowClass: 'modal-autofit', backdrop: 'static' });
            mergeGermplasmExistingLotsModal.componentInstance.gidsWithLots = gidsWithLots;
            mergeGermplasmExistingLotsModal.componentInstance.germplasmMergeRequest = germplasmMergeRequest;
            this.modal.dismiss();
        } else {
            const mergeGermplasmReviewModal = this.modalService.open(MergeGermplasmReviewComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
            mergeGermplasmReviewModal.componentInstance.germplasmMergeRequest = germplasmMergeRequest;
            this.modal.dismiss();
        }
    }

    private hasValidationError(nonSelectedGermplasmList: Germplasm[]): boolean {
        const fixedGids = nonSelectedGermplasmList.filter((germplasm) => (germplasm.groupId !== 0)).map((germplasm) => germplasm.gid);
        if (fixedGids.length > 0) {
            this.alertService.error('merge-germplasm.non-selected-germplasm-fixed', { param: fixedGids.join() });
            return true;
        }
        const gidsWithProgeny = nonSelectedGermplasmList.filter((germplasm) => (germplasm.hasProgeny)).map((germplasm) => germplasm.gid);
        if (gidsWithProgeny.length > 0) {
            this.alertService.error('merge-germplasm.non-selected-germplasm-has-progeny', { param: this.getGidsToDisplay(gidsWithProgeny) });
            return true;
        }
        const gidsInLockedList = nonSelectedGermplasmList.filter((germplasm) => (germplasm.usedInLockedList)).map((germplasm) => germplasm.gid);
        if (gidsInLockedList.length > 0) {
            this.alertService.error('merge-germplasm.non-selected-germplasm-in-locked-list', { param: this.getGidsToDisplay(gidsInLockedList) });
            return true;
        }
        const gidsInLockedStudy = nonSelectedGermplasmList.filter((germplasm) => (germplasm.usedInLockedStudy)).map((germplasm) => germplasm.gid);
        if (gidsInLockedStudy.length > 0) {
            this.alertService.error('merge-germplasm.non-selected-germplasm-in-locked-study', { param: this.getGidsToDisplay(gidsInLockedStudy) });
            return true;
        }
        return false
    }

    private getGidsToDisplay(gids: number[]): string {
        return gids. length > 7 ? gids.slice(0, 7).join() + '...' : gids.join();
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
