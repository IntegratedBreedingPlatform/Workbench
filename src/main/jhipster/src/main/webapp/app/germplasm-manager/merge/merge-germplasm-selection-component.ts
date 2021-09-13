import { Component, Input, OnInit } from '@angular/core';
import { AlertService } from '../../shared/alert/alert.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Germplasm } from '../../entities/germplasm/germplasm.model';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmSearchRequest } from '../../entities/germplasm/germplasm-search-request.model';
import { finalize } from 'rxjs/internal/operators/finalize';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { SearchResult } from '../../shared/search-result.model';
import { formatErrorList } from '../../shared/alert/format-error-list';
import { GermplasmImportReviewComponent } from '../import/germplasm-import-review.component';

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
        this.request.addedColumnsPropertyIds = ['PREFERRED NAME','HAS PROGENY', 'USED IN STUDY', 'USED IN LOCKED LIST'];
        this.request.gids = this.gids;

        this.isLoading = true;
        this.search(this.request).then((searchId) => {
            this.germplasmService.getSearchResults(
                { searchRequestId: searchId}
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

    private onError(response: HttpErrorResponse) {
        const msg = formatErrorList(response.error.errors);
        if (msg) {
            this.alertService.error('error.custom', { param: msg });
        } else {
            this.alertService.error('error.general');
        }
        this.isLoading = false;
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
        const nonSelectedGids = this.gids.filter((gid) => this.selectedGid !== gid);
        if (this.hasValidationError(nonSelectedGids)) {
            return;
        }
        // TODO show lots selection if there are non-selected germplasm having lots
        var gidsWithLots = this.germplasmList.filter((germplasm) => (germplasm.lotCount > 0 && nonSelectedGids.includes(germplasm.gid)))
            .map((germplasm) => germplasm.gid);

        if (gidsWithLots.length > 0) {
            // TODO show lots selection if there are non-selected germplasm having lots
        } else {
            // TODO proceed with merge
        }

    }

    private hasValidationError(nonSelectedGids: number[]): boolean {
        var fixedGids = this.germplasmList.filter((germplasm) => (germplasm.groupId !== 0 && nonSelectedGids.includes(germplasm.gid)))
            .map((germplasm) => germplasm.gid);
        if (fixedGids.length > 0) {
            this.alertService.error('merge-germplasm.non-selected-germplasm-fixed', { param: fixedGids.join() });
            return true;
        }
        var gidsWithProgeny = this.germplasmList.filter((germplasm) => (germplasm.hasProgeny && nonSelectedGids.includes(germplasm.gid)))
            .map((germplasm) => germplasm.gid);
        if (gidsWithProgeny.length > 0) {
            this.alertService.error('merge-germplasm.non-selected-germplasm-has-progeny', { param: gidsWithProgeny.join() });
            return true;
        }
        var gidsInLockedList = this.germplasmList.filter((germplasm) => (germplasm.usedInLockedList && nonSelectedGids.includes(germplasm.gid)))
            .map((germplasm) => germplasm.gid);
        if (gidsInLockedList.length > 0) {
            this.alertService.error('merge-germplasm.non-selected-germplasm-in-locked-list', { param: gidsInLockedList.join() });
            return true;
        }
        return false
    }

}
